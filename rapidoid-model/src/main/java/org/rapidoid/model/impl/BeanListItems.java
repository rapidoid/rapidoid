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

import java.util.Collections;
import java.util.List;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.model.Item;
import org.rapidoid.model.Models;
import org.rapidoid.model.Property;
import org.rapidoid.util.Cls;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class BeanListItems<T> extends ListItems {

	private static final long serialVersionUID = 7346765152583871241L;

	protected final Class<T> beanType;

	public BeanListItems(Class<T> beanType) {
		super("/" + (beanType != null ? beanType.getSimpleName().toLowerCase() : "?any"));
		this.beanType = beanType;
	}

	@Override
	public List<Property> properties(Object... properties) {
		return properties.length == 0 ? inferProperties() : filterProperties(properties);
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
	private List<Property> filterProperties(Object[] properties) {
		if (isEmpty()) {
			return Collections.EMPTY_LIST;
		}

		List<Property> props = U.list();

		Object item0 = get(0).value();
		for (Object prop : properties) {
			if (prop instanceof String) {
				String strProp = (String) prop;
				props.add(Models.propertyOf(Cls.of(item0), strProp));
			} else if (prop instanceof Property) {
				props.add((Property) prop);
			} else {
				throw U.rte("Invalid property: %s!", prop);
			}
		}

		return props;
	}

	@Override
	public boolean fitsIn(Item item) {
		return super.fitsIn(item) && (beanType == null || Cls.instanceOf(item.value(), beanType));
	}

}
