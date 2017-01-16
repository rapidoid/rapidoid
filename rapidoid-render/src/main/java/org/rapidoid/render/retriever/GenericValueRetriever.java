package org.rapidoid.render.retriever;

/*
 * #%L
 * rapidoid-render
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.BeanProp;
import org.rapidoid.beany.Beany;
import org.rapidoid.beany.Prop;
import org.rapidoid.render.Getter;
import org.rapidoid.u.U;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.2.0")
public class GenericValueRetriever extends RapidoidThing implements ValueRetriever {

	private final String property;

	private volatile CachedPropRetriever cachedPropRetriever;

	public GenericValueRetriever(String property) {
		this.property = property;
	}

	@Override
	public Object retrieve(List<Object> model) {
		if (U.isEmpty(model)) return null;
		return getProp(model);
	}

	private Object getProp(List<Object> model) {
		Object target = U.last(model);
		Class<?> cls = target.getClass();

		CachedPropRetriever cachedProp = cachedPropRetriever;
		if (cachedProp != null && cachedProp.canRetrieve(cls)) {
			return cachedProp.retrieve(target);
		}

		if (!(target instanceof Map)) {
			Prop prop = Beany.property(target, property, false);

			if (prop != null && prop instanceof BeanProp) {
				cachedPropRetriever = new CachedPropRetriever(cls, prop);
				return prop.getFast(target);
			}
		}

		return propOf(property, model);
	}

	public static Object self(List<Object> model) {
		return model.get(model.size() - 1);
	}

	public static Object propOf(String name, List<Object> scope) {
		int p = name.indexOf(".");

		if (p == 0) {
			U.must(name.length() == 1, "Invalid expression!");
			return self(scope);
		}

		if (p > 0) {
			Object first = propOf(name.substring(0, p), scope);
			return propOf(name.substring(p + 1), Collections.singletonList(first));
		}

		for (int i = scope.size() - 1; i >= 0; i--) {
			Object x = scope.get(i);

			if (x != null) {
				Object value = singleModelProp(name, x);
				if (value != null) return value;
			}
		}

		return null;
	}

	public static Object singleModelProp(String name, Object model) {

		if (model instanceof Map<?, ?>) {
			Map<?, ?> map = (Map<?, ?>) model;
			return map.get(name);
		}

		if (model instanceof Getter) {
			Getter getter = (Getter) model;
			return getter.get(name);
		}

		// process as bean
		Prop prop = Beany.property(model, name, false);

		if (prop != null) {
			return prop.get(model);
		}

		return null; // not found
	}

}
