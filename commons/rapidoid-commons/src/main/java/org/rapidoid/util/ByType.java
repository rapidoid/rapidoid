package org.rapidoid.util;

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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.u.U;

import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.2.0")
public class ByType<K, T> extends RapidoidThing {

	private final Map<Class<? extends K>, T> mappings = Coll.synchronizedMap();

	public void assign(Class<? extends K> type, T mapping) {
		mappings.put(type, mapping);
	}

	public T findByType(Class<? extends K> type) {
		T value = null;

		for (Class<? extends K> cls = type; cls.getSuperclass() != null; cls = U.cast(cls.getSuperclass())) {
			value = mappings.get(cls);

			if (value != null) {
				break;
			}
		}

		return value;
	}

	public static <K, T> ByType<K, T> create() {
		return new ByType<K, T>();
	}

	public void reset() {
		mappings.clear();
	}

}
