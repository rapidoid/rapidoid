package org.rapidoid.collection;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.util.Collection;
import java.util.Iterator;
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
public class ChangeTrackingCollection<E> extends AbstractCollectionDecorator<E> {

	protected final AtomicBoolean dirtyFlag;

	public ChangeTrackingCollection(Collection<E> decorated, AtomicBoolean dirtyFlag) {
		super(decorated);
		this.dirtyFlag = dirtyFlag;
	}

	@Override
	public boolean add(E e) {
		return changedIf(decorated.add(e));
	}

	@Override
	public boolean remove(Object o) {
		return changedIf(decorated.remove(o));
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return changedIf(decorated.addAll(c));
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return changedIf(decorated.retainAll(c));
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return changedIf(decorated.removeAll(c));
	}

	@Override
	public void clear() {
		if (!decorated.isEmpty()) {
			decorated.clear();
			dirtyFlag.set(true);
		}
	}

	@Override
	public Iterator<E> iterator() {
		return new ChangeTrackingIterator<E>(super.iterator(), dirtyFlag);
	}

	protected boolean changedIf(boolean changed) {
		if (changed) {
			dirtyFlag.set(true);
		}

		return changed;
	}

}
