package org.rapidoid.scan;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.Str;
import org.rapidoid.config.RapidoidInitializer;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/*
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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
public class ClasspathUtil extends RapidoidInitializer {

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
		Enumeration<URL> urls = getResources(packageName);

		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			File file = new File(url.getFile());

			getFiles(files, file, filter);
		}
	}

	public static Enumeration<URL> getResources(String name) {
		name = name.replace('.', '/');
		try {
			return Cls.classLoader().getResources(name);
		} catch (IOException e) {
			throw U.rte("Cannot scan: " + name, e);
		}
	}

	private static void getFiles(Collection<File> files, File file, Predicate<File> filter) {
		if (file.isDirectory()) {
			Log.trace("scanning directory", "dir", file);
			for (File f : file.listFiles()) {
				if (f.isDirectory()) {
					getFiles(files, f, filter);
				} else {
					Log.trace("scanned file", "file", f);
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

			if (cl instanceof URLClassLoader) {
				URL[] urls = ((URLClassLoader) cl).getURLs();

				for (URL url : urls) {
					String path = Msc.urlDecode(Str.trimr(url.getPath(), '/'));
					CLASSPATH.add(new File(path).getAbsolutePath());
				}
			}

			if (U.isEmpty(appJar)) {
				inferAppJarFromClasspath();
			}
		}

		return CLASSPATH;
	}

	private static void inferAppJarFromClasspath() {
		for (String cp : CLASSPATH) {
			if (cp.endsWith("/app.jar") || cp.endsWith("\\app.jar")) {
				appJar = cp;
				return;
			}
		}
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
		return ClasspathScanner.scan(scanParams);
	}

	public static List<Class<?>> loadClasses(ScanParams scanParams) {
		List<String> classNames = ClasspathScanner.scan(scanParams);
		List<Class<?>> classes = U.list();

		ClassLoader classLoader = U.or(scanParams.classLoader(), defaultClassLoader);

		for (String clsName : classNames) {
			try {
				Log.trace("Loading class", "name", clsName);
				classes.add(classLoader != null ? Class.forName(clsName, true, classLoader) : Class.forName(clsName));
			} catch (Throwable e) {
				Log.debug("Error while loading class", "name", clsName, "error", e);
			}
		}

		return classes;
	}

	public static Class<?> loadFromJar(String clsName, String jar, ClassLoader classLoader) throws Exception {
		URL url = new File(jar).toURI().toURL();
		URLClassLoader child = new URLClassLoader(U.array(url), classLoader);
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
		if (U.neq(ClasspathUtil.appJar, appJar)) {
			ClasspathUtil.appJar = appJar;
			boolean exists = new File(appJar).exists();

			if (exists) {
				Log.info("Found application JAR", "!file", appJar);
			}
		}
	}

	public static Set<String> getClasspathStaticFolders() {
		Set<String> folders = U.set();

		for (String cp : ClasspathUtil.getClasspathFolders()) {

			File classes = new File(cp);
			if (classes.getName().equals("classes")) {

				File target = classes.getParentFile();
				if (target.getName().equals("target")) {

					String project = target.getParent();

					File static1 = new File(Msc.path(project, "src", "main", "resources", "static"));
					if (static1.exists() && static1.isDirectory()) {
						folders.add(static1.getAbsolutePath());
					}

					File static2 = new File(Msc.path(project, "static"));
					if (static2.exists() && static2.isDirectory()) {
						folders.add(static2.getAbsolutePath());
					}
				}
			}
		}

		return folders;
	}

}
