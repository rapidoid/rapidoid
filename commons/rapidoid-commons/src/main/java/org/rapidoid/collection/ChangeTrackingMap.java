package org.rapidoid.collection;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
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
@Since("5.2.0")
@SuppressWarnings("NullableProblems")
public class ChangeTrackingMap<K, V> extends AbstractMapDecorator<K, V> {

	private final AtomicBoolean dirtyFlag;

	private transient volatile Set<Entry<K, V>> entrySet;
	private transient volatile Set<K> keySet;
	private transient volatile Collection<V> values;

	public ChangeTrackingMap(Map<K, V> target, AtomicBoolean dirtyFlag) {
		super(target);
		this.dirtyFlag = dirtyFlag;
	}

	@Override
	public V put(K k, V v) {
		V old = decorated.put(k, v);
		changedIf(old != v);
		return old;
	}

	@Override
	public V remove(Object o) {
		V removed = decorated.remove(o);
		changedIf(removed != null);
		return removed;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		decorated.putAll(map);
		changedIf(!map.isEmpty());
	}

	@Override
	public void clear() {
		synchronized (decorated) {
			if (!decorated.isEmpty()) {
				decorated.clear();
				dirtyFlag.set(true);
			}
		}
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		if (entrySet == null) {
			entrySet = new ChangeTrackingSet<Entry<K, V>>(super.entrySet(), dirtyFlag);
		}

		return entrySet;
	}

	@Override
	public Set<K> keySet() {
		if (keySet == null) {
			keySet = new ChangeTrackingSet<K>(super.keySet(), dirtyFlag);
		}

		return keySet;
	}

	@Override
	public Collection<V> values() {
		if (values == null) {
			values = new ChangeTrackingCollection<V>(super.values(), dirtyFlag);
		}

		return values;
	}

	private void changedIf(boolean changed) {
		if (changed) {
			dirtyFlag.set(true);
		}
	}

}
