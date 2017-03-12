package org.rapidoid.model.impl;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Str;
import org.rapidoid.model.Item;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;

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
public class BeanProperty extends AbstractProperty {

	private static final long serialVersionUID = 7627370931428864929L;

	private final String name;

	private final Class<?> type;

	private final ParameterizedType genericType;

	private final String caption;

	private final Annotation[] annotations;

	public BeanProperty(String name, Class<?> type, ParameterizedType genericType, Annotation[] annotations) {
		this(name, type, genericType, annotations, pretty(name));
	}

	public BeanProperty(String name, Class<?> type, ParameterizedType genericType, Annotation[] annotations,
	                    String caption) {
		this.name = name;
		this.type = type;
		this.annotations = annotations;
		this.caption = caption;
		this.genericType = genericType;
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

	@Override
	public Annotation[] annotations() {
		return annotations;
	}

	@Override
	public ParameterizedType genericType() {
		return genericType;
	}

	static String pretty(String prop) {
		if (prop.startsWith("_")) {
			prop = prop.substring(1);
			if (prop.equals("toString") || prop.equals("str")) {
				prop = "data";
			}
		}

		if (prop.equals("id")) {
			return "ID";
		}

		return Str.phrase(prop);
	}

	@Override
	public String toString() {
		return "BeanProperty [name=" + name + ", type=" + type + ", genericType=" + genericType + ", caption="
			+ caption + "]";
	}

	@Override
	public Object get(Item item) {
		return item.get(name);
	}

}
