package org.rapidoid.goodies;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.GUI;
import org.rapidoid.model.Models;
import org.rapidoid.model.Property;
import org.rapidoid.u.U;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/*
 * #%L
 * rapidoid-web
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
public class DetailsHandler extends GUI implements Callable<Object> {

	private final String title;
	private final Object target;
	private final String[] properties;
	private volatile boolean sorted;

	public DetailsHandler(String title, Object target, String... properties) {
		this.title = title;
		this.target = target;
		this.properties = properties;
	}

	@Override
	public Object call() throws Exception {
		String[] targetProps;

		if (sorted) {
			List<String> props = U.list();

			for (Property prop : Models.item(target).properties(properties)) {
				props.add(prop.name());
			}

			Collections.sort(props);
			targetProps = U.arrayOf(String.class, props);

		} else {
			targetProps = properties;
		}

		return row(h1(title + ":"), show(target, targetProps));
	}

	public boolean sorted() {
		return sorted;
	}

	public DetailsHandler sorted(boolean sorted) {
		this.sorted = sorted;
		return this;
	}
}
