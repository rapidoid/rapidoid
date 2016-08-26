package org.rapidoid.render;

/*
 * #%L
 * rapidoid-render
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.BeanProp;
import org.rapidoid.beany.Beany;
import org.rapidoid.beany.Prop;
import org.rapidoid.cls.Cls;
import org.rapidoid.u.U;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.2.0")
public class ValueRetriever extends RapidoidThing {

	private final String property;

	private volatile Class<?> cachedModelType;
	private volatile Method cachedGetter;

	public ValueRetriever(String property) {
		this.property = property;
	}

	public static ValueRetriever of(String property) {
		return new ValueRetriever(property);
	}

	public Object read(List<Object> model) {
		if (U.isEmpty(model)) return null;
		return property.equals(".") ? self(model) : getProp(model);
	}

	private Object getProp(List<Object> model) {
		Object target = U.last(model);

		Class<?> cls = target.getClass();
		if (cls.equals(cachedModelType) && cachedGetter != null) {
			return Cls.invoke(cachedGetter, target);
		}

		Prop prop = Beany.property(cls, property, false);

		if (prop != null && prop instanceof BeanProp) {
			Method getter = ((BeanProp) prop).getGetter();

			if (getter != null) {
				getter.setAccessible(true);
				cachedGetter = getter;
				cachedModelType = cls;
				return Cls.invoke(getter, target);
			}

			return prop.get(target);
		}

		return propOf(property, model);
	}

	private static Object self(List<Object> model) {
		return model.get(model.size() - 1);
	}

	public static Object propOf(String name, List<Object> scope) {
		int p = name.indexOf(".");

		if (p > 0) {
			Object first = propOf(name.substring(0, p), scope);
			return propOf(name.substring(p + 1), Collections.singletonList(first));
		}

		for (int i = scope.size() - 1; i >= 0; i--) {
			Object x = scope.get(i);
			if (x != null) {
				if (x instanceof Map<?, ?>) {
					Map<?, ?> map = (Map<?, ?>) x;

					if (map.containsKey(name)) {
						return map.get(name);
					}

				} else if (x instanceof Getter) {
					Getter getter = (Getter) x;

					return getter.get(name);

				} else {
					Prop prop = Beany.property(x, name, false);

					if (prop != null) {
						return prop.get(x);
					}
				}
			}
		}

		return null;
	}

}
