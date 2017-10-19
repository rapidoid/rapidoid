package org.rapidoid.ctx;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Metadata;
import org.rapidoid.u.U;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
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
@Since("2.1.0")
public class Classes extends LinkedHashMap<String, Class<?>> {

	private static final long serialVersionUID = 8987037790459772014L;

	public static Classes from(Iterable<Class<?>> classes) {
		Classes clss = new Classes();

		for (Class<?> cls : classes) {
			clss.put(cls.getSimpleName(), cls);
		}

		return clss;
	}

	public Classes annotated(Class<? extends Annotation> annotation) {
		List<Class<?>> selected = U.list();

		for (Class<?> cls : values()) {
			if (Metadata.isAnnotated(cls, annotation)) {
				selected.add(cls);
			}
		}

		return from(selected);
	}

}
