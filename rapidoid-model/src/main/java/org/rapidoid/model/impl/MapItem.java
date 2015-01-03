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
import java.util.Map;

import org.rapidoid.model.Item;
import org.rapidoid.model.Models;
import org.rapidoid.model.Property;
import org.rapidoid.prop.Prop;
import org.rapidoid.util.Cls;
import org.rapidoid.var.Var;

public class MapItem extends AbstractModel implements Item {

	private static final long serialVersionUID = 4793756823666203912L;

	private final Map<?, ?> map;

	public MapItem(Map<?, ?> map) {
		this.map = map;
	}

	@Override
	public String id() {
		Long id = Cls.getIdIfExists(map);
		return id != null ? "" + id : null;
	}

	@Override
	public Object value() {
		return map;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String property) {
		if (property.equals("_class")) {
			return (T) "Map";
		} else if (property.equals("_toString")) {
			return (T) map.toString();
		} else if (property.equals("_str")) {
			return (T) Cls.beanToStr(map, false);
		}

		Prop prop = Cls.property(map, property, true);

		if (Var.class.isAssignableFrom(prop.getType())) {
			Var<Object> propVar = prop.get(map);
			return (T) (propVar != null ? propVar.get() : null);
		}

		return prop.get(map);
	}

	@Override
	public void set(String property, Object propValue) {
		Prop prop = Cls.property(map, property, true);

		if (Var.class.isAssignableFrom(prop.getType())) {
			Var<Object> propVar = prop.get(map);
			if (propVar != null) {
				propVar.set(propValue);
				return;
			}
		}

		prop.set(map, propValue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Property> properties(String... propertyNames) {
		return map != null ? Models.propertiesOf(map.getClass(), propertyNames) : Collections.EMPTY_LIST;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Property> editableProperties(String... propertyNames) {
		return map != null ? Models.editablePropertiesOf(map.getClass(), propertyNames) : Collections.EMPTY_LIST;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Property> readableProperties(String... propertyNames) {
		return map != null ? Models.readablePropertiesOf(map.getClass(), propertyNames) : Collections.EMPTY_LIST;
	}

	@Override
	public String uri() {
		return "/" + map.getClass() + "/" + id();
	}

}
