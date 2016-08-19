package org.rapidoid.collection;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

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
public class ChangeTrackingMap<K, V> extends AbstractMapDecorator<K, V> {

	// FIXME wrap entryset, iterator etc. to detect changes made through them

	private final AtomicBoolean dirtyFlag;

	public ChangeTrackingMap(Map<K, V> target, AtomicBoolean dirtyFlag) {
		super(target);
		this.dirtyFlag = dirtyFlag;
	}

	@Override
	public V put(K k, V v) {
		V old = target.put(k, v);
		setDirtyIf(old != v);
		return old;
	}

	@Override
	public V remove(Object o) {
		V removed = target.remove(o);
		setDirtyIf(removed != null);
		return removed;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		target.putAll(map);
		setDirtyIf(!map.isEmpty());
	}

	@Override
	public void clear() {
		boolean notEmpty = !target.isEmpty();

		if (notEmpty) {
			target.clear();
		}

		setDirtyIf(notEmpty);
	}

	private void setDirtyIf(boolean changed) {
		if (changed) {
			dirtyFlag.set(true);
		}
	}

}
