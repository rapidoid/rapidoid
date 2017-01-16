package org.rapidoid.model.impl;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.Calc;
import org.rapidoid.lambda.Lmbd;
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
@Since("2.2.0")
public class CalcProperty extends AbstractProperty {

	private static final long serialVersionUID = -2697490242616488024L;

	private final String name;

	private Calc<Object> calc;

	public CalcProperty(String name, Calc<Object> calc) {
		this.name = name;
		this.calc = calc;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Class<?> type() {
		return Object.class;
	}

	@Override
	public String caption() {
		return name;
	}

	public Annotation[] annotations() {
		return null;
	}

	@Override
	public ParameterizedType genericType() {
		return null;
	}

	@Override
	public String toString() {
		return "CalcProperty [name=" + name + ", calc=" + calc + "]";
	}

	@Override
	public Object get(Item item) {
		return Lmbd.eval(calc, item.value());
	}

}
