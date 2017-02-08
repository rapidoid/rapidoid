package org.rapidoid.model;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.BeanProperties;
import org.rapidoid.beany.Beany;
import org.rapidoid.beany.Prop;
import org.rapidoid.cls.Cls;
import org.rapidoid.lambda.Calc;
import org.rapidoid.model.impl.*;
import org.rapidoid.u.U;
import org.rapidoid.var.Var;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

@SuppressWarnings("serial")
@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class Models extends RapidoidThing {

	private static final Set<String> SPECIAL_PROPERTIES = U.set("id", "version", "createdby", "createdon",
		"lastupdatedby", "lastupdatedon");

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
		return value instanceof Map ? new MapItem((Map<?, ?>) value) : new BeanItem(value);
	}

	public static Property propertyOf(Class<?> beanType, String property) {
		if (property.startsWith("_")) {
			return new BuiltInProperty(property);
		}

		BeanProperties props = Beany.propertiesOf(beanType);

		if (beanType == Object.class) {
			return new BeanProperty(property, property.equals("id") ? long.class : String.class, null, null);
		}

		Prop prop = props.get(property);
		U.must(prop != null, "Cannot find property %s in class %s!", property, beanType);

		return prop(prop);
	}

	public static List<Property> propertiesOf(Object target, String... propertyNames) {
		return properties(Beany.propertiesOf(target).select(new ModelPropertySelector(propertyNames) {
			@Override
			public boolean eval(Prop prop) throws Exception {
				return isReadable(prop);
			}
		}));
	}

	public static List<Property> editablePropertiesOf(Object target, String... propertyNames) {
		return properties(Beany.propertiesOf(target).select(new ModelPropertySelector(propertyNames) {
			@Override
			public boolean eval(Prop prop) throws Exception {
				return isEditable(prop);
			}
		}));
	}

	public static List<Property> readablePropertiesOf(Object target, String... propertyNames) {
		return properties(Beany.propertiesOf(target).select(new ModelPropertySelector(propertyNames) {
			@Override
			public boolean eval(Prop prop) throws Exception {
				return isReadable(prop);
			}
		}));
	}

	@SuppressWarnings("unchecked")
	private static List<Property> properties(BeanProperties props) {
		String key = Property.class.getCanonicalName();

		List<Property> properties = (List<Property>) props.extras.get(key);

		if (properties == null) {
			properties = U.list();
			for (Prop prop : props) {
				properties.add(prop(prop));
			}
			props.extras.put(key, properties);
		}

		return properties;
	}

	private static BeanProperty prop(Prop prop) {
		return new BeanProperty(prop.getName(), prop.getType(), prop.getGenericType(), prop.getAnnotations());
	}

	public static boolean isEditable(Prop prop) {
		String name = prop.getName();

		if (isSpecialProperty(name)) {
			return false;
		}

		if (prop.isReadOnly() && !Collection.class.isAssignableFrom(prop.getType())) {
			return false;
		}

		return true;
	}

	public static boolean isReadable(Prop prop) {
		return true;
	}

	@SuppressWarnings("unchecked")
	public static <T> Items mockBeanItems(int size, Class<T> beanType) {
		T[] beans = (T[]) new Object[size];

		for (int i = 0; i < beans.length; i++) {
			beans[i] = Cls.newInstance(beanType);
			Beany.setId(beans[i], i);
		}

		return beanItems(beanType, beans);
	}

	@SuppressWarnings("unchecked")
	public static <T> Property property(String name, Calc<T> calc) {
		return new CalcProperty(name, (Calc<Object>) calc);
	}

	public static <T> Var<T> propertyVar(String name, Item item, String property, T initValue, boolean readOnly) {
		return new ItemPropertyVar<T>(name, item, property, initValue,readOnly);
	}

	public static boolean isSpecialProperty(String name) {
		return SPECIAL_PROPERTIES.contains(name.toLowerCase());
	}

}
