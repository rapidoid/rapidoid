package org.rapidoid.datamodel.impl;

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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.datamodel.PageableData;
import org.rapidoid.datamodel.Results;
import org.rapidoid.u.U;

import java.util.Iterator;
import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class ResultsImpl<T> extends RapidoidThing implements Results<T> {

	private final PageableData<T> data;

	public ResultsImpl(PageableData<T> data) {
		this.data = data;
	}

	protected PageableData<T> data() {
		return data;
	}

	@Override
	public final Iterator<T> iterator() {
		return new PagingIterator<>(data);
	}

	@Override
	public final List<T> all() {
		return retrievePage(0, -1);
	}

	@Override
	public final List<T> page(long skip, long limit) {
		return retrievePage(skip, limit);
	}

	@Override
	public T single() {
		return U.single(retrievePage(0, 2));
	}

	@Override
	public final T first() {
		return U.single(retrievePage(0, 1));
	}

	@Override
	public final T last() {
		return U.single(retrievePage(count() - 1, 1));
	}

	private List<T> retrievePage(long skip, long limit) {
		return data().getPage(skip, limit);
	}

	@Override
	public boolean isLoaded() {
		return data().getCount() >= 0;
	}

	@Override
	public long count() {
		long count = data().getCount();

		if (count < 0) {
			// it is unknown, so count manually
			count = 0;

			for (T item : this) {
				count++;
			}
		}

		return count;
	}

	@Override
	public boolean isSingle() {
		long count = data().getCount();

		if (count >= 0) {
			return count == 1;

		} else {
			return retrievePage(0, 2).size() == 1;
		}
	}
}
