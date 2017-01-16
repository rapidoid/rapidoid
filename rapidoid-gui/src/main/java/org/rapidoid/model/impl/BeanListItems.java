package org.rapidoid.model.impl;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.model.Item;
import org.rapidoid.model.Models;
import org.rapidoid.model.Property;
import org.rapidoid.u.U;

import java.util.Collections;
import java.util.List;

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

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class BeanListItems<T> extends ListItems {

	private static final long serialVersionUID = 7346765152583871241L;

	protected final Class<T> beanType;

	public BeanListItems(Class<T> beanType) {
		this.beanType = beanType;
	}

	@Override
	public List<Property> properties(String... properties) {
		return U.isEmpty(properties) ? inferProperties() : filterProperties(properties);
	}

	@SuppressWarnings("unchecked")
	private List<Property> inferProperties() {
		if (isEmpty()) {
			return Collections.EMPTY_LIST;
		}

		Object item0 = get(0).value();
		return Models.propertiesOf(item0);
	}

	@SuppressWarnings("unchecked")
	private List<Property> filterProperties(String[] properties) {
		if (isEmpty() || U.isEmpty(properties)) {
			return Collections.EMPTY_LIST;
		}

		Object item0 = get(0).value();
		return Models.propertiesOf(item0, properties);
	}

	@Override
	public boolean fitsIn(Item item) {
		return super.fitsIn(item) && (beanType == null || Cls.instanceOf(item.value(), beanType));
	}

	@Override
	public Item ifFitsIn(Item item) {
		U.must(fitsIn(item), "The item doesn't fit in the items, expected type '%s' but found: %s", beanType, Cls.of(item.value()));
		return item;
	}

}
