/*-
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
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

package org.rapidoid.group;


import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.BeanProperties;
import org.rapidoid.beany.Beany;
import org.rapidoid.beany.Prop;
import org.rapidoid.cls.Cls;
import org.rapidoid.cls.TypeKind;
import org.rapidoid.commons.Str;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.concurrent.Callbacks;
import org.rapidoid.concurrent.Promise;
import org.rapidoid.concurrent.Promises;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public abstract class AbstractManageable extends RapidoidThing implements Manageable {

	@Override
	public Object runManageableAction(String action) {
		Method method = Cls.findMethod(source().getClass(), Str.uncapitalized(action));

		if (method != null) {
			return Cls.invoke(method, source());

		} else {
			return doManageableAction(action);
		}
	}

	@Override
	public List<String> getManageableActions() {
		Object source = source();
		List<String> actions = U.list();

		List<Method> actionMethods = Cls.getMethodsAnnotated(source.getClass(), Action.class);

		// sort the actions by @Order
		Msc.sortByOrder(actionMethods);

		for (Method method : actionMethods) {
			Action action = method.getAnnotation(Action.class);
			actions.add(!action.name().isEmpty() ? action.name() : method.getName());
		}

		for (Method method : Cls.getMethodsAnnotated(source.getClass(), ActionCondition.class)) {
			ActionCondition condition = method.getAnnotation(ActionCondition.class);

			U.must(method.getReturnType() == boolean.class, "The method return type must be boolean: " + method);

			boolean cond = Cls.invoke(method, source);

			if (!cond) {
				String name;

				if (!condition.name().isEmpty()) {
					name = condition.name();

				} else {
					name = method.getName();
					U.must(name.startsWith("can"), "The action condition method must start with 'can', to infer the action name!");
					name = Str.uncapitalized(Str.triml(name, "can"));
				}

				actions.remove(name);
			}
		}

		return actions;
	}

	@Override
	public List<String> getManageableProperties() {
		BeanProperties props = Beany.propertiesOf(source());

		List<String> ps = U.list();

		for (Prop prop : props) {
			if (!prop.getName().contains("manageable")) {
				TypeKind kind = Cls.kindOf(prop.getType());

				if (kind.isPrimitive() || kind.isNumber() || kind.isArray()
					|| kind == TypeKind.STRING || kind == TypeKind.DATE) {
					ps.add(prop.getName());
				}
			}
		}

		return ps;
	}

	protected Object source() {
		return this;
	}

	@Override
	public Map<String, List<Manageable>> getManageableChildren() {
		return U.map();
	}

	protected Object doManageableAction(String action) {
		throw U.rte("Cannot handle action '%s'!", action);
	}

	@Override
	public GroupOf<? extends Manageable> group() {
		return null;
	}

	@Override
	public String kind() {
		return Manageables.kindOf(source().getClass());
	}

	protected void doReloadManageable(Callback<Void> callback) {
		Callbacks.done(callback, null, null);
	}

	@Override
	public final void reloadManageable() {
		final Promise<Void> promise = Promises.create();

		doReloadManageable(promise);

		try {
			promise.get(5000);
		} catch (TimeoutException e) {
			Log.error("Couldn't reload the manageable!", e);
		}
	}

	@Override
	public Object getManageableDisplay() {
		return null;
	}
}
