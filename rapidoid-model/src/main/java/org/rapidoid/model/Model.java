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

	public static Items items(Object... values) {
		ListItems items = new ListItems();

		for (Object value : values) {
			items.add(item(value));
		}

		return items;
	}

	public static <T> Items beanItems(Class<T> beanType, T... beans) {
		ListItems items = new BeanListItems<T>(beanType);

		for (Object bean : beans) {
			items.add(item(bean));
		}

		return items;
	}

	@SuppressWarnings("unchecked")
	public static <T> Items beanItems(T... items) {
		U.must(items.length > 0, "Must have at least 1 item to infer the bean type!");
		Class<T> type = (Class<T>) items[0].getClass();
		return beanItems(type, items);
	}

	public static Item item(Object value) {
		return new BeanItem(value);
	}

	public static List<Property> propertiesOf(Class<?> beanType) {
		List<Property> pr = U.list();

		Map<String, Prop> props = Cls.propertiesOf(beanType);
		for (Prop prop : props.values()) {
			pr.add(new BeanProperty(prop.getName(), prop.getType()));
		}

		return pr;
	}

}
