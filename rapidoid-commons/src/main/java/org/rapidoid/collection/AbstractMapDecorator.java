package org.rapidoid.collection;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

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
@Since("5.2.0")
@SuppressWarnings("NullableProblems")
public class AbstractMapDecorator<K, V> extends AbstractDecorator<Map<K, V>> implements Map<K, V> {

	public AbstractMapDecorator(Map<K, V> decorated) {
		super(decorated);
	}

	@Override
	public V get(Object key) {
		return decorated.get(key);
	}

	@Override
	public V put(K k, V v) {
		return decorated.put(k, v);
	}

	@Override
	public V remove(Object o) {
		return decorated.remove(o);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		decorated.putAll(map);
	}

	@Override
	public void clear() {
		decorated.clear();
	}

	@Override
	public Set<K> keySet() {
		return decorated.keySet();
	}

	@Override
	public Collection<V> values() {
		return decorated.values();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return decorated.entrySet();
	}

	@Override
	public int size() {
		return decorated.size();
	}

	@Override
	public boolean isEmpty() {
		return decorated.isEmpty();
	}

	@Override
	public boolean containsKey(Object o) {
		return decorated.containsKey(o);
	}

	@Override
	public boolean containsValue(Object o) {
		return decorated.containsValue(o);
	}

}
