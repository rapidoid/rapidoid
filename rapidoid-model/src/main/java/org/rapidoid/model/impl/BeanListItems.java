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

import java.util.List;

import org.rapidoid.annotation.Authors;
import org.rapidoid.model.Item;
import org.rapidoid.model.Models;
import org.rapidoid.model.Property;
import org.rapidoid.util.Cls;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
public class BeanListItems<T> extends ListItems {

	private static final long serialVersionUID = 7346765152583871241L;

	protected final Class<T> beanType;

	protected final List<Property> properties;

	public BeanListItems(Class<T> beanType) {
		super("/" + beanType.getSimpleName().toLowerCase());
		this.beanType = beanType;
		this.properties = Models.propertiesOf(beanType);
	}

	@Override
	public List<Property> properties(String... propertyNames) {
		return propertyNames.length == 0 ? properties : filterProperties(propertyNames);
	}

	private List<Property> filterProperties(String[] propertyNames) {
		List<Property> props = U.list();

		for (String pr : propertyNames) {
			props.add(Models.propertyOf(beanType, pr));
		}

		return props;
	}

	@Override
	public boolean fitsIn(Item item) {
		return super.fitsIn(item) && Cls.instanceOf(item.value(), beanType);
	}

}
