package org.rapidoid.scan;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.ctx.Classes;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.io.IO;
import org.rapidoid.lambda.Lambdas;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.log.Log;
import org.rapidoid.tuple.Tuple;
import org.rapidoid.util.U;

/*
 * #%L
 * rapidoid-scan
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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
public class Scan {

	private static final String[] SKIP_PKG = { "com", "org", "net", "io" };
	private static final Set<String> SKIP_PACKAGES = new HashSet<String>();
	private static final Map<String, Set<String>> SKIP_SUBPACKAGES = new HashMap<String, Set<String>>();

	private static final Set<String> CLASSPATH = new TreeSet<String>();

	private static final Map<Tuple, List<Class<?>>> CLASSES_CACHE = U.map();

	private Scan() {}

	static {
		SKIP_PACKAGES.addAll(IO.loadLines("scan-ignore.txt"));
		SKIP_PACKAGES.add("java");
		SKIP_PACKAGES.add("javax");
		SKIP_PACKAGES.add("META-INF");
		SKIP_PACKAGES.add("license");
		SKIP_PACKAGES.add("public");
		SKIP_PACKAGES.add("static");

		for (String pkg : SKIP_PKG) {
			SKIP_SUBPACKAGES.put(pkg, U.set(IO.loadLines(U.format("scan-ignore-%s.txt", pkg))));
		}

		SKIP_SUBPACKAGES.get("org").add("xml");
		SKIP_SUBPACKAGES.get("org").add("dom4j");
		SKIP_SUBPACKAGES.get("com").add("fasterxml");
	}

	public static synchronized void reset() {
		CLASSPATH.clear();
		CLASSES_CACHE.clear();
	}

	public static synchronized void args(String... args) {
		for (String arg : args) {
			if (arg.matches("\\+\\w+")) {
				addon(arg.substring(1));
			}
		}
	}

	private static Object addon(String addonName) {
		U.must(addonName.matches("\\w+"), "Invalid add-on name, must be alphanumeric!");

		String addonClassName = "org.rapidoid.addon." + U.capitalized(addonName) + "Addon";
		Class<?> addonCls = Cls.getClassIfExists(addonClassName);

		if (addonCls != null) {
			if (Callable.class.isAssignableFrom(addonCls)) {
				Callable<?> addon = (Callable<?>) Cls.newInstance(addonCls);
				try {
					Object addonResult = addon.call();
					Log.info("Executed add-on", "add-on", addonName, "add-on class", addonClassName, "result",
							addonResult);
					return addonResult;
				} catch (Exception e) {
					throw U.rte(e);
				}
			} else {
				Log.warn("Found add-on, but it's not a Runnable!", "add-on", addonName, "add-on class", addonClassName);
			}
		} else {
			Log.debug("No add-on was found", "add-on", addonName, "add-on class", addonClassName);
		}

		return null;
	}

	public static synchronized List<Class<?>> classes() {
		return classes(null, null, null, null, null);
	}

	public static synchronized List<Class<?>> classes(String packageName, String nameRegex, Predicate<Class<?>> filter,
			Class<? extends Annotation> annotated, ClassLoader classLoader) {
		return scanClasses(packageName, nameRegex, filter, annotated, classLoader);
	}

	public static synchronized List<Class<?>> annotated(Class<? extends Annotation> annotated) {
		return scanClasses(null, null, null, annotated, null);
	}

	public static synchronized List<Class<?>> annotated(Class<? extends Annotation> annotated, ClassLoader classLoader) {
		return scanClasses(null, null, null, annotated, classLoader);
	}

	public static synchronized List<Class<?>> pkg(String packageName) {
		return scanClasses(packageName, null, null, null, null);
	}

	public static synchronized List<Class<?>> byName(String simpleName, Predicate<Class<?>> filter,
			ClassLoader classLoader) {
		return scanClasses(null, "(.*\\.|^)" + simpleName, filter, null, classLoader);
	}

	public static synchronized List<Class<?>> bySuffix(String nameSuffix, Predicate<Class<?>> filter,
			ClassLoader classLoader) {
		return scanClasses(null, ".*\\w" + nameSuffix, filter, null, classLoader);
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

	private static List<Class<?>> scanClasses(String packageName, String nameRegex, Predicate<Class<?>> filter,
			Class<? extends Annotation> annotated, ClassLoader classLoader) {

		packageName = U.or(packageName, "");

		boolean caching = classLoader == null;
		Tuple cacheKey = null;

		if (caching) {
			cacheKey = new Tuple(packageName, nameRegex, filter, annotated, classLoader);
			List<Class<?>> classes = CLASSES_CACHE.get(cacheKey);
			if (classes != null) {
				return classes;
			}
		}

		List<Class<?>> classes;
		Classes ctxClasses = Ctx.classes();
		Pattern regex = nameRegex != null ? Pattern.compile(nameRegex) : null;

		if (ctxClasses != null) {
			classes = filterClasses(ctxClasses, packageName, regex, filter, annotated);
		} else {
			classes = retrieveClasses(packageName, filter, annotated, regex, classLoader);
		}

		if (caching) {
			CLASSES_CACHE.put(cacheKey, classes);
		}

		return classes;
	}

	private static List<Class<?>> retrieveClasses(String packageName, Predicate<Class<?>> filter,
			Class<? extends Annotation> annotated, Pattern regex, ClassLoader classLoader) {

		List<Class<?>> classes = new ArrayList<Class<?>>();

		String pkgName = U.or(packageName, "");

		Set<String> classpath = getClasspath();

		for (String cpe : classpath) {
			File file = new File(cpe);

			String path = file.getAbsolutePath();

			String pkgPath = pkgName.replace('.', File.separatorChar);
			String rootPath = pkgPath.isEmpty() ? path : path.replace(File.separatorChar + pkgPath, "");

			File root = new File(rootPath);

			if (root.exists()) {
				if (root.isDirectory()) {
					Log.debug("Scanning directory", "name", root.getAbsolutePath());
					getClassesFromDir(classes, root, file, regex, filter, annotated, classLoader);
				} else if (root.isFile() && root.getAbsolutePath().toLowerCase().endsWith(".jar")) {
					Log.debug("Scanning JAR", "name", root.getAbsolutePath());
					getClassesFromJAR(root.getAbsolutePath(), classes, packageName, regex, filter, annotated,
							classLoader);
				} else {
					Log.warn("Invalid classpath entry: " + cpe);
				}
			} else {
				Log.warn("Classpath entry doesn't exist: " + cpe);
			}
		}

		return classes;
	}

	private static List<Class<?>> filterClasses(Classes classes, String packageName, Pattern regex,
			Predicate<Class<?>> filter, Class<? extends Annotation> annotated) {

		List<Class<?>> matching = U.list();

		for (Entry<String, Class<?>> e : classes.entrySet()) {
			Class<?> cls = e.getValue();
			String pkg = cls.getPackage() != null ? cls.getPackage().getName() : "";

			if (packageName == null || pkg.startsWith(packageName + ".") || pkg.equals(packageName)) {
				if (classMatches(cls, filter, annotated, regex)) {
					matching.add(cls);
				}
			}
		}

		return matching;
	}

	private static void getClassesFromDir(Collection<Class<?>> classes, File root, File parent, Pattern regex,
			Predicate<Class<?>> filter, Class<? extends Annotation> annotated, ClassLoader classLoader) {

		if (parent.isDirectory()) {
			Log.debug("scanning directory", "dir", parent);
			for (File file : parent.listFiles()) {
				if (file.isDirectory()) {
					getClassesFromDir(classes, root, file, regex, filter, annotated, classLoader);
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
	}

	private static void scanFile(Collection<Class<?>> classes, Pattern regex, Predicate<Class<?>> filter,
			Class<? extends Annotation> annotated, ClassLoader classLoader, String relName) {
		Log.debug("scanned file", "file", relName);

		if (relName.endsWith(".class")) {

			String clsName = U.mid(relName, 0, -6).replace(File.separatorChar, '.');

			if (regex == null || regex.matcher(clsName).matches()) {
				try {
					Log.debug("loading class", "name", clsName);

					Class<?> cls = classLoader != null ? Class.forName(clsName, true, classLoader) : Class
							.forName(clsName);

					// regex match is tested before the class is loaded
					if (classMatches(cls, filter, annotated, null)) {
						classes.add(cls);
					}
				} catch (NoClassDefFoundError e1) {
					// do nothing
				} catch (Exception e) {
					throw U.rte(e);
				}
			}
		}
	}

	private static String pkgToPath(String pkg) {
		return pkg.replace('.', File.separatorChar);
	}

	private static boolean classMatches(Class<?> cls, Predicate<Class<?>> filter,
			Class<? extends Annotation> annotated, Pattern regex) {

		return (annotated == null || cls.getAnnotation(annotated) != null)
				&& (regex == null || regex.matcher(cls.getCanonicalName()).matches())
				&& (filter == null || Lambdas.eval(filter, cls));
	}

	private static Enumeration<URL> resources(String name) {
		name = name.replace('.', '/');
		try {
			return Cls.classLoader().getResources(name);
		} catch (IOException e) {
			throw U.rte("Cannot scan: " + name, e);
		}
	}

	private static List<Class<?>> getClassesFromJAR(String jarName, List<Class<?>> classes, String pkg, Pattern regex,
			Predicate<Class<?>> filter, Class<? extends Annotation> annotated, ClassLoader classLoader) {

		try {
			String pkgPath = pkgToPath(pkg);
			ZipInputStream zip = new ZipInputStream(new URL("file://" + jarName).openStream());

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
			throw U.rte(e);
		}

		return classes;
	}

	private static boolean ignore(String name) {
		String pkgName = U.triml(name, File.separatorChar);

		int p1 = pkgName.indexOf(File.separatorChar);
		int p2 = -1;

		if (p1 > 0) {
			String part1 = pkgName.substring(0, p1);
			if (SKIP_PACKAGES.contains(part1)) {
				return true;
			}

			p2 = pkgName.indexOf(File.separatorChar, p1 + 1);
			if (p2 > 0) {
				String part2 = pkgName.substring(p1 + 1, p2);
				if (U.isEmpty(part2)) {
					return true;
				}
				Set<String> subpkg = SKIP_SUBPACKAGES.get(part1);
				if (subpkg != null && subpkg.contains(part2)) {
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

}
