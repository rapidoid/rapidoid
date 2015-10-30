package org.rapidoid.webapp;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ctx.Classes;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.log.Log;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.u.U;

/*
 * #%L
 * rapidoid-http
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
public class Scan {

	private Scan() {}

	public static List<Class<?>> classes() {
		return classes(null, null, null, null, null);
	}

	public static List<Class<?>> classes(String packageName, String nameRegex, Predicate<Class<?>> filter,
			Class<? extends Annotation> annotated, ClassLoader classLoader) {
		return scanClasses(packageName, nameRegex, filter, annotated, classLoader);
	}

	public static List<Class<?>> annotated(Class<? extends Annotation> annotated) {
		return scanClasses(null, null, null, annotated, null);
	}

	public static List<Class<?>> annotated(Class<? extends Annotation> annotated, ClassLoader classLoader) {
		return scanClasses(null, null, null, annotated, classLoader);
	}

	public static List<Class<?>> pkg(String packageName) {
		return scanClasses(packageName, null, null, null, null);
	}

	public static List<Class<?>> byName(String simpleName, Predicate<Class<?>> filter, ClassLoader classLoader) {
		return scanClasses(null, "(.*\\.|^)" + simpleName, filter, null, classLoader);
	}

	public static List<Class<?>> bySuffix(String nameSuffix, Predicate<Class<?>> filter, ClassLoader classLoader) {
		return scanClasses(null, ".*\\w" + nameSuffix, filter, null, classLoader);
	}

	private static synchronized List<Class<?>> scanClasses(String packageName, String nameRegex,
			Predicate<Class<?>> filter, Class<? extends Annotation> annotated, ClassLoader classLoader) {

		packageName = U.or(packageName, "");

		List<?> cacheKey = null;

		WebApp app = Ctxs.ctx().app();
		Classes appClasses = app.getClasses();
		Map<List<?>, List<Class<?>>> cache = appClasses.getCache();

		cacheKey = U.list(packageName, nameRegex, filter, annotated, classLoader);
		List<Class<?>> cachedClasses = cache.get(cacheKey);
		if (cachedClasses != null) {
			return cachedClasses;
		}

		long startingAt = U.time();

		Log.info("Filtering " + appClasses.size() + " classes", "annotated", annotated, "package", packageName, "name",
				nameRegex);

		Pattern regex = nameRegex != null ? Pattern.compile(nameRegex) : null;

		List<Class<?>> classes = filterClasses(appClasses, packageName, regex, filter, annotated);

		cache.put(cacheKey, classes);

		long timeMs = U.time() - startingAt;
		Log.info("Finished classpath scan", "time", timeMs + "ms", "classes", classes);

		return classes;
	}

	private static List<Class<?>> filterClasses(Classes appClasses, String packageName, Pattern regex,
			Predicate<Class<?>> filter, Class<? extends Annotation> annotated) {

		List<Class<?>> matching = U.list();

		for (Entry<String, Class<?>> e : appClasses.entrySet()) {
			Class<?> cls = e.getValue();
			String pkg = cls.getPackage() != null ? cls.getPackage().getName() : "";

			if (U.isEmpty(packageName) || pkg.startsWith(packageName + ".") || pkg.equals(packageName)) {
				if (ClasspathUtil.classMatches(cls, filter, annotated, regex)) {
					matching.add(cls);
				}
			}
		}

		return matching;
	}

}
