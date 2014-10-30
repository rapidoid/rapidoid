package org.rapidoid.pages.impl;

/*
 * #%L
 * rapidoid-pages
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

import org.rapidoid.pages.Var;

public class VarImpl<T> implements Var<T> {

	private volatile T value;

	public VarImpl(T value) {
		this.value = value;
	}

	@Override
	public T get() {
		return value;
	}

	@Override
	public void set(T value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return StringEscapeUtils.escapeHtml4(String.valueOf(value));
	}

}
