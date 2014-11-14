package org.rapidoid.model.impl;

/*
 * #%L
 * rapidoid-model
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import org.rapidoid.model.Item;
import org.rapidoid.model.Model;
import org.rapidoid.model.Property;
import org.rapidoid.util.U;

public class BeanListItems<T> extends ListItems {

	private static final long serialVersionUID = 7346765152583871241L;

	private final Class<T> beanType;

	private final List<Property> properties;

	public BeanListItems(Class<T> beanType) {
		this.beanType = beanType;
		this.properties = Model.propertiesOf(beanType);
	}

	@Override
	public List<Property> properties() {
		return properties;
	}

	@Override
	public boolean fitsIn(Item item) {
		return super.fitsIn(item) && U.instanceOf(item.value(), beanType);
	}

}
