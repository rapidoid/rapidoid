package org.rapidoid.scan;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.lambda.Lmbd;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

/*
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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
public class ClasspathUtil {

	private static final Set<String> CLASSPATH = new TreeSet<String>();

	private static boolean ignoreRapidoidClasses = true;
	private static String rootPackage = null;

	private static final String ORG_RAPIDOID_DIR = "org" + File.separatorChar + "rapidoid" + File.separatorChar;
	private static final String ORG_RAPIDOIDX_DIR = "org" + File.separatorChar + "rapidoidx" + File.separatorChar;

	private ClasspathUtil() {}

	public static synchronized void reset() {
		CLASSPATH.clear();
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

	public static List<Class<?>> scanClasses(ScanParams params) {

		String packageName = U.safe(U.or(params.pkg(), rootPackage));

		long startingAt = U.time();

		String regex = params.matching();
		Pattern pattern = regex != null ? Pattern.compile(regex) : null;

		Log.info("Retrieving classes", "annotated", params.annotated(), "package", packageName, "matching", regex);

		List<Class<?>> classes = retrieveClasses(packageName, params.filter(), params.annotated(), pattern,
				params.classLoader());

		long timeMs = U.time() - startingAt;
		Log.info("Finished classpath scan", "time", timeMs + "ms", "classes", classes);

		return classes;
	}

	private static List<Class<?>> retrieveClasses(String packageName, Predicate<Class<?>> filter,
			Class<? extends Annotation> annotated, Pattern regex, ClassLoader classLoader) {

		List<Class<?>> classes = new ArrayList<Class<?>>();

		String pkgName = U.safe(packageName);
		String pkgPath = pkgName.replace('.', File.separatorChar);

		Set<String> classpath = getClasspath();

		Log.info("Scanning classpath", "package", packageName, "annotated", annotated, "regex", regex, "filter",
				filter, "loader", classLoader);

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
							getClassesFromDir(classes, cpEntry, startingDir, pkgName, regex, filter, annotated,
									classLoader);
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
				getClassesFromJAR(jarName, classes, packageName, regex, filter, annotated, classLoader);
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
		File file = new File(jar);
		String simpleName = file.getName();
		return simpleName.startsWith("app.") || simpleName.startsWith("app-");
	}

	private static void getClassesFromDir(Collection<Class<?>> classes, File root, File dir, String pkg, Pattern regex,
			Predicate<Class<?>> filter, Class<? extends Annotation> annotated, ClassLoader classLoader) {
		U.must(dir.isDirectory());
		Log.debug("Traversing directory", "root", root, "dir", dir);

		File[] files = dir.listFiles();
		if (files == null) {
			Log.warn("Not a folder!", "dir", dir);
			return;
		}

		for (File file : files) {
			if (file.isDirectory()) {
				getClassesFromDir(classes, root, file, pkg, regex, filter, annotated, classLoader);
			} else {
				String rootPath = U.trimr(root.getAbsolutePath(), File.separatorChar);
				int from = rootPath.length() + 1;
				String relName = file.getAbsolutePath().substring(from);

				if (!ignore(relName)) {
					scanFile(classes, regex, filter, annotated, classLoader, relName);
				}
			}
		}
	}

	private static void scanFile(Collection<Class<?>> classes, Pattern regex, Predicate<Class<?>> filter,
			Class<? extends Annotation> annotated, ClassLoader classLoader, String relName) {
		Log.debug("scanned file", "file", relName);

		if (relName.endsWith(".class")) {

			String clsName = U.mid(relName, 0, -6).replace('/', '.').replace('\\', '.');

			if (regex == null || regex.matcher(clsName).matches()) {
				try {
					Log.debug("loading class", "name", clsName);

					Class<?> cls = classLoader != null ? Class.forName(clsName, true, classLoader) : Class
							.forName(clsName);

					// regex match was tested before the class was loaded
					if (classMatches(cls, filter, annotated, null)) {
						classes.add(cls);
					}
				} catch (Throwable e) {
					Log.warn("Error while loading class", "name", clsName, "error", e);
				}
			}
		}
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

	public static List<Class<?>> getClassesFromJAR(String jarName, List<Class<?>> classes, String pkg, Pattern regex,
			Predicate<Class<?>> filter, Class<? extends Annotation> annotated, ClassLoader classLoader) {

		ZipInputStream zip = null;
		try {
			String pkgPath = pkgToPath(pkg);
			File jarFile = new File(jarName);
			FileInputStream jarInputStream = new FileInputStream(jarFile);
			zip = new ZipInputStream(jarInputStream);

			ZipEntry e;
			while ((e = zip.getNextEntry()) != null) {
				if (!e.isDirectory()) {
					String name = e.getName();

					if (!ignore(name)) {
						if (U.isEmpty(pkg) || name.startsWith(pkgPath)) {
							scanFile(classes, regex, filter, annotated, classLoader, name);
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
		String pkgDirName = U.triml(name, File.separatorChar);

		if (ignoreRapidoidClasses) {
			if (pkgDirName.startsWith(ORG_RAPIDOID_DIR) || pkgDirName.startsWith(ORG_RAPIDOIDX_DIR)) {
				return true;
			}
		}

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
					cpe = U.trimr(cpe, '/');
					CLASSPATH.add(new File(cpe).getAbsolutePath());
				}
			}

			ClassLoader cl = ClassLoader.getSystemClassLoader();

			URL[] urls = ((URLClassLoader) cl).getURLs();

			for (URL url : urls) {
				String path = U.trimr(url.getPath(), '/');
				CLASSPATH.add(new File(path).getAbsolutePath());
			}
		}

		return CLASSPATH;
	}

	public static boolean classMatches(Class<?> cls, Predicate<Class<?>> filter, Class<? extends Annotation> annotated,
			Pattern regex) {

		return (annotated == null || cls.getAnnotation(annotated) != null)
				&& (regex == null || (cls.getCanonicalName() != null && regex.matcher(cls.getCanonicalName()).matches()))
				&& (filter == null || Lmbd.eval(filter, cls));
	}

	public static void setIgnoreRapidoidClasses(boolean ignoreRapidoidClasses) {
		ClasspathUtil.ignoreRapidoidClasses = ignoreRapidoidClasses;
	}

	public static String getRootPackage() {
		return rootPackage;
	}

	public static void setRootPackage(String rootPackage) {
		ClasspathUtil.rootPackage = rootPackage;
	}

	public static List<Class<?>> getClasses(ScanParams scanParams) {
		return scanClasses(scanParams);
	}

}
