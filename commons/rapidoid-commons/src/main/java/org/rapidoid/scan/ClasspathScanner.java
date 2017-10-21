package org.rapidoid.scan;

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

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.commons.Str;
import org.rapidoid.lambda.Lmbd;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.io.*;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class ClasspathScanner extends RapidoidThing {

	private static Map<ScanParams, List<String>> cache = Coll.autoExpandingMap(new Mapper<ScanParams, List<String>>() {
		@Override
		public List<String> map(ScanParams params) throws Exception {
			return scanClasses(params); // FIXME equality of 'params'
		}
	});

	public static synchronized void reset() {
		cache.clear();
	}

	public static List<String> scan(ScanParams params) {
		return scanClasses(params);
	}

	private static List<String> scanClasses(ScanParams params) {
		String[] pkgs = params.in();

		String rootPackage = ClasspathUtil.getRootPackage();
		if (U.isEmpty(pkgs)) {
			pkgs = rootPackage != null ? new String[]{rootPackage} : new String[]{""};
		}

		long startingAt = U.time();

		String regex = params.matching();
		Pattern pattern = U.notEmpty(regex) ? Pattern.compile(regex) : null;

		if (regex != null) {
			Log.info("Scanning classpath", "!annotated", Msc.annotations(params.annotated()), "!packages", Arrays.toString(pkgs), "!matching", regex);
		} else {
			Log.info("Scanning classpath", "!annotated", Msc.annotations(params.annotated()), "!packages", Arrays.toString(pkgs));
		}

		Set<String> classpath = U.notEmpty(params.classpath()) ? U.set(params.classpath()) : ClasspathUtil.getClasspath();
		AtomicInteger searched = new AtomicInteger();
		Set<String> classes = U.set();

		for (String pkg : pkgs) {
			classes.addAll(retrieveClasses(classpath, pkg, params.annotated(), params.bytecodeFilter(), pattern, params.classLoader(), searched));
		}

		List<String> classList = U.list(classes);

		long timeMs = U.time() - startingAt;

		Log.info("Finished classpath scan", "time", Msc.maybeMasked(timeMs) + "ms", "searched", searched.get(), "!found", Msc.classNames(classList));

		return classList;
	}

	private static List<String> retrieveClasses(Set<String> classpath, String packageName,
	                                            Class<? extends Annotation>[] annotated, Predicate<InputStream> bytecodeFilter,
	                                            Pattern regex, ClassLoader classLoader, AtomicInteger searched) {

		List<String> classes = U.list();

		String pkgName = U.safe(packageName);
		String pkgPath = pkgName.replace('.', File.separatorChar);

		Log.trace("Starting classpath scan", "package", packageName, "annotated", annotated, "regex", regex, "loader", classLoader);

		Log.trace("Classpath details", "classpath", classpath);

		Set<String> jars = U.set();

		for (String cpe : classpath) {
			File cpEntry = new File(cpe);

			if (cpEntry.exists()) {
				if (cpEntry.isDirectory()) {
					if (shouldScanDir(cpEntry.getAbsolutePath())) {
						Log.trace("Scanning directory", "root", cpEntry.getAbsolutePath());

						File startingDir;
						if (pkgPath.isEmpty()) {
							startingDir = cpEntry;
						} else {
							startingDir = new File(cpEntry.getAbsolutePath(), pkgPath);
						}

						if (startingDir.exists()) {
							getClassesFromDir(classes, cpEntry, startingDir, pkgName, regex, annotated, bytecodeFilter, searched);
						}
					} else {
						Log.trace("Skipping directory", "root", cpEntry.getAbsolutePath());
					}
				} else if (cpEntry.isFile() && cpEntry.getAbsolutePath().toLowerCase().endsWith(".jar")) {
					jars.add(cpEntry.getAbsolutePath());
				} else {
					Log.warn("Invalid classpath entry: " + cpe);
				}
			} else {
				if (!cpe.contains("*") && !cpe.contains("?") && U.neq(cpe, ClasspathUtil.appJar())) {
					Log.warn("Classpath entry doesn't exist: " + cpe);
				}
			}
		}

		for (String jarName : jars) {
			if (shouldScanJAR(jarName)) {
				Log.trace("Scanning JAR", "name", jarName);
				getClassesFromJAR(jarName, classes, packageName, regex, annotated, bytecodeFilter, classLoader, searched);
			} else {
				Log.trace("Skipping JAR", "name", jarName);
			}
		}

		return classes;
	}

	private static boolean shouldScanDir(String dir) {
		return true;
	}

	private static boolean shouldScanJAR(String jar) {
		String filename = new File(jar).getName();
		return !filename.equalsIgnoreCase("rapidoid.jar") && (!ClasspathUtil.hasAppJar() || U.eq(jar, ClasspathUtil.appJar()));
	}

	private static void getClassesFromDir(Collection<String> classes, File root, File dir, String pkg, Pattern regex,
	                                      Class<? extends Annotation>[] annotated, Predicate<InputStream> bytecodeFilter, AtomicInteger searched) {

		U.must(dir.isDirectory());
		Log.trace("Traversing directory", "root", root, "dir", dir);

		File[] files = dir.listFiles();
		if (files == null) {
			Log.warn("Not a folder!", "dir", dir);
			return;
		}

		for (File file : files) {
			if (file.isDirectory()) {
				getClassesFromDir(classes, root, file, pkg, regex, annotated, bytecodeFilter, searched);
			} else {
				String rootPath = Str.trimr(root.getAbsolutePath(), File.separatorChar);
				int from = rootPath.length() + 1;
				String relName = file.getAbsolutePath().substring(from);

				if (!ignore(relName)) {
					scanFile(classes, regex, annotated, bytecodeFilter, relName, file, null, null, searched);
				}
			}
		}
	}

	private static void scanFile(Collection<String> classes, Pattern regex,
	                             Class<? extends Annotation>[] annotated, Predicate<InputStream> bytecodeFilter, String relName,
	                             File file, ZipFile zip, ZipEntry entry, AtomicInteger searched) {

		Log.trace("scanned file", "file", relName);

		if (relName.endsWith(".class")) {
			searched.incrementAndGet();

			String clsName = Str.sub(relName, 0, -6).replace('/', '.').replace('\\', '.');

			if (U.isEmpty(regex) || regex.matcher(clsName).matches()) {
				try {

					InputStream input = file != null ? new FileInputStream(file) : zip.getInputStream(entry);

					boolean include;

					if (U.isEmpty(annotated)) {
						include = true;

					} else {
						ClassFile classFile = new ClassFile(new DataInputStream(input));
						include = isAnnotated(classFile, annotated);
					}

					if (include && bytecodeFilter != null) {
						include = Lmbd.eval(bytecodeFilter, input);
					}

					if (include) {
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

	public static List<String> getClassesFromJAR(String jarName, List<String> classes, String pkg, Pattern regex,
	                                             Class<? extends Annotation>[] annotated, Predicate<InputStream> bytecodeFilter,
	                                             ClassLoader classLoader, AtomicInteger searched) {

		ZipFile zip = null;

		try {
			String pkgPath = pkg != null ? pkg.replace('.', File.separatorChar) : null;
			String pkgPath2 = pkg != null ? pkg.replace('.', '/') : null;

			zip = new ZipFile(new File(jarName));
			Enumeration<? extends ZipEntry> entries = zip.entries();

			while (entries.hasMoreElements()) {
				ZipEntry zipEntry = entries.nextElement();
				if (!zipEntry.isDirectory()) {
					String name = zipEntry.getName();

					if (!ignore(name)) {
						if (U.isEmpty(pkg) || name.startsWith(pkgPath) || name.startsWith(pkgPath2)) {
							scanFile(classes, regex, annotated, bytecodeFilter, name, null, zip, zipEntry, searched);
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

}
