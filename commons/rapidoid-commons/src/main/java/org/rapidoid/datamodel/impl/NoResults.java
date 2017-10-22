package org.rapidoid.datamodel.impl;

/*-
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.datamodel.PageableData;
import org.rapidoid.u.U;

import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.4.6")
public class NoResults<T> extends ResultsImpl<T> {

	public NoResults() {
		super((PageableData<T>) noData());
	}

	private static <T> PageableData<T> noData() {
		return new PageableData<T>() {

			@Override
			public List<T> getPage(long skip, long limit) {
				return U.list();
			}

			@Override
			public long getCount() {
				return 0;
			}
		};
	}

	@Override
	public String toString() {
		return "[N/A]";
	}
}
