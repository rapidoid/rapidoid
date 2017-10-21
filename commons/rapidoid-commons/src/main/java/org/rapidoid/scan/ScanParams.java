package org.rapidoid.scan;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.Operation;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.Arrays;
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
public class ScanParams extends RapidoidThing {

	private volatile String[] packages;

	private volatile String matching;

	private volatile Class<? extends java.lang.annotation.Annotation>[] annotated;

	private volatile ClassLoader classLoader;

	private volatile String[] classpath;

	private volatile Predicate<InputStream> bytecodeFilter;

	public ScanParams in(String... packages) {
		this.packages = packages;
		return this;
	}

	public ScanParams in(Iterable<String> packages) {
		return in(U.arrayOf(String.class, packages));
	}

	public String[] in() {
		return this.packages;
	}

	public ScanParams matching(String matching) {
		this.matching = matching;
		return this;
	}

	public String matching() {
		return this.matching;
	}

	public ScanParams annotated(Class<? extends Annotation>... annotated) {
		this.annotated = annotated;
		return this;
	}

	@SuppressWarnings("unchecked")
	public ScanParams annotated(Collection<Class<? extends Annotation>> annotated) {
		return annotated(U.arrayOf(Class.class, annotated));
	}

	public Class<? extends Annotation>[] annotated() {
		return this.annotated;
	}

	public ScanParams classLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
		return this;
	}

	public ClassLoader classLoader() {
		return this.classLoader;
	}

	public String[] classpath() {
		return classpath;
	}

	public ScanParams classpath(String... classpath) {
		this.classpath = classpath;
		return this;
	}

	public Predicate<InputStream> bytecodeFilter() {
		return bytecodeFilter;
	}

	public ScanParams bytecodeFilter(Predicate<InputStream> bytecodeFilter) {
		this.bytecodeFilter = bytecodeFilter;
		return this;
	}

	public List<String> getAll() {
		return ClasspathUtil.getClasses(this);
	}

	public List<Class<?>> loadAll() {
		return ClasspathUtil.loadClasses(this);
	}

	public void forEach(Operation<Class<?>> classOperation) {
		for (Class<?> cls : loadAll()) {
			try {
				classOperation.execute(cls);
			} catch (Exception e) {
				Log.error("Cannot process annotated class!", e);
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ScanParams that = (ScanParams) o;

		if (!Arrays.equals(packages, that.packages)) return false;
		if (matching != null ? !matching.equals(that.matching) : that.matching != null) return false;
		if (!Arrays.equals(annotated, that.annotated)) return false;
		if (classLoader != null ? !classLoader.equals(that.classLoader) : that.classLoader != null) return false;
		if (!Arrays.equals(classpath, that.classpath)) return false;
		return bytecodeFilter != null ? bytecodeFilter.equals(that.bytecodeFilter) : that.bytecodeFilter == null;
	}

	@Override
	public int hashCode() {
		int result = Arrays.hashCode(packages);
		result = 31 * result + (matching != null ? matching.hashCode() : 0);
		result = 31 * result + Arrays.hashCode(annotated);
		result = 31 * result + (classLoader != null ? classLoader.hashCode() : 0);
		result = 31 * result + Arrays.hashCode(classpath);
		result = 31 * result + (bytecodeFilter != null ? bytecodeFilter.hashCode() : 0);
		return result;
	}
}
