package org.rapidoid.widget.impl;

/*
 * #%L
 * rapidoid-widget
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.util.ImportExport;
import org.rapidoid.var.impl.AbstractVar;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class SessionVar<T> extends AbstractVar<T> {

	private static final long serialVersionUID = 2761159925375675659L;

	private final HttpExchange ctx;

	private final String name;

	private final T defaultValue;

	public SessionVar(HttpExchange ctx, ImportExport props) {
		this.ctx = ctx;
		name = props.get(A);
		defaultValue = props.get(B);
	}

	public SessionVar(HttpExchange ctx, String name, T defaultValue) {
		this.ctx = ctx;
		this.name = name;
		this.defaultValue = defaultValue;
	}

	@Override
	public T get() {
		T val = ctx.session(name, null);

		if (val == null) {
			ctx.sessionSet(name, defaultValue);
			val = defaultValue;
		}

		return val;
	}

	@Override
	public void set(T value) {
		ctx.sessionSet(name, value);
	}

	@Override
	public void exportTo(ImportExport props) {
		props.put(A, name);
		props.put(B, defaultValue);
	}

}
