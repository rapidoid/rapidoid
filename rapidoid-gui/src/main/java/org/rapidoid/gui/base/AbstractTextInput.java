package org.rapidoid.gui.base;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.GUI;
import org.rapidoid.html.Tag;

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
public abstract class AbstractTextInput<W extends AbstractTextInput<?>> extends AbstractInput<W> {

	protected String placeholder;

	protected String initial;

	public String placeholder() {
		return placeholder;
	}

	public W placeholder(String placeholder) {
		this.placeholder = placeholder;
		return me();
	}

	public String initial() {
		return initial;
	}

	public W initial(String value) {
		this.initial = value;
		return me();
	}

	protected Tag renderInput(String type) {
		return GUI.input().type(type)
			.class_("form-control")
			.name(_name())
			.value(_strVal(initial))
			.placeholder(placeholder);
	}

}
