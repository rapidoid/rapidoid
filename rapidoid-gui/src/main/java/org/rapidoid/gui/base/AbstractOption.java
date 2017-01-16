package org.rapidoid.gui.base;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.GUI;

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
public abstract class AbstractOption<W extends AbstractOption<?>> extends AbstractInput<W> {

	protected Object value = true;

	protected String label;

	protected boolean checked;

	protected Object render(String type) {
		return GUI.input().type(type)
			.class_("pretty")
			.name(_name())
			.value(str(value))
			.checked(picked(value, checked))
			.data("label", label); // OLD style: label(cc, opt).class_("radio-checkbox");
	}

	public boolean checked() {
		return checked;
	}

	public W checked(boolean checked) {
		this.checked = checked;
		return me();
	}

	public String label() {
		return label;
	}

	public W label(String label) {
		this.label = label;
		return me();
	}

	public Object value() {
		return value;
	}

	public W value(Object value) {
		this.value = value;
		return me();
	}
}
