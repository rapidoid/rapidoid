package org.rapidoid.collection;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.Err;
import org.rapidoid.data.JSON;
import org.rapidoid.datamodel.Results;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.u.U;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

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
public class Coll extends RapidoidThing {

	@SuppressWarnings("unchecked")
	@SafeVarargs
	public static <T> Set<T> synchronizedSet(T... values) {
		return Collections.synchronizedSet(U.set(values));
	}

	@SuppressWarnings("unchecked")
	@SafeVarargs
	public static <T> List<T> synchronizedList(T... values) {
		return Collections.synchronizedList(U.list(values));
	}

	public static <T> Set<T> concurrentSet() {
		return Collections.newSetFromMap(new ConcurrentHashMap<T, Boolean>());
	}

	public static boolean isMap(Object obj) {
		return obj instanceof Map<?, ?>;
	}

	public static boolean isList(Object obj) {
		return obj instanceof List<?>;
	}

	public static boolean isSet(Object obj) {
		return obj instanceof Set<?>;
	}

	public static boolean isCollection(Object obj) {
		return obj instanceof Collection<?>;
	}

	public static <T> void assign(Collection<T> destination, Collection<? extends T> source) {
		Err.argMust(destination != null, "destination cannot be null!");

		destination.clear();

		if (source != null) {
			destination.addAll(source);
		}
	}

	public static <T> void assign(Collection<? super T> destination, T[] source) {
		Err.argMust(destination != null, "destination cannot be null!");

		destination.clear();

		if (source != null) {
			Collections.addAll(destination, source);
		}
	}

	public static <K, V> void assign(Map<K, V> destination, Map<? extends K, ? extends V> source) {
		Err.argMust(destination != null, "destination cannot be null!");

		destination.clear();

		if (source != null) {
			destination.putAll(source);
		}
	}

	public static <K, V> V get(Map<K, V> map, K key) {
		V value = map.get(key);
		U.notNull(value, "map[%s]", key);
		return value;
	}

	public static <K, V> V get(Map<K, V> map, K key, V defaultValue) {
		V value = map.get(key);
		return value != null ? value : defaultValue;
	}

	public static <K, V> ConcurrentMap<K, V> concurrentMap() {
		return new ConcurrentHashMap<K, V>();
	}

	public static <K, V> ConcurrentMap<K, V> concurrentMap(Map<? extends K, ? extends V> src, boolean ignoreNullValues) {
		ConcurrentMap<K, V> map = concurrentMap();

		for (Map.Entry<? extends K, ? extends V> e : src.entrySet()) {
			if (!ignoreNullValues || e.getValue() != null) {
				map.put(e.getKey(), e.getValue());
			}
		}

		return map;
	}

	public static <K, V> ConcurrentMap<K, V> concurrentMap(K key, V value) {
		ConcurrentMap<K, V> map = concurrentMap();
		map.put(key, value);
		return map;
	}

	public static <K, V> ConcurrentMap<K, V> concurrentMap(K key1, V value1, K key2, V value2) {
		ConcurrentMap<K, V> map = concurrentMap(key1, value1);
		map.put(key2, value2);
		return map;
	}

	public static <K, V> ConcurrentMap<K, V> concurrentMap(K key1, V value1, K key2, V value2, K key3, V value3) {
		ConcurrentMap<K, V> map = concurrentMap(key1, value1, key2, value2);
		map.put(key3, value3);
		return map;
	}

	public static <K, V> ConcurrentMap<K, V> concurrentMap(K key1, V value1, K key2, V value2, K key3, V value3,
	                                                       K key4, V value4) {
		ConcurrentMap<K, V> map = concurrentMap(key1, value1, key2, value2, key3, value3);
		map.put(key4, value4);
		return map;
	}

	public static <K, V> ConcurrentMap<K, V> concurrentMap(K key1, V value1, K key2, V value2, K key3, V value3,
	                                                       K key4, V value4, K key5, V value5) {
		ConcurrentMap<K, V> map = concurrentMap(key1, value1, key2, value2, key3, value3, key4, value4);
		map.put(key5, value5);
		return map;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> ConcurrentMap<K, V> concurrentMap(Object... keysAndValues) {
		U.must(keysAndValues.length % 2 == 0, "Incorrect number of arguments (expected key-value pairs)!");

		ConcurrentMap<K, V> map = concurrentMap();

		for (int i = 0; i < keysAndValues.length / 2; i++) {
			map.put((K) keysAndValues[i * 2], (V) keysAndValues[i * 2 + 1]);
		}

		return map;
	}

	public static <K, V> Map<K, V> orderedMap() {
		return new LinkedHashMap<K, V>();
	}

	public static <K, V> Map<K, V> orderedMap(Map<? extends K, ? extends V> src, boolean ignoreNullValues) {
		Map<K, V> map = orderedMap();

		for (Map.Entry<? extends K, ? extends V> e : src.entrySet()) {
			if (!ignoreNullValues || e.getValue() != null) {
				map.put(e.getKey(), e.getValue());
			}
		}

		return map;
	}

	public static <K, V> Map<K, V> orderedMap(K key, V value) {
		Map<K, V> map = orderedMap();
		map.put(key, value);
		return map;
	}

	public static <K, V> Map<K, V> orderedMap(K key1, V value1, K key2, V value2) {
		Map<K, V> map = orderedMap(key1, value1);
		map.put(key2, value2);
		return map;
	}

	public static <K, V> Map<K, V> orderedMap(K key1, V value1, K key2, V value2, K key3, V value3) {
		Map<K, V> map = orderedMap(key1, value1, key2, value2);
		map.put(key3, value3);
		return map;
	}

	public static <K, V> Map<K, V> orderedMap(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4) {
		Map<K, V> map = orderedMap(key1, value1, key2, value2, key3, value3);
		map.put(key4, value4);
		return map;
	}

	public static <K, V> Map<K, V> orderedMap(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4,
	                                          K key5, V value5) {
		Map<K, V> map = orderedMap(key1, value1, key2, value2, key3, value3, key4, value4);
		map.put(key5, value5);
		return map;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> orderedMap(Object... keysAndValues) {
		U.must(keysAndValues.length % 2 == 0, "Incorrect number of arguments (expected key-value pairs)!");

		Map<K, V> map = orderedMap();

		for (int i = 0; i < keysAndValues.length / 2; i++) {
			map.put((K) keysAndValues[i * 2], (V) keysAndValues[i * 2 + 1]);
		}

		return map;
	}

	public static <K, V> Map<K, V> synchronizedMap() {
		return Collections.synchronizedMap(U.<K, V>map());
	}

	public static <T> Queue<T> queue() {
		return new ConcurrentLinkedQueue<T>();
	}

	public static <T> BlockingQueue<T> queue(int maxSize) {
		Err.argMust(maxSize > 0, "Maximum queue size must be > 0!");
		return new ArrayBlockingQueue<T>(maxSize);
	}

	public static <K, V> Map<K, V> autoExpandingMap(final Class<K> keyClass, final Class<V> valueClass) {
		try {
			// search for the key-based constructor
			final Constructor<V> constructor = valueClass.getConstructor(keyClass);

			return autoExpandingMap(new Mapper<K, V>() {

				@SuppressWarnings("unchecked")
				@Override
				public V map(K key) throws Exception {
					try {
						return (V) constructor.newInstance(key);
					} catch (Exception e) {
						throw U.rte(e);
					}
				}
			});

		} catch (NoSuchMethodException e) {

			// otherwise, use the default constructor
			Constructor<V> constructor;
			try {
				constructor = valueClass.getConstructor();
			} catch (NoSuchMethodException e2) {
				throw U.rte("Couldn't find a matching constructor for the auto-expanding map!");
			}

			final Constructor<V> defConstructor = constructor;
			return autoExpandingMap(new Mapper<K, V>() {

				@SuppressWarnings("unchecked")
				@Override
				public V map(K key) throws Exception {
					try {
						return (V) defConstructor.newInstance();
					} catch (Exception e) {
						throw U.rte(e);
					}
				}
			});
		}
	}

	@SuppressWarnings("serial")
	public static <K, V> Map<K, V> autoExpandingMap(Mapper<K, V> valueFactory) {
		return new AutoExpandingMap<K, V>(valueFactory);
	}

	public static <K1, K2, V> Map<K1, Map<K2, V>> mapOfMaps() {
		return autoExpandingMap(new Mapper<K1, Map<K2, V>>() {

			@Override
			public Map<K2, V> map(K1 src) throws Exception {
				return synchronizedMap();
			}

		});
	}

	public static <K1, K2, K3, V> Map<K1, Map<K2, Map<K3, V>>> mapOfMapOfMaps() {
		return autoExpandingMap(new Mapper<K1, Map<K2, Map<K3, V>>>() {

			@Override
			public Map<K2, Map<K3, V>> map(K1 src) throws Exception {
				return mapOfMaps();
			}

		});
	}

	public static <K, V> Map<K, List<V>> mapOfLists() {
		return autoExpandingMap(new Mapper<K, List<V>>() {

			@SuppressWarnings("unchecked")
			@Override
			public List<V> map(K src) throws Exception {
				return synchronizedList();
			}

		});
	}

	public static <K1, K2, V> Map<K1, Map<K2, List<V>>> mapOfMapOfLists() {
		return autoExpandingMap(new Mapper<K1, Map<K2, List<V>>>() {

			@Override
			public Map<K2, List<V>> map(K1 src) throws Exception {
				return mapOfLists();
			}

		});
	}

	public static <K, V> Map<K, Set<V>> mapOfSets() {
		return autoExpandingMap(new Mapper<K, Set<V>>() {

			@SuppressWarnings("unchecked")
			@Override
			public Set<V> map(K src) throws Exception {
				return synchronizedSet();
			}

		});
	}

	public static <K1, K2, V> Map<K1, Map<K2, Set<V>>> mapOfMapOfSets() {
		return autoExpandingMap(new Mapper<K1, Map<K2, Set<V>>>() {

			@Override
			public Map<K2, Set<V>> map(K1 src) throws Exception {
				return mapOfSets();
			}

		});
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> range(Iterable<T> items, int from, int to) {
		U.must(from <= to, "'from' (%s) must be <= 'to' (%s)!", from, to);

		if (from == to) {
			return Collections.emptyList();
		}

		if (items instanceof Results) {
			Results results = (Results) items;
			return results.page(from, to - from);
		}

		List<?> list = (items instanceof List<?>) ? (List<?>) items : U.list(items);

		from = Math.min(from, list.size());
		to = Math.min(to, list.size());

		return U.cast(list.subList(from, to));
	}

	public static Integer getSizeOrNull(Iterable<?> items) {
		return (items instanceof Collection<?>) ? ((Collection<?>) items).size() : null;
	}

	public static <K, V> ChangeTrackingMap<K, V> trackChanges(Map<K, V> map, AtomicBoolean dirtyFlag) {
		return new ChangeTrackingMap<K, V>(map, dirtyFlag);
	}

	public static <T> Map<String, T> toBeanMap(Map<String, Object> data, Class<T> type) {
		Map<String, T> map = U.map();

		for (Map.Entry<String, Object> e : data.entrySet()) {
			T bean;
			Object value = e.getValue();

			if (value instanceof Map) {
				bean = JSON.MAPPER.convertValue(value, type);

			} else if (value instanceof String) {
				bean = Cls.newInstance(type, value);

			} else {
				throw U.rte("Unsupported configuration type: %s", Cls.of(value));
			}

			map.put(e.getKey(), bean);
		}

		return Collections.unmodifiableMap(map);
	}

	public static Map<String, Object> deepCopyOf(Map<String, Object> map) {
		return JSON.parseMap(JSON.stringify(map)); // FIXME proper implementation
	}

	public static <T> Set<T> copyOf(Set<T> src) {
		Set<T> copy;

		synchronized (src) {
			copy = U.set(src);
		}

		return copy;
	}

	public static <T> List<T> copyOf(List<T> src) {
		List<T> copy;

		synchronized (src) {
			copy = U.list(src);
		}

		return copy;
	}

}
