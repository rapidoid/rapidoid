package org.rapidoid.var.impl;

/*
 * #%L
 * rapidoid-utils
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import java.util.Collection;

import org.rapidoid.util.ImportExport;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;
import org.rapidoid.var.Var;

public class CollectionContainerVar extends AbstractVar<Boolean> {

	private static final long serialVersionUID = 6990464844550633598L;

	private final Var<Collection<Object>> container;

	private final Object item;

	public CollectionContainerVar(ImportExport props) {
		container = props.get(A);
		item = props.get(B);
	}

	public CollectionContainerVar(Var<Collection<Object>> container, Object item) {
		this.container = container;
		this.item = item;
	}

	@Override
	public Boolean get() {
		return UTILS.contains(container.get(), item);
	}

	@Override
	public void set(Boolean value) {
		Collection<Object> coll = container.get();

		if (value) {
			if (coll != null) {
				if (!coll.contains(item)) {
					coll.add(item);
				}
			} else {
				container.set(U.list(item));
			}
		} else {
			if (coll != null) {
				coll.remove(item);
			}
		}
	}

	@Override
	public void exportTo(ImportExport props) {
		props.put(A, container);
		props.put(B, item);
	}

}
