package org.rapidoid.scan;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.u.U;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

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
@Since("2.5.0")
public class Scan extends RapidoidThing {

	public static ScanParams in(String... packages) {
		return new ScanParams().in(packages);
	}

	public static ScanParams in(List<String> packages) {
		return in(U.arrayOf(String.class, packages));
	}

	public static ScanParams matching(String matching) {
		return new ScanParams().matching(matching);
	}

	public static ScanParams annotated(Class<? extends Annotation>... annotated) {
		return new ScanParams().annotated(annotated);
	}

	@SuppressWarnings("unchecked")
	public static ScanParams annotated(Collection<Class<? extends Annotation>> annotated) {
		return new ScanParams().annotated(annotated);
	}

	public static ScanParams classLoader(ClassLoader classLoader) {
		return new ScanParams().classLoader(classLoader);
	}

	public static ScanParams classpath(String... classpath) {
		return new ScanParams().classpath(classpath);
	}

	public static ScanParams bytecodeFilter(Predicate<InputStream> bytecodeFilter) {
		return new ScanParams().bytecodeFilter(bytecodeFilter);
	}

	public static List<String> getAll() {
		return ClasspathUtil.getClasses(new ScanParams());
	}

	public static List<Class<?>> loadAll() {
		return ClasspathUtil.loadClasses(new ScanParams());
	}

}
