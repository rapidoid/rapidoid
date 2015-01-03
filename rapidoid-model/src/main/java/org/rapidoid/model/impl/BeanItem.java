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

import org.rapidoid.model.Item;
import org.rapidoid.model.Models;
import org.rapidoid.model.Property;
import org.rapidoid.prop.Prop;
import org.rapidoid.util.Cls;
import org.rapidoid.var.Var;

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

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String property) {
		if (property.equals("_class")) {
			return (T) value.getClass().getSimpleName();
		} else if (property.equals("_toString")) {
			return (T) value.toString();
		} else if (property.equals("_str")) {
			return (T) Cls.beanToStr(value, false);
		}

		Prop prop = Cls.property(value, property, true);

		if (Var.class.isAssignableFrom(prop.getType())) {
			Var<Object> propVar = prop.get(value);
			return (T) (propVar != null ? propVar.get() : null);
		}

		return prop.get(value);
	}

	@Override
	public void set(String property, Object propValue) {
		Prop prop = Cls.property(value, property, true);

		if (Var.class.isAssignableFrom(prop.getType())) {
			Var<Object> propVar = prop.get(value);
			if (propVar != null) {
				propVar.set(propValue);
				return;
			}
		}

		prop.set(value, propValue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Property> properties(String... propertyNames) {
		return value != null ? Models.propertiesOf(value.getClass(), propertyNames) : Collections.EMPTY_LIST;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Property> editableProperties(String... propertyNames) {
		return value != null ? Models.editablePropertiesOf(value.getClass(), propertyNames) : Collections.EMPTY_LIST;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Property> readableProperties(String... propertyNames) {
		return value != null ? Models.readablePropertiesOf(value.getClass(), propertyNames) : Collections.EMPTY_LIST;
	}

	@Override
	public String uri() {
		return "/" + value.getClass() + "/" + id();
	}

}
