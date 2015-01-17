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

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.rapidoid.annotation.Optional;
import org.rapidoid.annotation.Programmatic;
import org.rapidoid.beany.Beany;
import org.rapidoid.html.FieldType;
import org.rapidoid.html.FormLayout;
import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.ButtonTag;
import org.rapidoid.html.tag.FormTag;
import org.rapidoid.html.tag.InputTag;
import org.rapidoid.html.tag.TextareaTag;
import org.rapidoid.model.Item;
import org.rapidoid.model.Property;
import org.rapidoid.security.DataPermissions;
import org.rapidoid.security.Secure;
import org.rapidoid.util.Cls;
import org.rapidoid.util.Metadata;
import org.rapidoid.util.TypeKind;
import org.rapidoid.util.U;
import org.rapidoid.var.Var;
import org.rapidoid.var.Vars;

public class FormWidget extends AbstractWidget {

	protected final DataManager dataManager;
	protected final Item item;
	protected final FormMode mode;

	protected List<Property> props;

	protected Tag[] buttons;
	protected FormLayout layout = FormLayout.VERTICAL;
	protected String[] fieldNames;
	protected String[] fieldLabels;
	protected FieldType[] fieldTypes;
	protected Collection<?>[] fieldOptions;
	protected boolean[] fieldRequired;
	protected Var<?>[] vars;
	protected DataPermissions[] permissions;
	protected Tag[] fields;

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
		this.fieldNames = fieldNames;
		this.fieldLabels = U.or(fieldLabels, Arrays.copyOf(fieldNames, fieldNames.length));
		this.fieldTypes = fieldTypes;
		this.fieldOptions = options;
		this.vars = vars;
		this.buttons = buttons;
	}

	/************************** FIELD ********************************/

	public FormWidget field(String fieldName, Tag field) {
		return field(fieldIndex(fieldName), field);
	}

	public FormWidget field(int fieldIndex, Tag field) {
		this.fields[fieldIndex] = field;
		return this;
	}

	public Tag field(String fieldName) {
		return field(fieldIndex(fieldName));
	}

	public Tag field(int fieldIndex) {
		return this.fields[fieldIndex];
	}

	/************************** FIELD LABEL ********************************/

	public FormWidget fieldLabel(String fieldName, String fieldLabel) {
		return fieldLabel(fieldIndex(fieldName), fieldLabel);
	}

	public FormWidget fieldLabel(int fieldIndex, String fieldLabel) {
		this.fieldLabels[fieldIndex] = fieldLabel;
		return this;
	}

	public String fieldLabel(String fieldName) {
		return fieldLabel(fieldIndex(fieldName));
	}

	public String fieldLabel(int fieldIndex) {
		return this.fieldLabels[fieldIndex];
	}

	/************************** FIELD TYPE ********************************/

	public FormWidget fieldType(String fieldName, FieldType fieldType) {
		return fieldType(fieldIndex(fieldName), fieldType);
	}

	public FormWidget fieldType(int fieldIndex, FieldType fieldType) {
		this.fieldTypes[fieldIndex] = fieldType;
		return this;
	}

	public String fieldType(String fieldName) {
		return fieldLabel(fieldType(fieldName));
	}

	public FieldType fieldType(int fieldIndex) {
		return this.fieldTypes[fieldIndex];
	}

	/************************** FIELD OPTIONS ********************************/

	public FormWidget fieldOptions(String fieldName, Collection<?> fieldOptions) {
		return fieldOptions(fieldIndex(fieldName), fieldOptions);
	}

	public FormWidget fieldOptions(int fieldIndex, Collection<?> fieldOptions) {
		this.fieldOptions[fieldIndex] = fieldOptions;
		return this;
	}

	public Collection<?> fieldOptions(String fieldName) {
		return fieldOptions(fieldIndex(fieldName));
	}

	public Collection<?> fieldOptions(int fieldIndex) {
		return this.fieldOptions[fieldIndex];
	}

	/************************** FIELD REQUIRED ********************************/

	public FormWidget fieldRequired(String fieldName, boolean fieldRequired) {
		return fieldRequired(fieldIndex(fieldName), fieldRequired);
	}

	public FormWidget fieldRequired(int fieldIndex, boolean fieldRequired) {
		this.fieldRequired[fieldIndex] = fieldRequired;
		return this;
	}

	public boolean fieldRequired(String fieldName) {
		return fieldRequired(fieldIndex(fieldName));
	}

	public boolean fieldRequired(int fieldIndex) {
		return this.fieldRequired[fieldIndex];
	}

	/************************** FIELD PERMISSIONS ********************************/

	public FormWidget permissions(String fieldName, DataPermissions permissions) {
		return permissions(fieldIndex(fieldName), permissions);
	}

	public FormWidget permissions(int fieldIndex, DataPermissions permissions) {
		this.permissions[fieldIndex] = permissions;
		return this;
	}

	public DataPermissions permissions(String fieldName) {
		return permissions(fieldIndex(fieldName));
	}

	public DataPermissions permissions(int fieldIndex) {
		return this.permissions[fieldIndex];
	}

	/************************** FIELD VARS ********************************/

	public FormWidget vars(String fieldName, Var<?> vars) {
		return vars(fieldIndex(fieldName), vars);
	}

	public FormWidget vars(int fieldIndex, Var<?> vars) {
		this.vars[fieldIndex] = vars;
		return this;
	}

	public Var<?> vars(String fieldName) {
		return vars(fieldIndex(fieldName));
	}

	public Var<?> vars(int fieldIndex) {
		return this.vars[fieldIndex];
	}

	/************************** BUTTONS ********************************/

	public FormWidget buttons(ButtonTag... buttons) {
		this.buttons = buttons;
		return this;
	}

	public Tag[] buttons() {
		return this.buttons;
	}

	/************************** OTHER ********************************/

	public int fieldIndex(String fieldName) {
		for (int i = 0; i < fieldNames.length; i++) {
			if (fieldNames[i].equals(fieldName)) {
				return i;
			}
		}

		throw U.rte("Cannot find field '%s'!", fieldName);
	}

	public FormWidget addField(String fieldName, Tag field) {
		throw U.notReady();
	}

	protected void init(Item item, String... properties) {

		props = editable() ? item.editableProperties(properties) : item.readableProperties(properties);

		int propN = props.size();

		fieldNames = new String[propN];
		fieldLabels = new String[propN];
		fieldTypes = new FieldType[propN];
		fieldOptions = new Collection<?>[propN];
		fieldRequired = new boolean[propN];
		vars = new Var[propN];
		permissions = new DataPermissions[propN];
		fields = new Tag[propN];

		for (int i = 0; i < propN; i++) {
			Property prop = props.get(i);
			fieldNames[i] = prop.name();
			fieldLabels[i] = prop.caption();
			fieldTypes[i] = editable() ? getPropertyFieldType(prop) : FieldType.LABEL;
			fieldOptions[i] = getPropertyOptions(prop);
			fieldRequired[i] = Metadata.get(prop.annotations(), Optional.class) == null;
			vars[i] = property(item, prop.name());
		}
	}

	protected boolean editable() {
		return mode != FormMode.SHOW;
	}

	protected void initPermissions() {
		if (item != null) {
			Object target = item.value();
			Class<?> targetClass = Cls.of(target);

			for (int i = 0; i < fieldNames.length; i++) {
				if (permissions[i] == null) {
					permissions[i] = Secure.getPropertyPermissions(exchange().username(), targetClass, target,
							fieldNames[i]);
				}
			}
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

		if (Cls.kindOf(type) == TypeKind.OBJECT) {
			return Collections.EMPTY_LIST;
		}

		return null;
	}

	protected Collection<?> getCollectionPropertyOptions(Property prop) {
		return propertyOptions(prop);
	}

	protected Collection<?> propertyOptions(Property prop) {
		if (prop.genericType() != null) {
			Type[] typeArgs = prop.genericType().getActualTypeArguments();
			return typeArgs.length == 1 ? getOptionsOfType(Cls.clazz(typeArgs[0])) : Collections.EMPTY_LIST;
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	protected Collection<?> getOptionsOfType(Class<?> clazz) {
		if (Cls.kindOf(clazz) == TypeKind.OBJECT && Beany.hasProperty(clazz, "id")) {
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

		initPermissions();

		FormTag form = emptyForm();

		form = addFormFields(form);

		form = form.append(formButtons());

		return form;
	}

	protected FormTag addFormFields(FormTag form) {
		for (int i = 0; i < fieldNames.length; i++) {
			Tag field = getField(i);
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

	protected Tag getField(int index) {
		if (!isFieldAllowed(index)) {
			return null;
		}

		if (fields[index] != null) {
			return fields[index];
		}

		if (!isFieldProgrammatic(index) || mode == FormMode.SHOW) {
			Var<?> var = vars[index];
			if (fieldRequired[index]) {
				var = Vars.mandatory(var);
			}

			return field(fieldNames[index], fieldLabels[index], fieldTypes[index], fieldOptions[index], var);
		}

		return null;
	}

	protected boolean isFieldProgrammatic(int index) {
		return Metadata.get(props.get(index).annotations(), Programmatic.class) != null;
	}

	protected boolean isFieldAllowed(int index) {
		DataPermissions perm = permissions[index];

		switch (fieldMode(index)) {
		case CREATE:
			return perm.insert;

		case EDIT:
			return perm.read && perm.change;

		case SHOW:
			return perm.read;

		default:
			throw U.notExpected();
		}
	}

	protected FormMode fieldMode(int index) {
		return fieldTypes[index] != FieldType.LABEL ? mode : FormMode.SHOW;
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

		Tag err = span("").class_("field-error");
		Tag group = label != null ? div(label, inputWrap, err) : div(inputWrap, err);
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
			return readonly(var);

		default:
			throw U.notExpected();
		}
	}

	protected Object readonly(Object item) {
		return display(item);
	}

	protected Object checkboxesInput(String name, Collection<?> options, Var<?> var) {
		return checkboxes(name, options, var);
	}

	protected Object radiosInput(String name, Collection<?> options, Var<?> var) {
		return radios(name, options, var);
	}

	protected Object multiSelectInput(String name, Collection<?> options, Var<?> var) {
		return multiSelect(options, var).name(name);
	}

	protected Object dropdownInput(String name, Collection<?> options, Var<?> var) {
		return dropdown(options, var).name(name);
	}

	protected Object checkboxInput(String name, Var<?> var) {
		return checkbox(var).name(name);
	}

	protected Object textareaInput(String name, String desc, Var<?> var) {
		TextareaTag textarea = textareaInput(var).name(name);
		textarea = layout == FormLayout.INLINE ? textarea.placeholder(desc) : textarea;
		return textarea;
	}

	protected Object emailInput(String name, String desc, Var<?> var) {
		InputTag input;
		input = emailInput(var).name(name);
		input = layout == FormLayout.INLINE ? input.placeholder(desc) : input;
		return input;
	}

	protected Object passwordInput(String name, String desc, Var<?> var) {
		InputTag input;
		input = passwordInput(var).name(name);
		input = layout == FormLayout.INLINE ? input.placeholder(desc) : input;
		return input;
	}

	protected Object textInput(String name, String desc, Var<?> var) {
		InputTag input;
		input = textInput(var).name(name);
		input = layout == FormLayout.INLINE ? input.placeholder(desc) : input;
		return input;
	}

}
