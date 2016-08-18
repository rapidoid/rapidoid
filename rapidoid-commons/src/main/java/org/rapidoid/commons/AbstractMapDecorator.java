package org.rapidoid.commons;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

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

@Authors("Nikolche Mihajlovski")
@Since("5.2.0")
public class AbstractMapDecorator<K, V> extends RapidoidThing implements Map<K, V> {

	protected final Map<K, V> target;

	public AbstractMapDecorator(Map<K, V> target) {
		this.target = target;
	}

	@Override
	public V get(Object key) {
		return target.get(key);
	}

	@Override
	public V put(K k, V v) {
		return target.put(k, v);
	}

	@Override
	public V remove(Object o) {
		return target.remove(o);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		target.putAll(map);
	}

	@Override
	public void clear() {
		target.clear();
	}

	@Override
	public Set<K> keySet() {
		return target.keySet();
	}

	@Override
	public Collection<V> values() {
		return target.values();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return target.entrySet();
	}

	@Override
	public int size() {
		return target.size();
	}

	@Override
	public boolean isEmpty() {
		return target.isEmpty();
	}

	@Override
	public boolean containsKey(Object o) {
		return target.containsKey(o);
	}

	@Override
	public boolean containsValue(Object o) {
		return target.containsValue(o);
	}

	@Override
	public String toString() {
		return target.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		return target.equals(obj);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (target.hashCode());
		return result;
	}

}
