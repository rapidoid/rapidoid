package org.rapidoid.gui.base;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.AnyObj;
import org.rapidoid.gui.GUI;
import org.rapidoid.u.U;
import org.rapidoid.var.Var;

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
@Since("5.1.0")
public abstract class AbstractInput<W extends AbstractInput<?>> extends AbstractWidget<W> {

	protected Var<?> var;

	protected String name;

	public Var<?> var() {
		return var;
	}

	public W var(Var<?> var) {
		this.var = var;
		return me();
	}

	public String name() {
		return name;
	}

	public W name(String name) {
		this.name = name;
		return me();
	}

	protected static boolean has(Var<?> var, Object value) {
		return var != null && AnyObj.contains(var.get(), str(value));
	}

	protected static String str(Object value) {
		return GUI.str(value);
	}

	protected Var<?> _var() {
		return var != null ? var : U.notEmpty(name) ? GUI.var(name) : null;
	}

	protected Object _valOr(Object initial) {
		Var<?> _var = _var();
		Object value = _var != null ? _var.get() : null;
		return U.or(value, initial);
	}

	protected String _strVal(Object initial) {
		Var<?> _var = _var();
		Object value = _var != null ? _var.get() : null;
		return str(U.or(value, initial, ""));
	}

	protected String _name() {
		return U.or(name, var != null ? var.name() : null);
	}

	protected boolean picked(Object value, boolean initial) {
		return req().isGetReq() ? initial : has(_var(), value);
	}

}
