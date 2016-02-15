package org.rapidoid.web;

/*
 * #%L
 * rapidoid-http-fast
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
import org.rapidoid.lambda.Operation;
import org.rapidoid.log.Log;
import org.rapidoid.scan.Scan;

import java.lang.annotation.Annotation;
import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class OnAnnotated {

	private final Class<? extends Annotation>[] annotated;

	private volatile String[] path;

	OnAnnotated(Class<? extends Annotation>[] annotated, String[] path) {
		this.annotated = annotated;
		this.path = path;
	}

	public synchronized void forEach(Operation<Class<?>> classOperation) {
		for (Class<?> cls : getAll()) {
			try {
				classOperation.execute(cls);
			} catch (Exception e) {
				Log.error("Cannot process annotated class!", e);
			}
		}
	}

	public synchronized OnAnnotated in(String... packages) {
		this.path = packages;
		return this;
	}

	@SuppressWarnings("unchecked")
	public synchronized List<Class<?>> getAll() {
		return Scan.annotated(annotated).in(path).getClasses();
	}

}
