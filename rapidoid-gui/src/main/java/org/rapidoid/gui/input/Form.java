package org.rapidoid.gui.input;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.AnyObj;
import org.rapidoid.commons.Err;
import org.rapidoid.gui.Btn;
import org.rapidoid.gui.GUI;
import org.rapidoid.gui.base.AbstractWidget;
import org.rapidoid.html.FormLayout;
import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.FormTag;
import org.rapidoid.model.Item;
import org.rapidoid.model.Property;
import org.rapidoid.u.U;

import java.util.List;

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
public class Form extends AbstractWidget<Form> {

	protected final Item item;
	protected final FormMode mode;

	protected List<Property> props;

	protected List<Field> fields = U.list();
	protected List<Btn> buttons;

	protected FormLayout layout = FormLayout.VERTICAL;

	protected boolean hasFields = false;

	public Form(FormMode mode, Item item, String... properties) {
		this.mode = mode;
		this.item = item;
		init(item, properties);
	}

	/* FIELD */

	public int fieldIndex(String fieldName) {
		for (int i = 0; i < fields.size(); i++) {
			if (U.eq(fields.get(i).name, fieldName)) {
				return i;
			}
		}

		throw U.rte("Cannot find field '%s'!", fieldName);
	}

	public Form field(String fieldName, Field field) {
		return field(fieldIndex(fieldName), field);
	}

	public Form field(int fieldIndex, Field field) {
		this.fields.set(fieldIndex, field);
		return this;
	}

	public Field field(String fieldName) {
		return field(fieldIndex(fieldName));
	}

	public Field field(int fieldIndex) {
		return this.fields.get(fieldIndex);
	}

	/* BUTTONS */

	public Form buttons(Btn... buttons) {
		this.buttons = AnyObj.withoutNulls(buttons);
		return this;
	}

	public List<Btn> buttons() {
		return this.buttons;
	}

	/* OTHER */

	public Form add(Field field) {
		if (field.mode() == null) {
			field.mode(mode);
		}

		if (field.layout() == null) {
			field.layout(layout);
		}

		fields.add(field);
		return this;
	}

	protected void init(Item item, String... properties) {
		props = editable() ? item.editableProperties(properties) : item.readableProperties(properties);
		int propN = props.size();

		for (int i = 0; i < propN; i++) {
			Property prop = props.get(i);
			Field field = GUI.field(item, prop).mode(mode).layout(layout);
			fields.add(field);
		}
	}

	protected boolean editable() {
		return mode != FormMode.SHOW;
	}

	protected void initPermissions() {
		// if (item != null) {
		// Object target = item.value();
		// Class<?> targetClass = Cls.of(target);
		//
		// for (Field field : U.safe(fields)) {
		// // FIXME permissions for target class?
		// }
		// }
	}

	@Override
	protected FormTag render() {
		initPermissions();

		FormTag form = emptyForm();

		form = addFormFields(form);
		form = form.append(formButtons());

		return form;
	}

	protected FormTag addFormFields(FormTag form) {
		for (int i = 0; i < fields.size(); i++) {
			Field field = getField(i);
			if (field != null) {
				form = form.append(field);
				hasFields = true;
			}
		}

		if (!hasFields) {
			form = form.append(noFormFields());
		}

		return form;
	}

	protected Tag noFormFields() {
		return div("No details are available!");
	}

	protected Field getField(int index) {
		Field field = fields.get(index);

		if (field != null) {
			return field.isFieldAllowed() ? field : null;
		}

		return null;
	}

	protected FormTag emptyForm() {
		return GUI.form().class_(formLayoutClass(layout)).role("form");
	}

	protected Tag formButtons() {
		Tag btns;

		if (layout == FormLayout.HORIZONTAL) {
			btns = div().class_("col-sm-offset-4 col-sm-8");
		} else {
			btns = div().class_("form-group");
		}

		if (buttons != null) {
			if (hasFields) {
				for (Object btn : buttons) {
					btns = btns.append(btn);
				}
			}
		}

		if (layout == FormLayout.HORIZONTAL) {
			return div(btns).class_("form-group");
		} else {
			return btns;
		}
	}

	protected String formLayoutClass(FormLayout layout) {
		switch (layout) {
			case VERTICAL:
				return "";
			case HORIZONTAL:
				return "form-horizontal";
			case INLINE:
				return "form-inline";
			default:
				throw Err.notExpected();
		}
	}

}
