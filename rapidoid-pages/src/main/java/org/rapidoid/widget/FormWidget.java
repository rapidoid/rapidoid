package org.rapidoid.widget;

/*
 * #%L
 * rapidoid-pages
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import java.util.Collection;
import java.util.List;

import org.rapidoid.html.FieldType;
import org.rapidoid.html.FormLayout;
import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.ButtonTag;
import org.rapidoid.html.tag.FormTag;
import org.rapidoid.model.Item;
import org.rapidoid.model.Property;
import org.rapidoid.security.Secure;
import org.rapidoid.util.Cls;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;
import org.rapidoid.var.Var;

public class FormWidget extends AbstractWidget {

	protected final DataManager dataManager;
	protected final Item item;
	protected final FormMode mode;

	protected List<Property> props;

	protected List<FormFieldWidget> fields = U.list();
	protected List<Tag> buttons;

	protected FormLayout layout = FormLayout.VERTICAL;

	protected boolean hasFields = false;

	public FormWidget(DataManager dataManager, FormMode mode, Item item, String... properties) {
		this.dataManager = dataManager;
		this.mode = mode;
		this.item = item;
		init(item, properties);
	}

	public FormWidget(DataManager dataManager, FormMode mode, FormLayout layout, String[] fieldNames,
			String[] fieldLabels, FieldType[] fieldTypes, Collection<?>[] options, Var<?>[] vars, Tag[] buttons) {
		this.dataManager = dataManager;
		this.mode = mode;
		this.item = null;
		this.layout = layout;

		for (int i = 0; i < fieldNames.length; i++) {
			fields.add(new FormFieldWidget(dataManager, mode, layout, null, fieldNames[i], fieldLabels[i],
					fieldTypes[i], options[i], true, vars[i], null));
		}

		this.buttons = UTILS.withoutNulls(buttons);
	}

	/************************** FIELD ********************************/

	public int fieldIndex(String fieldName) {
		for (int i = 0; i < fields.size(); i++) {
			if (U.eq(fields.get(i).name, fieldName)) {
				return i;
			}
		}

		throw U.rte("Cannot find field '%s'!", fieldName);
	}

	public FormWidget field(String fieldName, FormFieldWidget field) {
		return field(fieldIndex(fieldName), field);
	}

	public FormWidget field(int fieldIndex, FormFieldWidget field) {
		this.fields.set(fieldIndex, field);
		return this;
	}

	public FormFieldWidget field(String fieldName) {
		return field(fieldIndex(fieldName));
	}

	public FormFieldWidget field(int fieldIndex) {
		return this.fields.get(fieldIndex);
	}

	/************************** BUTTONS ********************************/

	public FormWidget buttons(ButtonTag... buttons) {
		this.buttons = UTILS.withoutNulls(buttons);
		return this;
	}

	public List<Tag> buttons() {
		return this.buttons;
	}

	/************************** OTHER ********************************/

	public FormWidget add(FormFieldWidget field) {
		if (field.getMode() == null) {
			field.setMode(mode);
		}

		if (field.getLayout() == null) {
			field.setLayout(layout);
		}

		fields.add(field);
		return this;
	}

	protected void init(Item item, String... properties) {
		props = editable() ? item.editableProperties(properties) : item.readableProperties(properties);
		int propN = props.size();

		for (int i = 0; i < propN; i++) {
			Property prop = props.get(i);
			FormFieldWidget field = field(dataManager, mode, layout, item, prop);
			fields.add(field);
		}
	}

	protected boolean editable() {
		return mode != FormMode.SHOW;
	}

	protected void initPermissions() {
		if (item != null) {
			Object target = item.value();
			Class<?> targetClass = Cls.of(target);

			for (FormFieldWidget field : fields) {
				if (field.permissions == null) {
					field.permissions = Secure.getPropertyPermissions(Secure.username(), targetClass, target,
							field.name);
				}
			}
		}
	}

	@Override
	protected FormTag create() {
		initPermissions();

		FormTag form = emptyForm();

		form = addFormFields(form);
		form = form.append(formButtons());

		return form;
	}

	protected FormTag addFormFields(FormTag form) {
		for (int i = 0; i < fields.size(); i++) {
			FormFieldWidget field = getField(i);
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
		return h4("Insufficient permissions!");
	}

	protected FormFieldWidget getField(int index) {
		FormFieldWidget field = fields.get(index);

		if (field != null) {
			return field.isFieldAllowed() ? field : null;
		}

		return null;
	}

	protected FormTag emptyForm() {
		return form().class_(formLayoutClass(layout)).role("form");
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
			throw U.notExpected();
		}
	}

}
