package org.rapidoid.model.impl;

/*
 * #%L
 * rapidoid-model
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.model.Item;
import org.rapidoid.util.ImportExport;
import org.rapidoid.var.impl.AbstractVar;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class ItemPropertyVar<T> extends AbstractVar<T> {

	private static final long serialVersionUID = -1208784804459879580L;

	private final Item item;

	private final String property;

	public ItemPropertyVar(ImportExport props) {
		item = props.get(A);
		property = props.get(B);
	}

	public ItemPropertyVar(Item item, String property) {
		this.item = item;
		this.property = property;
	}

	@Override
	public T get() {
		return item.get(property);
	}

	@Override
	public void set(T value) {
		item.set(property, value);
	}

	@Override
	public void exportTo(ImportExport props) {
		props.put(A, item);
		props.put(B, property);
	}

}
