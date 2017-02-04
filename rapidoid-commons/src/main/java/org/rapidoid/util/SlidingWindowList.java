package org.rapidoid.util;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.AbstractListDecorator;

import java.util.ArrayList;
import java.util.Collection;

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
public class SlidingWindowList<E> extends AbstractListDecorator<E> {

	private final int maxSize;

	private final double stretchingFactor;

	public SlidingWindowList(int maxSize) {
		this(maxSize, 1.1);
	}

	public SlidingWindowList(int maxSize, double stretchingFactor) {
		super(new ArrayList<E>());

		this.maxSize = maxSize;
		this.stretchingFactor = stretchingFactor;
	}

	@Override
	public boolean add(E element) {
		boolean ret = super.add(element);
		truncateIfTooBig();
		return ret;
	}

	@Override
	public void add(int index, E element) {
		super.add(index, element);
		truncateIfTooBig();
	}

	@Override
	public boolean addAll(Collection<? extends E> elements) {
		boolean ret = super.addAll(elements);
		truncateIfTooBig();
		return ret;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> elements) {
		boolean ret = super.addAll(index, elements);
		truncateIfTooBig();
		return ret;
	}

	private void truncateIfTooBig() {
		int size = size();
		if (size >= maxSize * (1 + stretchingFactor)) {
			decorated = new ArrayList<>(decorated.subList(size - maxSize, size));
		}
	}
}
