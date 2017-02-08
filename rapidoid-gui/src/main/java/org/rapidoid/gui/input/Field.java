package org.rapidoid.gui.input;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Programmatic;
import org.rapidoid.annotation.Required;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.beany.Metadata;
import org.rapidoid.cls.Cls;
import org.rapidoid.cls.TypeKind;
import org.rapidoid.commons.Err;
import org.rapidoid.gui.GUI;
import org.rapidoid.gui.base.AbstractWidget;
import org.rapidoid.gui.reqinfo.IReqInfo;
import org.rapidoid.html.FieldType;
import org.rapidoid.html.FormLayout;
import org.rapidoid.html.Tag;
import org.rapidoid.model.Item;
import org.rapidoid.model.Models;
import org.rapidoid.model.Property;
import org.rapidoid.u.U;
import org.rapidoid.var.Var;
import org.rapidoid.var.Vars;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;

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
public class Field extends AbstractWidget<Field> {

	protected volatile FormMode mode = FormMode.EDIT;
	protected volatile Item item;
	protected volatile Property prop;
	protected volatile FormLayout layout = FormLayout.VERTICAL;
	protected volatile String name;
	protected volatile String desc;
	protected volatile FieldType type;
	protected volatile Collection<?> options;
	protected volatile Boolean required;
	protected volatile Tag content;
	protected volatile Tag label;
	protected volatile Tag input;

	public Field(Item item, Property prop) {
		this.item = item;
		this.prop = prop;
	}

	protected boolean isFieldRequired(Property prop) {
		return prop.type().isPrimitive() || Metadata.has(prop.annotations(), Required.class);
	}

	private static Var<Object> initVar(Item item, Property prop, FormMode mode, boolean required) {

		Object target = U.or(item.value(), item);
		String varName = propVarName(target, prop.name());

		boolean isReadOnly = mode == FormMode.SHOW;
		Var<Object> var = Models.propertyVar(varName, item, prop.name(), null, isReadOnly);

		if (mode != FormMode.SHOW) {
			if (required) {
				var = Vars.mandatory(var);
			}
		}

		IReqInfo req = req();

		Object value = req.data().get(varName);
		if (value != null || !req.isGetReq()) {
			var.set(value);
		}

		return var;
	}

	protected Tag field() {

		String name = this.prop.name();
		String desc = U.or(this.desc, prop.caption(), name);

		FieldType type = this.type;
		if (type == null) {
			type = mode != FormMode.SHOW ? getPropertyFieldType(prop) : FieldType.LABEL;
		}

		Collection<?> options = this.options;
		if (options == null) {
			options = getPropertyOptions(prop);
		}

		Boolean required = this.required();
		if (required == null) {
			required = isFieldRequired(prop);
		}

		Var<Object> var = initVar(item, prop, mode, required);

		desc = U.or(desc, name) + ": ";

		Object inp = input == null ? input_(name, desc, type, options, var) : input;

		Tag lbl = label;
		Object inputWrap;

		if (type == FieldType.RADIOS || type == FieldType.CHECKBOXES) {
			inp = layout == FormLayout.VERTICAL ? div(inp) : span(inp);
		}

		if (type == FieldType.CHECKBOX) {
			if (label == null) {
				lbl = null;
			}

			inp = div(GUI.label(inp, desc)).class_("checkbox");

			inputWrap = layout == FormLayout.HORIZONTAL ? div(inp).class_("col-sm-offset-4 col-sm-8") : inp;

		} else {
			if (label == null) {
				// if it doesn't have custom label
				if (layout != FormLayout.INLINE) {
					lbl = GUI.label(desc);
				} else {
					if (type == FieldType.RADIOS) {
						lbl = GUI.label(desc);
					} else {
						lbl = null;
					}
				}
			}

			if (layout == FormLayout.HORIZONTAL && lbl != null) {
				lbl = lbl.class_("col-sm-4 control-label");
			}

			inputWrap = layout == FormLayout.HORIZONTAL ? div(inp).class_("col-sm-8") : inp;
		}

		boolean hasErrors = U.notEmpty(var.errors());
		Tag err = hasErrors ? span(U.join(", ", var.errors())).class_("field-error") : null;

		Tag group = lbl != null ? div(lbl, inputWrap, err) : div(inputWrap, err);
		group = group.class_(hasErrors ? "form-group with-validation-errors" : "form-group");

		if (hasErrors) {
			group = group.attr("data-has-validation-errors", "yes");
			GUI.markValidationErrors();
		}

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
				throw Err.notExpected();
		}
	}

	protected Tag readonly(Object item) {
		Object display = GUI.display(item);
		return div(display).class_("display-wrap");
	}

	protected Object checkboxesInput(String name, Collection<?> options, Var<?> var) {
		return GUI.checkboxes().name(name).options(options).var(var);
	}

	protected Object radiosInput(String name, Collection<?> options, Var<?> var) {
		return GUI.radios().name(name).options(options).var(var);
	}

	protected Object multiSelectInput(String name, Collection<?> options, Var<?> var) {
		return GUI.multiSelect().options(options).var(var).name(name);
	}

	protected Object dropdownInput(String name, Collection<?> options, Var<?> var) {
		return GUI.dropdown().options(options).var(var).name(name);
	}

	protected Object checkboxInput(String name, Var<?> var) {
		return GUI.checkbox().var(var).name(name);
	}

	protected Object textareaInput(String name, String desc, Var<?> var) {
		TextArea textarea = GUI.txtbig().var(var).name(name);
		textarea = layout == FormLayout.INLINE ? textarea.placeholder(desc) : textarea;
		return textarea;
	}

	protected Object emailInput(String name, String desc, Var<?> var) {
		EmailInput input;
		input = GUI.email().var(var).name(name);
		input = layout == FormLayout.INLINE ? input.placeholder(desc) : input;
		return input;
	}

	protected Object passwordInput(String name, String desc, Var<?> var) {
		PasswordInput input;
		input = GUI.password().var(var).name(name);
		input = layout == FormLayout.INLINE ? input.placeholder(desc) : input;
		return input;
	}

	protected Object textInput(String name, String desc, Var<?> var) {
		TextInput input = GUI.txt().var(var).name(name);
		input = layout == FormLayout.INLINE ? input.placeholder(desc) : input;
		return input;
	}

	@Override
	protected Tag render() {
		if (content != null) {
			return content;
		}

		if (isFieldProgrammatic() && mode != FormMode.SHOW) {
			return null;
		}

		return field();
	}

	protected boolean isFieldProgrammatic() {
		return prop != null && Metadata.get(prop.annotations(), Programmatic.class) != null;
	}

	protected boolean isFieldAllowed() {
		// FIXME
		return true;
	}

	protected FormMode fieldMode() {
		return type != FieldType.LABEL ? mode : FormMode.SHOW;
	}

	protected FieldType getPropertyFieldType(Property prop) {
		Class<?> type = prop.type();

		if (type == Boolean.class || type == boolean.class) {
			return FieldType.CHECKBOX;
		}

		if (type.isEnum()) {
			return type.getEnumConstants().length <= 3 ? FieldType.RADIOS : FieldType.DROPDOWN;
		}

		if (prop.name().toLowerCase().contains("email")) {
			return FieldType.EMAIL;
		}

		if (Collection.class.isAssignableFrom(type)) {
			return FieldType.MULTI_SELECT;
		}

		if (Cls.kindOf(type) == TypeKind.UNKNOWN) {
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

		if (Cls.kindOf(type) == TypeKind.UNKNOWN) {
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
		if (Cls.kindOf(clazz) == TypeKind.UNKNOWN && Beany.hasProperty(clazz, "id")) {
			return Collections.EMPTY_LIST; // FIXME use magic?
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	public static String propVarName(Object target, String name) {
		// TODO in future complex names might be constructed
		return name;
	}

	public FormMode mode() {
		return mode;
	}

	public Field mode(FormMode mode) {
		this.mode = mode;
		return this;
	}

	public Property prop() {
		return prop;
	}

	public Field prop(Property prop) {
		this.prop = prop;
		return this;
	}

	public FormLayout layout() {
		return layout;
	}

	public Field layout(FormLayout layout) {
		this.layout = layout;
		return this;
	}

	public String name() {
		return name;
	}

	public Field name(String name) {
		this.name = name;
		return this;
	}

	public String desc() {
		return desc;
	}

	public Field desc(String desc) {
		this.desc = desc;
		return this;
	}

	public FieldType type() {
		return type;
	}

	public Field type(FieldType type) {
		this.type = type;
		return this;
	}

	public Collection<?> options() {
		return options;
	}

	public Field options(Collection<?> options) {
		this.options = options;
		return this;
	}

	public Boolean required() {
		return required;
	}

	public Field required(Boolean required) {
		this.required = required;
		return this;
	}

	public Tag content() {
		return content;
	}

	public Field content(Tag content) {
		this.content = content;
		return this;
	}

	public Tag label() {
		return label;
	}

	public Field label(Tag label) {
		this.label = label;
		return this;
	}

	public Tag input() {
		return input;
	}

	public Field input(Tag input) {
		this.input = input;
		return this;
	}
}
