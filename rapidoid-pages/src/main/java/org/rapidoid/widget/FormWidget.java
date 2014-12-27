package org.rapidoid.widget;

/*
 * #%L
 * rapidoid-pages
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

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.rapidoid.html.FieldType;
import org.rapidoid.html.FormLayout;
import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.ATag;
import org.rapidoid.html.tag.ButtonTag;
import org.rapidoid.html.tag.FormTag;
import org.rapidoid.html.tag.InputTag;
import org.rapidoid.html.tag.OptionTag;
import org.rapidoid.html.tag.SelectTag;
import org.rapidoid.html.tag.TextareaTag;
import org.rapidoid.model.Item;
import org.rapidoid.model.Property;
import org.rapidoid.util.Cls;
import org.rapidoid.util.TypeKind;
import org.rapidoid.util.U;
import org.rapidoid.var.Var;
import org.rapidoid.var.Vars;

public class FormWidget extends AbstractWidget {

	protected final DataManager dataManager;

	protected Tag[] buttons;
	protected FormLayout layout = FormLayout.VERTICAL;
	protected String[] fieldNames;
	protected String[] fieldLabels;
	protected FieldType[] fieldTypes;
	protected Collection<?>[] options;
	protected Var<?>[] vars;

	public FormWidget(DataManager dataManager, boolean editable, Item item, String... properties) {
		this.dataManager = dataManager;
		init(editable, item, properties);
	}

	public FormWidget(DataManager dataManager, FormLayout layout, String[] fieldNames, String[] fieldLabels,
			FieldType[] fieldTypes, Collection<?>[] options, Var<?>[] vars, Tag[] buttons) {
		this.dataManager = dataManager;
		this.layout = layout;
		this.fieldNames = fieldNames;
		this.fieldLabels = U.or(fieldLabels, Arrays.copyOf(fieldNames, fieldNames.length));
		this.fieldTypes = fieldTypes;
		this.options = options;
		this.vars = vars;
		this.buttons = buttons;
	}

	public FormWidget buttons(ButtonTag... buttons) {
		this.buttons = buttons;
		return this;
	}

	public FormWidget fieldType(String fieldName, FieldType fieldType) {
		return fieldType(fieldIndex(fieldName), fieldType);
	}

	public FormWidget fieldType(int fieldIndex, FieldType fieldType) {
		fieldTypes[fieldIndex] = fieldType;
		return this;
	}

	public FieldType fieldType(int fieldIndex) {
		return fieldTypes[fieldIndex];
	}

	public FormWidget fieldLabel(String fieldName, String fieldLabel) {
		return fieldLabel(fieldIndex(fieldName), fieldLabel);
	}

	public FormWidget fieldLabel(int fieldIndex, String fieldLabel) {
		fieldLabels[fieldIndex] = fieldLabel;
		return this;
	}

	public String fieldLabel(int fieldIndex) {
		return fieldLabels[fieldIndex];
	}

	public int fieldIndex(String fieldName) {
		for (int i = 0; i < fieldNames.length; i++) {
			if (fieldNames[i].equals(fieldName)) {
				return i;
			}
		}

		throw U.rte("Cannot find field '%s'!", fieldName);
	}

	protected void init(boolean editable, Item item, String... properties) {
		final List<Property> props = editable ? item.editableProperties(properties) : item
				.readableProperties(properties);

		int propN = props.size();

		fieldNames = new String[propN];
		fieldLabels = new String[propN];
		fieldTypes = new FieldType[propN];
		options = new Collection<?>[propN];
		vars = new Var[propN];

		for (int i = 0; i < propN; i++) {
			Property prop = props.get(i);
			fieldNames[i] = prop.name();
			fieldLabels[i] = prop.caption();
			fieldTypes[i] = editable ? getPropertyFieldType(prop) : FieldType.LABEL;
			options[i] = getPropertyOptions(prop);
			vars[i] = property(item, prop.name());
		}
	}

	protected FieldType getPropertyFieldType(Property prop) {
		Class<?> type = prop.type();

		if (type.isEnum()) {
			return type.getEnumConstants().length <= 3 ? FieldType.RADIOS : FieldType.DROPDOWN;
		}

		if (prop.name().toLowerCase().contains("email")) {
			return FieldType.EMAIL;
		}

		if (Collection.class.isAssignableFrom(type)) {
			return FieldType.MULTI_SELECT;
		}

		if (Cls.kindOf(type) == TypeKind.OBJECT) {
			return FieldType.DROPDOWN;
		}

		return FieldType.TEXT;
	}

	protected Collection<?> getPropertyOptions(Property prop) {
		Class<?> type = prop.type();

		if (type.isEnum()) {
			return U.list(type.getEnumConstants());
		}

		if (Collection.class.isAssignableFrom(type)) {
			return getCollectionPropertyOptions(prop);
		}

		if (Var.class.isAssignableFrom(type)) {
			return getVarPropertyOptions(prop);
		}

		if (Cls.kindOf(type) == TypeKind.OBJECT) {
			return Collections.EMPTY_LIST;
		}

		return null;
	}

	protected Collection<?> getCollectionPropertyOptions(Property prop) {
		Type[] typeArgs = prop.genericType().getActualTypeArguments();
		return typeArgs.length == 1 ? getOptionsOfType(Cls.clazz(typeArgs[0])) : Collections.EMPTY_LIST;
	}

	protected Collection<?> getVarPropertyOptions(Property prop) {
		Type[] typeArgs = prop.genericType().getActualTypeArguments();
		return typeArgs.length == 1 ? getOptionsOfType(Cls.clazz(typeArgs[0])) : Collections.EMPTY_LIST;
	}

	protected Collection<?> getOptionsOfType(Class<?> clazz) {
		if (Cls.kindOf(clazz) == TypeKind.OBJECT && Cls.hasProperty(clazz, "id")) {
			return dataManager != null ? dataManager.getAll(clazz) : Collections.EMPTY_LIST;
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	@Override
	protected FormTag create() {
		U.notNull(fieldNames, "field names");
		fieldLabels = U.or(fieldLabels, fieldNames);
		U.must(fieldNames.length == fieldLabels.length);

		FormTag form = emptyForm();

		form = addFormFields(form);

		form = form.append(formButtons());

		return form;
	}

	protected FormTag addFormFields(FormTag form) {
		for (int i = 0; i < fieldNames.length; i++) {
			form = form.append(field(fieldNames[i], fieldLabels[i], fieldTypes[i], options[i], vars[i]));
		}
		return form;
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

		for (Object btn : buttons) {
			btns = btns.append(btn);
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

	protected Tag field(String name, String desc, FieldType type, Collection<?> options, Var<?> var) {
		desc = U.or(desc, name) + ":";

		Object inp = input_(name, desc, type, options, var);
		Tag label;
		Object inputWrap;

		if (type == FieldType.RADIOS || type == FieldType.CHECKBOXES) {
			inp = layout == FormLayout.VERTICAL ? div(inp) : span(inp);
		}

		if (type == FieldType.CHECKBOX) {
			label = null;

			inp = div(label(inp, desc)).class_("checkbox");

			inputWrap = layout == FormLayout.HORIZONTAL ? div(inp).class_("col-sm-offset-4 col-sm-8") : inp;

		} else {
			if (layout != FormLayout.INLINE) {
				label = label(desc);
			} else {
				if (type == FieldType.RADIOS) {
					label = label(desc);
				} else {
					label = null;
				}
			}

			if (layout == FormLayout.HORIZONTAL) {
				label = label.class_("col-sm-4 control-label");
			}

			inputWrap = layout == FormLayout.HORIZONTAL ? div(inp).class_("col-sm-8") : inp;
		}

		Tag group = label != null ? div(label, inputWrap) : div(inputWrap);
		group = group.class_("form-group");
		return group;
	}

	protected Object input_(String name, String desc, FieldType type, Collection<?> options, Var<?> var) {

		switch (type) {

		case TEXT:
			return textInput(name, desc, var);

		case PASSWORD:
			return passwordInput(name, desc, var);

		case EMAIL:
			return emailInput(name, desc, var);

		case TEXTAREA:
			return textareaInput(name, desc, var);

		case CHECKBOX:
			return checkboxInput(name, var);

		case DROPDOWN:
			return dropdownInput(name, options, var);

		case MULTI_SELECT:
			return multiSelectInput(name, options, var);

		case RADIOS:
			return radiosInput(name, options, var);

		case CHECKBOXES:
			return checkboxesInput(name, options, var);

		case LABEL:
			return readonlyInput(var);

		default:
			throw U.notExpected();
		}
	}

	protected Object readonlyInput(Var<?> var) {
		Object value = var.get();

		if (value instanceof Collection) {
			Collection<?> coll = (Collection<?>) value;

			Tag wrap = div();
			for (Object item : coll) {
				Tag icon = awesome("circle-o");
				ATag itemLink = a(item).href("/").class_("value-display");
				wrap = wrap.append(div(icon, " ", itemLink).class_("value-line"));
			}

			return wrap;
		}
		return span(value).class_("value-display");
	}

	protected Object checkboxesInput(String name, Collection<?> options, Var<?> var) {
		U.notNull(options, "checkboxes options");
		Object[] checkboxes = new Object[options.size()];
		int i = 0;
		for (Object opt : options) {
			Var<Boolean> optVar = Vars.has(var, opt);
			InputTag cc = input().type("checkbox").name(name).value(str(opt)).bind(optVar);
			checkboxes[i] = label(cc, opt).class_("radio-checkbox");
			i++;
		}
		return checkboxes;
	}

	protected Object radiosInput(String name, Collection<?> options, Var<?> var) {
		U.notNull(options, "radios options");
		Object[] radios = new Object[options.size()];

		int i = 0;
		for (Object opt : options) {
			Var<Boolean> optVar = Vars.eq(var, opt);
			InputTag radio = input().type("radio").name(name).value(str(opt)).bind(optVar);
			radios[i] = label(radio, opt).class_("radio-inline");
			i++;
		}
		return radios;
	}

	protected Object multiSelectInput(String name, Collection<?> options, Var<?> var) {
		U.notNull(options, "multi-select options");
		SelectTag select = select().name(name).class_("form-control").multiple(true);
		for (Object opt : options) {
			Var<Boolean> optVar = Vars.has(var, opt);
			OptionTag op = option(opt).value(str(opt)).bind(optVar);
			select = select.append(op);
		}
		return select;
	}

	protected Object dropdownInput(String name, Collection<?> options, Var<?> var) {
		U.notNull(options, "dropdown options");
		SelectTag dropdown = select().name(name).class_("form-control").multiple(false);
		for (Object opt : options) {
			Var<Boolean> optVar = Vars.eq(var, opt);
			OptionTag op = option(opt).value(str(opt)).bind(optVar);
			dropdown = dropdown.append(op);
		}
		return dropdown;
	}

	protected Object checkboxInput(String name, Var<?> var) {
		InputTag input;
		input = input().type("checkbox").name(name).bind(var);
		return input;
	}

	protected Object textareaInput(String name, String desc, Var<?> var) {
		TextareaTag textarea = textarea().class_("form-control").name(name).bind(var);
		textarea = layout == FormLayout.INLINE ? textarea.placeholder(desc) : textarea;
		return textarea;
	}

	protected Object emailInput(String name, String desc, Var<?> var) {
		InputTag input;
		input = input().type("email").class_("form-control").name(name).bind(var);
		input = layout == FormLayout.INLINE ? input.placeholder(desc) : input;
		return input;
	}

	protected Object passwordInput(String name, String desc, Var<?> var) {
		InputTag input;
		input = input().type("password").class_("form-control").name(name).bind(var);
		input = layout == FormLayout.INLINE ? input.placeholder(desc) : input;
		return input;
	}

	protected Object textInput(String name, String desc, Var<?> var) {
		InputTag input;
		input = input().type("text").class_("form-control").name(name).bind(var);
		input = layout == FormLayout.INLINE ? input.placeholder(desc) : input;
		return input;
	}

}
