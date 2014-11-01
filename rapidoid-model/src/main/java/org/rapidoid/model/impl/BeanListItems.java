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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.rapidoid.model.Property;
import org.rapidoid.util.Cls;
import org.rapidoid.util.Prop;
import org.rapidoid.util.U;

public class BeanListItems<T> extends ListItems {

	private final Class<T> beanType;

	private final List<Property> properties;

	public BeanListItems(Class<T> beanType, Object... values) {
		super(values);
		this.beanType = beanType;
		this.properties = propertiesFrom(beanType);
	}

	public BeanListItems(Class<T> beanType, Collection<?> values) {
		super(values);
		this.beanType = beanType;
		this.properties = propertiesFrom(beanType);
	}

	@Override
	public List<Property> properties() {
		return properties;
	}

	private static List<Property> propertiesFrom(Class<?> type) {
		List<Property> pr = U.list();

		Map<String, Prop> props = Cls.propertiesOf(type);
		for (Prop prop : props.values()) {
			pr.add(new BeanProperty(prop.getName(), prop.getType()));
		}

		return pr;
	}

}
