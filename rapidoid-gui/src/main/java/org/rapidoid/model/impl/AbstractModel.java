package org.rapidoid.model.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.model.IModel;
import org.rapidoid.u.U;
import org.rapidoid.var.Var;
import org.rapidoid.var.Vars;

import java.util.Map;

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
public abstract class AbstractModel extends RapidoidThing implements IModel {

	private static final long serialVersionUID = -7147599758816052755L;

	protected final Map<String, Object> extras = U.map();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getExtra(String name, T defaultValue) {
		return (T) U.or(extras.get(name), defaultValue);
	}

	@Override
	public void setExtra(String name, Object value) {
		extras.put(name, value);
	}

	@Override
	public <T> Var<T> var(String name, T defaultValue) {
		Var<T> var = getExtra(name, null);

		if (var == null) {
			var = Vars.var("model(" + name + ")", defaultValue);
			setExtra(name, var);
		}

		return var;
	}

}
