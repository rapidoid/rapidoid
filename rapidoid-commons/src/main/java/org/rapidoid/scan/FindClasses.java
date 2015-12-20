package org.rapidoid.scan;

import java.lang.annotation.Annotation;
import java.util.List;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.Predicate;
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
public class FindClasses {

	private FindClasses() {}

	public static List<Class<?>> all() {
		return find(null, null, null, null, null);
	}

	public static List<Class<?>> find(String packageName, String nameRegex, Predicate<Class<?>> filter,
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

		return Scan.pkg(packageName).annotated(annotated).filter(filter).classLoader(classLoader).matching(nameRegex)
				.getClasses();
	}

}
