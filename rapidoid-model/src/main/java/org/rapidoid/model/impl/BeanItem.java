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

import java.util.Collections;
import java.util.List;

import org.rapidoid.model.Item;
import org.rapidoid.model.Model;
import org.rapidoid.model.Property;
import org.rapidoid.util.Cls;

public class BeanItem extends AbstractModel implements Item {

	private static final long serialVersionUID = 4793756823666203912L;

	private final Object value;

	public BeanItem(Object object) {
		this.value = object;
	}

	@Override
	public String id() {
		Long id = Cls.getIdIfExists(value);
		return id != null ? "" + id : null;
	}

	@Override
	public Object value() {
		return value;
	}

	@Override
	public <T> T get(String property) {
		return Cls.getPropValue(value, property);
	}

	@Override
	public void set(String property, Object propValue) {
		Cls.setPropValue(value, property, propValue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Property> properties(String... propertyNames) {
		return value != null ? Model.propertiesOf(value.getClass(), propertyNames) : Collections.EMPTY_LIST;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Property> editableProperties(String... propertyNames) {
		return value != null ? Model.editablePropertiesOf(value.getClass(), propertyNames) : Collections.EMPTY_LIST;
	}

	@Override
	public String uri() {
		return "/" + value.getClass() + "/" + id();
	}

}
