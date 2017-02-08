package org.rapidoid.model.impl;

/*
 * #%L
 * rapidoid-gui
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
import org.rapidoid.cls.Cls;
import org.rapidoid.model.Item;
import org.rapidoid.var.impl.AbstractVar;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class ItemPropertyVar<T> extends AbstractVar<T> {

	private static final long serialVersionUID = -1208784804459879580L;

	private final Item item;

	private final String property;

	private final boolean readOnly;

	public ItemPropertyVar(String name, Item item, String property, T initValue, boolean readOnly) {
		super(name);
		this.item = item;
		this.property = property;
		this.readOnly = readOnly;

		if (initValue != null) {
			set(initValue);
		}
	}

	@Override
	public T get() {
		return item.get(property);
	}

	@Override
	public void doSet(T value) {
		if (!readOnly) {
			T oldValue = get();

			if (oldValue != null) {
				item.set(property, Cls.convert(value, oldValue.getClass()));
			} else {
				item.set(property, value);
			}
		}
	}

}
