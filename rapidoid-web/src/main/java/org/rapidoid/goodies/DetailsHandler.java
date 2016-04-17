package org.rapidoid.goodies;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.GUI;

import java.util.concurrent.Callable;

/*
 * #%L
 * rapidoid-web
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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class DetailsHandler extends GUI implements Callable<Object> {

	private final String title;
	private final Object target;
	private final String[] properties;

	public DetailsHandler(String title, Object target, String... properties) {
		this.title = title;
		this.target = target;
		this.properties = properties;
	}

	@Override
	public Object call() throws Exception {
		return row(h1(title + ":"), show(target, properties));
	}

}
