package org.rapidoid.commons;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.collection.AutoExpandingMap;
import org.rapidoid.collection.Coll;
import org.rapidoid.lambda.Mapper;

import java.util.Collection;
import java.util.Map;

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
@Since("5.1.0")
public class Deep extends RapidoidThing {

	@SuppressWarnings("unchecked")
	public static Object copyOf(Object source, Mapper<Object, ?> transformation) {
		Err.argMust(source != null, "source cannot be null!");

		if (Coll.isCollection(source)) {
			return copyOf((Collection<?>) source, transformation);

		} else if (Coll.isMap(source)) {
			return copyOf((Map<?, ?>) source, transformation);

		} else if (source instanceof Object[]) {
			// FIXME support primitive arrays
			return copyOf((Object[]) source, transformation);

		} else {
			try {
				return transformation != null ? transformation.map(source) : source;
			} catch (Exception e) {
				throw new RuntimeException("Transformation error!", e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Collection<T> copyOf(Collection<? extends T> source, Mapper<Object, ?> transformation) {
		Err.argMust(source != null, "source cannot be null!");

		Collection<T> destination = newInstance(source);
		copy(destination, source, transformation);

		return destination;
	}

	@SuppressWarnings("unchecked")
	public static <T> void copy(Collection<T> destination, Collection<? extends T> source, Mapper<Object, ?> transformation) {
		Err.argMust(source != null, "source cannot be null!");
		Err.argMust(destination != null, "destination cannot be null!");

		destination.clear();
		for (Object el : source) {
			destination.add((T) copyOf(el, transformation));
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] copyOf(T[] source, Mapper<Object, ?> transformation) {
		Err.argMust(source != null, "source cannot be null!");

		T[] destination = (T[]) newInstance(source);
		copy(destination, source, transformation);

		return destination;
	}

	@SuppressWarnings("unchecked")
	public static <T> void copy(T[] destination, T[] source, Mapper<Object, ?> transformation) {
		Err.argMust(source != null, "source cannot be null!");
		Err.argMust(destination != null, "destination cannot be null!");
		Err.argMust(source.length == destination.length, "source and destination arrays must have the same length!");

		for (int i = 0; i < destination.length; i++) {
			destination[i] = (T) copyOf(source[i], transformation);
		}
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> copyOf(Map<? extends K, ? extends V> source, Mapper<Object, ?> transformation) {
		Err.argMust(source != null, "source cannot be null!");

		Map<K, V> destination = newInstance(source);
		copy(destination, source, transformation);

		return destination;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> void copy(Map<K, V> destination, Map<? extends K, ? extends V> source, Mapper<Object, ?> transformation) {
		Err.argMust(source != null, "source cannot be null!");
		Err.argMust(destination != null, "destination cannot be null!");

		destination.clear();
		for (Map.Entry<? extends K, ? extends V> e : source.entrySet()) {
			K key = (K) copyOf(e.getKey(), transformation);
			V value = (V) copyOf(e.getValue(), transformation);
			destination.put(key, value);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> T newInstance(Object source) {
		if (source instanceof AutoExpandingMap) {
			AutoExpandingMap autoExpandingMap = (AutoExpandingMap) source;
			return (T) autoExpandingMap.copy();
		}

		return (T) Cls.newInstance(source.getClass());
	}

}
