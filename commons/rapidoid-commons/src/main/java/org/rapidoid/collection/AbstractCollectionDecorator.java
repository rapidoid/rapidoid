package org.rapidoid.collection;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.util.Collection;
import java.util.Iterator;

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
public class AbstractCollectionDecorator<E> extends AbstractDecorator<Collection<E>> implements Collection<E> {

	public AbstractCollectionDecorator(Collection<E> decorated) {
		super(decorated);
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
	public boolean contains(Object o) {
		return decorated.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return decorated.iterator();
	}

	@Override
	public Object[] toArray() {
		return decorated.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return decorated.toArray(a);
	}

	@Override
	public boolean add(E e) {
		return decorated.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return decorated.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return decorated.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return decorated.addAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return decorated.retainAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return decorated.removeAll(c);
	}

	@Override
	public void clear() {
		decorated.clear();
	}

}
