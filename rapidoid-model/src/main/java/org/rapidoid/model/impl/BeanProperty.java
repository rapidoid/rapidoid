package org.rapidoid.model.impl;

/*
 * #%L
 * rapidoid-model
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

import org.rapidoid.model.Property;
import org.rapidoid.util.U;

public class BeanProperty implements Property {

	private static final long serialVersionUID = 7627370931428864929L;

	private final String name;

	private final Class<?> type;

	private final String caption;

	public BeanProperty(String name, Class<?> type) {
		this(name, type, pretty(name));
	}

	public BeanProperty(String name, Class<?> type, String caption) {
		this.name = name;
		this.type = type;
		this.caption = caption;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Class<?> type() {
		return type;
	}

	@Override
	public String caption() {
		return caption;
	}

	private static String pretty(String prop) {
		if (prop.equals("id")) {
			return "ID";
		}
		return U.camelPhrase(prop);
	}

}
