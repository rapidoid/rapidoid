package org.rapidoid.scan;

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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.lang.annotation.Annotation;
import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("2.5.0")
public class Scan {

	public static ScanParams pkg(String pkg) {
		return new ScanParams().pkg(pkg);
	}

	public static ScanParams matching(String matching) {
		return new ScanParams().matching(matching);
	}

	public static ScanParams filter(org.rapidoid.lambda.Predicate<Class<?>> filter) {
		return new ScanParams().filter(filter);
	}

	public static ScanParams annotated(Class<? extends Annotation>... annotated) {
		return new ScanParams().annotated(annotated);
	}

	public static ScanParams classLoader(ClassLoader classLoader) {
		return new ScanParams().classLoader(classLoader);
	}

	public static synchronized List<Class<?>> getClasses() {
		return ClasspathUtil.getClasses(new ScanParams());
	}

}
