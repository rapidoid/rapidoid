package org.rapidoid.util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import org.rapidoid.lambda.Predicate;
import org.rapidoid.log.Log;

/*
 * #%L
 * rapidoid-utils
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

public class Scan {

	private Scan() {
	}

	public static synchronized void args(String... args) {
		for (String arg : args) {
			if (arg.matches("\\+\\w+")) {
				addon(arg.substring(1));
			}
		}
	}

	public static Object addon(String addonName) {
		U.must(addonName.matches("\\w+"), "Invalid add-on name, must be alphanumeric!");

		String addonClassName = "org.rapidoid.addon." + U.capitalized(addonName) + "Addon";
		Class<?> addonCls = U.getClassIfExists(addonClassName);

		if (addonCls != null) {
			if (Callable.class.isAssignableFrom(addonCls)) {
				Callable<?> addon = (Callable<?>) U.newInstance(addonCls);
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

	public static List<Class<?>> classpathClasses(String packageName, String nameRegex, Predicate<Class<?>> filter,
			Class<? extends Annotation> annotated, ClassLoader classLoader) {

		packageName = U.or(packageName, "");

		Pattern regex = nameRegex != null ? Pattern.compile(nameRegex) : null;

		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		Enumeration<URL> urls = resources(packageName);

		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			File file = new File(url.getFile());

			String path = file.getAbsolutePath();

			String pkgPath = packageName.replace('.', File.separatorChar);
			String rootPath = pkgPath.isEmpty() ? path : path.replace(File.separatorChar + pkgPath, "");

			File root = new File(rootPath);
			U.must(root.exists());
			U.must(root.isDirectory());

			getClasses(classes, root, file, regex, filter, annotated, classLoader);
		}

		return classes;
	}

	public static List<Class<?>> annotated(Class<? extends Annotation> annotated) {
		return classpathClasses(null, null, null, annotated, null);
	}

	public static List<Class<?>> classpathClassesByName(String simpleName, Predicate<Class<?>> filter,
			ClassLoader classLoader) {
		List<Class<?>> classes = classpathClasses("*", ".*\\." + simpleName, filter, null, classLoader);

		if (classes.isEmpty()) {
			Log.warn("No classes found on classpath with the specified simple name", "name", simpleName);
		}

		return classes;
	}

	public static List<Class<?>> classpathClassesBySuffix(String nameSuffix, Predicate<Class<?>> filter,
			ClassLoader classLoader) {
		List<Class<?>> classes = classpathClasses("*", ".+\\w" + nameSuffix, filter, null, classLoader);

		if (classes.isEmpty()) {
			Log.warn("No classes found on classpath with the specified suffix", "suffix", nameSuffix);
		}

		return classes;
	}

	public static List<File> classpath(String packageName, Predicate<File> filter) {
		ArrayList<File> files = new ArrayList<File>();

		classpath(packageName, files, filter);

		return files;
	}

	public static void classpath(String packageName, Collection<File> files, Predicate<File> filter) {
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

	private static void getClasses(Collection<Class<?>> classes, File root, File parent, Pattern regex,
			Predicate<Class<?>> filter, Class<? extends Annotation> annotated, ClassLoader classLoader) {

		if (parent.isDirectory()) {
			Log.debug("scanning directory", "dir", parent);
			for (File f : parent.listFiles()) {
				if (f.isDirectory()) {
					getClasses(classes, root, f, regex, filter, annotated, classLoader);
				} else {
					Log.debug("scanned file", "file", f);
					if (f.getName().endsWith(".class")) {
						String clsName = f.getAbsolutePath();
						String rootPath = root.getAbsolutePath();
						U.must(clsName.startsWith(rootPath));

						clsName = clsName.substring(rootPath.length() + 1, clsName.length() - 6);
						clsName = clsName.replace(File.separatorChar, '.');

						if (regex == null || regex.matcher(clsName).matches()) {
							try {
								Log.debug("loading class", "name", clsName);

								Class<?> cls = classLoader != null ? Class.forName(clsName, true, classLoader) : Class
										.forName(clsName);

								if (annotated == null || cls.getAnnotation(annotated) != null) {
									if (filter == null || filter.eval(cls)) {
										classes.add(cls);
									}
								}
							} catch (Exception e) {
								throw U.rte(e);
							}
						}
					}
				}
			}
		}
	}

	private static Enumeration<URL> resources(String name) {

		name = name.replace('.', '/');

		if (name.equals("*")) {
			name = "";
		}

		try {
			return U.classLoader().getResources(name);
		} catch (IOException e) {
			throw U.rte("Cannot scan: " + name, e);
		}
	}

}
