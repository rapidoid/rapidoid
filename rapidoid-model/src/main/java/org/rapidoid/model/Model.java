package org.rapidoid.model;

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
import java.util.Map;

import org.rapidoid.model.impl.BeanItem;
import org.rapidoid.model.impl.BeanListItems;
import org.rapidoid.model.impl.BeanProperty;
import org.rapidoid.model.impl.ListItems;
import org.rapidoid.util.Cls;
import org.rapidoid.util.Prop;
import org.rapidoid.util.U;

public class Model {

	public static <T> Items beanItems(Class<T> beanType, Object... beans) {
		ListItems items = new BeanListItems<T>(beanType);

		for (Object bean : beans) {
			items.add(item(bean));
		}

		return items;
	}

	@SuppressWarnings("unchecked")
	public static <T> Items beanItemsInfer(T... items) {
		U.must(items.length > 0, "Must have at least 1 item to infer the bean type!");
		Class<T> type = (Class<T>) items[0].getClass();
		return beanItems(type, items);
	}

	public static Item item(Object value) {
		return new BeanItem(value);
	}

	public static Property propertyOf(Class<?> beanType, String property) {
		Map<String, Prop> props = Cls.propertiesOf(beanType);

		Prop prop = props.get(property);
		U.must(prop != null, "Cannot find property %s in class %s!", property, beanType);

		return new BeanProperty(prop.getName(), prop.getType());
	}

	public static List<Property> propertiesOf(Class<?> beanType, String... propertyNames) {
		List<Property> pr = U.list();

		Map<String, Prop> props = Cls.propertiesOf(beanType);

		if (propertyNames.length == 0) {

			Prop idProp = props.get("id");
			if (idProp != null) {
				pr.add(new BeanProperty(idProp.getName(), idProp.getType()));
			}

			for (Prop prop : props.values()) {
				if (!prop.getName().equals("id")) {
					pr.add(new BeanProperty(prop.getName(), prop.getType()));
				}
			}
		} else {
			for (String propName : propertyNames) {
				Prop prop = props.get(propName);
				U.must(prop != null, "Cannot find property '%s' in type: %s", propName, beanType);
				pr.add(new BeanProperty(prop.getName(), prop.getType()));
			}
		}
		return pr;
	}

	public static List<Property> editablePropertiesOf(Class<?> beanType, String... propertyNames) {
		List<Property> pr = U.list();

		Map<String, Prop> props = Cls.propertiesOf(beanType);

		if (propertyNames.length == 0) {
			for (Prop prop : props.values()) {
				if (!prop.getName().equalsIgnoreCase("id")) {
					pr.add(new BeanProperty(prop.getName(), prop.getType()));
				}
			}
		} else {
			for (String propName : propertyNames) {
				Prop prop = props.get(propName);
				U.must(prop != null, "Cannot find property '%s' in type: %s", propName, beanType);
				pr.add(new BeanProperty(prop.getName(), prop.getType()));
			}
		}

		return pr;
	}

	@SuppressWarnings("unchecked")
	public static <T> Items mockBeanItems(int size, Class<T> beanType) {
		T[] beans = (T[]) new Object[size];

		for (int i = 0; i < beans.length; i++) {
			beans[i] = U.newInstance(beanType);
			Cls.setId(beans[i], i);
		}

		return beanItems(beanType, beans);
	}

}
