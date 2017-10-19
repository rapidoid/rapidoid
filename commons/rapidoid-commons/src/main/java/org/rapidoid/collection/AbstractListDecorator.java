package org.rapidoid.collection;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.util.*;

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
@Since("5.3.0")
@SuppressWarnings("NullableProblems")
public class AbstractListDecorator<E> extends AbstractDecorator<List<E>> implements List<E> {

	public AbstractListDecorator(List<E> decorated) {
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
	public boolean addAll(int index, Collection<? extends E> c) {
		return decorated.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return decorated.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return decorated.retainAll(c);
	}

	@Override
	public void clear() {
		decorated.clear();
	}

	@Override
	public E get(int index) {
		return decorated.get(index);
	}

	@Override
	public E set(int index, E element) {
		return decorated.set(index, element);
	}

	@Override
	public void add(int index, E element) {
		decorated.add(index, element);
	}

	@Override
	public E remove(int index) {
		return decorated.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return decorated.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return decorated.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return decorated.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return decorated.listIterator(index);
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return decorated.subList(fromIndex, toIndex);
	}

}
