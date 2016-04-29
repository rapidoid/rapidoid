package org.rapidoid.scan;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.Str;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import java.io.*;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/*
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class ClasspathUtil extends RapidoidThing {

	private static final Set<String> CLASSPATH = new TreeSet<String>();

	private static volatile String appJar;

	private static volatile String rootPackage = null;

	private static volatile ClassLoader defaultClassLoader = ClasspathUtil.class.getClassLoader();

	private ClasspathUtil() {
	}

	public static synchronized void reset() {
		CLASSPATH.clear();
		appJar = null;
	}

	public static synchronized List<File> files(String packageName, Predicate<File> filter) {
		ArrayList<File> files = new ArrayList<File>();

		files(packageName, files, filter);

		return files;
	}

	public static synchronized List<File> dir(String dir, Predicate<File> filter) {
		ArrayList<File> files = new ArrayList<File>();

		getFiles(files, new File(dir), filter);

		return files;
	}

	public static synchronized void files(String packageName, Collection<File> files, Predicate<File> filter) {
		Enumeration<URL> urls = resources(packageName);

		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			File file = new File(url.getFile());

			getFiles(files, file, filter);
		}
	}

	private static void getFiles(Collection<File> files, File file, Predicate<File> filter) {
		if (file.isDirectory()) {
			Log.debug("scanning directory", "dir", file);
			for (File f : file.listFiles()) {
				if (f.isDirectory()) {
					getFiles(files, f, filter);
				} else {
					Log.debug("scanned file", "file", f);
					try {
						if (filter == null || filter.eval(f)) {
							files.add(f);
						}
					} catch (Exception e) {
						throw U.rte(e);
					}
				}
			}
		}
	}

	public static List<String> scanClasses(ScanParams params) {
		String[] pkgs = params.in();

		if (U.isEmpty(pkgs)) {
			pkgs = new String[]{rootPackage};
		}

		long startingAt = U.time();

		String regex = params.matching();
		Pattern pattern = regex != null ? Pattern.compile(regex) : null;

		Log.info("Scanning classpath", "annotated", Arrays.toString(params.annotated()), "packages", Arrays.toString(pkgs), "matching", regex);

		AtomicInteger searched = new AtomicInteger();
		List<String> classes = U.list();

		for (int i = 0; i < pkgs.length; i++) {
			String pkg = pkgs[i];
			classes.addAll(retrieveClasses(pkg, params.annotated(), pattern, params.classLoader(), searched));
		}

		long timeMs = U.time() - startingAt;
		Log.info("Finished classpath scan", "time", timeMs + "ms", "searched", searched.get(), "found", classes.size());

		return classes;
	}

	private static List<String> retrieveClasses(String packageName, Class<? extends Annotation>[] annotated,
	                                            Pattern regex, ClassLoader classLoader, AtomicInteger searched) {

		List<String> classes = U.list();

		String pkgName = U.safe(packageName);
		String pkgPath = pkgName.replace('.', File.separatorChar);

		Set<String> classpath = getClasspath();

		Log.debug("Starting classpath scan", "package", packageName, "annotated", annotated, "regex", regex, "loader", classLoader);

		Log.debug("Classpath details", "classpath", classpath);

		Set<String> jars = U.set();

		for (String cpe : classpath) {
			File cpEntry = new File(cpe);

			if (cpEntry.exists()) {
				if (cpEntry.isDirectory()) {
					if (shouldScanDir(cpEntry.getAbsolutePath())) {
						Log.debug("Scanning directory", "root", cpEntry.getAbsolutePath());

						File startingDir;
						if (pkgPath.isEmpty()) {
							startingDir = cpEntry;
						} else {
							startingDir = new File(cpEntry.getAbsolutePath(), pkgPath);
						}

						if (startingDir.exists()) {
							getClassesFromDir(classes, cpEntry, startingDir, pkgName, regex, annotated,
									classLoader, searched);
						}
					} else {
						Log.debug("Skipping directory", "root", cpEntry.getAbsolutePath());
					}
				} else if (cpEntry.isFile() && cpEntry.getAbsolutePath().toLowerCase().endsWith(".jar")) {
					jars.add(cpEntry.getAbsolutePath());
				} else {
					Log.warn("Invalid classpath entry: " + cpe);
				}
			} else {
				Log.warn("Classpath entry doesn't exist: " + cpe);
			}
		}

		for (String jarName : jars) {
			if (shouldScanJAR(jarName)) {
				Log.debug("Scanning JAR", "name", jarName);
				getClassesFromJAR(jarName, classes, packageName, regex, annotated, classLoader, searched);
			} else {
				Log.debug("Skipping JAR", "name", jarName);
			}
		}

		return classes;
	}

	private static boolean shouldScanDir(String dir) {
		return true;
	}

	private static boolean shouldScanJAR(String jar) {
		return true;
	}

	private static void getClassesFromDir(Collection<String> classes, File root, File dir, String pkg, Pattern regex,
	                                      Class<? extends Annotation>[] annotated, ClassLoader classLoader, AtomicInteger searched) {

		U.must(dir.isDirectory());
		Log.debug("Traversing directory", "root", root, "dir", dir);

		File[] files = dir.listFiles();
		if (files == null) {
			Log.warn("Not a folder!", "dir", dir);
			return;
		}

		for (File file : files) {
			if (file.isDirectory()) {
				getClassesFromDir(classes, root, file, pkg, regex, annotated, classLoader, searched);
			} else {
				String rootPath = Str.trimr(root.getAbsolutePath(), File.separatorChar);
				int from = rootPath.length() + 1;
				String relName = file.getAbsolutePath().substring(from);

				if (!ignore(relName)) {
					scanFile(classes, regex, annotated, classLoader, relName, file, null, null, searched);
				}
			}
		}
	}

	private static void scanFile(Collection<String> classes, Pattern regex,
	                             Class<? extends Annotation>[] annotated, ClassLoader classLoader, String relName,
	                             File file, ZipFile zip, ZipEntry entry, AtomicInteger searched) {

		Log.debug("scanned file", "file", relName);

		if (relName.endsWith(".class")) {
			searched.incrementAndGet();

			String clsName = Str.sub(relName, 0, -6).replace('/', '.').replace('\\', '.');

			if (U.isEmpty(regex) || regex.matcher(clsName).matches()) {
				try {

					InputStream input = file != null ? new FileInputStream(file) : zip.getInputStream(entry);
					ClassFile classFile = new ClassFile(new DataInputStream(input));

					if (U.isEmpty(annotated) || isAnnotated(classFile, annotated)) {
						classes.add(clsName);
					}

				} catch (Throwable e) {
					Log.debug("Error while loading class", "name", clsName, "error", e);
				}
			}
		}
	}

	private static boolean isAnnotated(ClassFile cfile, Class<? extends Annotation>[] annotated) throws IOException {
		List attributes = U.safe(cfile.getAttributes());

		for (Object attribute : attributes) {
			if (attribute instanceof AnnotationsAttribute) {
				AnnotationsAttribute annotations = (AnnotationsAttribute) attribute;

				for (Class<? extends Annotation> ann : annotated) {
					if (annotations.getAnnotation(ann.getName()) != null) {
						return true;
					}
				}
			}
		}

		return false;
	}

	private static String pkgToPath(String pkg) {
		return pkg != null ? pkg.replace('.', File.separatorChar) : null;
	}

	private static Enumeration<URL> resources(String name) {
		name = name.replace('.', '/');
		try {
			return Cls.classLoader().getResources(name);
		} catch (IOException e) {
			throw U.rte("Cannot scan: " + name, e);
		}
	}

	public static List<String> getClassesFromJAR(String jarName, List<String> classes, String pkg, Pattern regex,
	                                             Class<? extends Annotation>[] annotated, ClassLoader classLoader,
	                                             AtomicInteger searched) {

		// ZipInputStream zip = null;
		ZipFile zip = null;

		try {
			String pkgPath = pkgToPath(pkg);
			zip = new ZipFile(new File(jarName));
			Enumeration<? extends ZipEntry> entries = zip.entries();

			while (entries.hasMoreElements()) {
				ZipEntry zipEntry = entries.nextElement();
				if (!zipEntry.isDirectory()) {
					String name = zipEntry.getName();

					if (!ignore(name)) {
						if (U.isEmpty(pkg) || name.startsWith(pkgPath)) {
							scanFile(classes, regex, annotated, classLoader, name, null, zip, zipEntry, searched);
						}
					}
				}
			}
		} catch (Exception e) {
			Log.error("Cannot scan JAR: " + jarName, e);
		} finally {
			if (zip != null) {
				try {
					zip.close();
				} catch (IOException e) {
					Log.error("Couldn't close the ZIP stream!", e);
				}
			}
		}

		return classes;
	}

	private static boolean ignore(String name) {
		String pkgDirName = Str.triml(name, File.separatorChar);

		int p1 = pkgDirName.indexOf(File.separatorChar);
		int p2 = -1;

		if (p1 > 0) {
			p2 = pkgDirName.indexOf(File.separatorChar, p1 + 1);
			if (p2 > 0) {
				String part2 = pkgDirName.substring(p1 + 1, p2);
				if (U.isEmpty(part2)) {
					return true;
				}
			}
		}

		return false;
	}

	public static synchronized Set<String> getClasspath() {
		if (CLASSPATH.isEmpty()) {

			String classpathProp = System.getProperty("java.class.path");
			if (classpathProp != null) {
				String[] classpathEntries = classpathProp.split(File.pathSeparator);
				for (String cpe : classpathEntries) {
					cpe = Str.trimr(cpe, '/');
					CLASSPATH.add(new File(cpe).getAbsolutePath());
				}
			}

			ClassLoader cl = ClassLoader.getSystemClassLoader();

			URL[] urls = ((URLClassLoader) cl).getURLs();

			for (URL url : urls) {
				String path = Str.trimr(url.getPath(), '/');
				CLASSPATH.add(new File(path).getAbsolutePath());
			}

			for (String cp : CLASSPATH) {
				if (cp.endsWith("/app.jar") || cp.endsWith("\\app.jar")) {
					appJar = cp;
				}
			}
		}

		return CLASSPATH;
	}

	public static synchronized Set<String> getClasspathFolders() {
		Set<String> folders = U.set();

		Set<String> cps = ClasspathUtil.getClasspath();

		for (String cp : cps) {
			if (new File(cp).isDirectory()) {
				folders.add(cp);
			}
		}

		return folders;
	}

	public static synchronized Set<String> getClasspathJars() {
		Set<String> jars = U.set();

		Set<String> cps = ClasspathUtil.getClasspath();

		for (String cp : cps) {
			if (new File(cp).isFile() && cp.substring(cp.length() - 4).equalsIgnoreCase(".JAR")) {
				jars.add(cp);
			}
		}

		return jars;
	}

	public static String getRootPackage() {
		return rootPackage;
	}

	public static void setRootPackage(String rootPackage) {
		ClasspathUtil.rootPackage = rootPackage;
	}

	public static ClassLoader getDefaultClassLoader() {
		return defaultClassLoader;
	}

	public static void setDefaultClassLoader(ClassLoader defaultClassLoader) {
		ClasspathUtil.defaultClassLoader = defaultClassLoader;
	}

	public static List<String> getClasses(ScanParams scanParams) {
		return scanClasses(scanParams);
	}

	public static List<Class<?>> loadClasses(ScanParams scanParams) {
		List<String> classNames = scanClasses(scanParams);
		List<Class<?>> classes = U.list();

		ClassLoader classLoader = U.or(scanParams.classLoader(), defaultClassLoader);

		for (String clsName : classNames) {
			try {
				Log.debug("Loading class", "name", clsName);
				classes.add(classLoader != null ? Class.forName(clsName, true, classLoader) : Class.forName(clsName));
			} catch (Throwable e) {
				Log.debug("Error while loading class", "name", clsName, "error", e);
			}
		}

		return classes;
	}

	public static Class<?> loadFromJar(String clsName, String jar, ClassLoader classLoader) throws Exception {
		URL url = new File(jar).toURI().toURL();
		URLClassLoader child = new URLClassLoader(new URL[]{url}, classLoader);
		return Class.forName(clsName, true, child);
	}

	public static boolean hasAppJar() {
		getClasspath(); // init
		return appJar != null;
	}

	public static String appJar() {
		return appJar;
	}

	public static void appJar(String appJar) {
		ClasspathUtil.appJar = appJar;
	}
}
