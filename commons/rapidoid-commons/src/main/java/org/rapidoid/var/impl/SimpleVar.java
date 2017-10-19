package org.rapidoid.var.impl;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

/*
 * #%L
 * rapidoid-commons
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
public class SimpleVar<T> extends AbstractVar<T> {

	private static final long serialVersionUID = 7970150705828178233L;

	private volatile T value;

	public SimpleVar(String name, T value) {
		super(name);
		this.value = value;
	}

	@Override
	public T get() {
		return value;
	}

	@Override
	public void doSet(T value) {
		this.value = value;
	}

}
