package org.rapidoid.model.impl;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.beany.Prop;
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
public abstract class AbstractItem extends AbstractModel implements Item {

	private static final long serialVersionUID = 7047562610876960947L;

	protected final Object value;

	public AbstractItem(Object value) {
		U.notNull(value, "item value");
		this.value = value;
	}

	@Override
	public String id() {
		return Cls.convert(Beany.getIdIfExists(value), String.class);
	}

	@Override
	public Object value() {
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String property) {
		if (property.equals("_class")) {
			return (T) Cls.entityName(value);
		} else if (property.equals("_toString")) {
			return (T) value.toString();
		} else if (property.equals("_str")) {
			return (T) Beany.beanToNiceText(value, false);
		}

		Prop prop = Beany.property(value, property, true);

		return prop.get(value);
	}

	@Override
	public void set(String property, Object propValue) {
		Prop prop = Beany.property(value, property, true);
		prop.set(value, propValue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Property> properties(String... propertyNames) {
		return value != null ? Models.propertiesOf(value, propertyNames) : Collections.EMPTY_LIST;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Property> editableProperties(String... propertyNames) {
		return value != null ? Models.editablePropertiesOf(value, propertyNames) : Collections.EMPTY_LIST;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Property> readableProperties(String... propertyNames) {
		return value != null ? Models.readablePropertiesOf(value, propertyNames) : Collections.EMPTY_LIST;
	}

}
