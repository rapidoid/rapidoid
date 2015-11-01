package org.rapidoid.widget;

/*
 * #%L
 * rapidoid-widget
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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
import java.util.Collection;
import java.util.Collections;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Optional;
import org.rapidoid.annotation.Programmatic;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.beany.Metadata;
import org.rapidoid.cls.Cls;
import org.rapidoid.cls.TypeKind;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.html.FieldType;
import org.rapidoid.html.FormLayout;
import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.InputTag;
import org.rapidoid.html.tag.TextareaTag;
import org.rapidoid.log.Log;
import org.rapidoid.model.Item;
import org.rapidoid.model.Models;
import org.rapidoid.model.Property;
import org.rapidoid.plugins.db.DB;
import org.rapidoid.security.DataPermissions;
import org.rapidoid.u.U;
import org.rapidoid.var.Var;
import org.rapidoid.var.Vars;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class FormFieldWidget extends AbstractWidget {

	protected FormMode mode;
	protected Property prop;
	protected FormLayout layout = FormLayout.VERTICAL;
	protected String name;
	protected String desc;
	protected FieldType type;
	protected Collection<?> options;
	protected boolean required;
	protected Var<?> var;
	protected DataPermissions permissions;

	// fully customize: label OR input OR content (label+input)
	protected Tag content;
	protected Tag label;
	protected Tag input;

	public FormFieldWidget(FormMode mode, FormLayout layout, Property prop, String name, String desc, FieldType type,
			Collection<?> options, boolean required, Var<?> var, DataPermissions permissions) {

		this.mode = U.or(mode, FormMode.EDIT);
		this.layout = layout;
		this.prop = prop;
		this.name = name;
		this.desc = U.or(desc, name);
		this.type = type;
		this.options = options;
		this.required = required;
		this.var = var;
		this.permissions = permissions;
	}

	public FormFieldWidget(FormMode mode, FormLayout layout, Item item, Property prop) {
		this.mode = U.or(mode, FormMode.EDIT);
		this.layout = layout;
		this.prop = prop;
		this.name = prop.name();
		this.desc = U.or(prop.caption(), name);
		this.type = mode != FormMode.SHOW ? getPropertyFieldType(prop) : FieldType.LABEL;
		this.options = getPropertyOptions(prop);
		this.required = Metadata.get(prop.annotations(), Optional.class) == null;
		this.var = initVar(item, prop);
	}

	private Var<?> initVar(Item item, Property prop) {
		Object target = U.or(item.value(), item);
		String varName = propVarName(target, prop.name());

		Ctx ctx = Ctxs.get();
		Object initValue = ctx != null ? ctx.data().get(varName) : null;

		try {
			return Models.propertyVar(varName, item, prop.name(), initValue);

		} catch (Exception e) {
			// FIXME x.errors().put(varName, "Invalid value!");
			Log.warn("Invalid value for property: " + prop.name(), e);
			return Models.propertyVar(varName, item, prop.name(), null);
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

			inp = div(label(inp, desc)).class_("checkbox");

			inputWrap = layout == FormLayout.HORIZONTAL ? div(inp).class_("col-sm-offset-4 col-sm-8") : inp;

		} else {
			if (label == null) {
				// if it doesn't have custom label
				if (layout != FormLayout.INLINE) {
					lbl = label(desc);
				} else {
					if (type == FieldType.RADIOS) {
						lbl = label(desc);
					} else {
						lbl = null;
					}
				}
			}

			if (layout == FormLayout.HORIZONTAL) {
				lbl = lbl.class_("col-sm-4 control-label");
			}

			inputWrap = layout == FormLayout.HORIZONTAL ? div(inp).class_("col-sm-8") : inp;
		}

		Tag err = span("").class_("field-error");
		Tag group = lbl != null ? div(lbl, inputWrap, err) : div(inputWrap, err);
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

	protected Tag readonly(Object item) {
		Object display = display(item);

		if (display instanceof Tag) {
			return (Tag) display;
		} else {
			return span(display).class_("display-wrap");
		}
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
		TextareaTag textarea = txtbig(var).name(name);
		textarea = layout == FormLayout.INLINE ? textarea.placeholder(desc) : textarea;
		return textarea;
	}

	protected Object emailInput(String name, String desc, Var<?> var) {
		InputTag input;
		input = email(var).name(name);
		input = layout == FormLayout.INLINE ? input.placeholder(desc) : input;
		return input;
	}

	protected Object passwordInput(String name, String desc, Var<?> var) {
		InputTag input;
		input = password(var).name(name);
		input = layout == FormLayout.INLINE ? input.placeholder(desc) : input;
		return input;
	}

	protected Object textInput(String name, String desc, Var<?> var) {
		InputTag input;
		input = txt(var).name(name);
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

		if (mode != FormMode.SHOW) {
			if (required) {
				var = Vars.mandatory(var);
			}
		}

		return field(name, desc, type, options, var);
	}

	protected boolean isFieldProgrammatic() {
		return prop != null && Metadata.get(prop.annotations(), Programmatic.class) != null;
	}

	protected boolean isFieldAllowed() {
		switch (fieldMode()) {
		case CREATE:
			return permissions.insert;

		case EDIT:
			return permissions.read && permissions.change;

		case SHOW:
			return permissions.read;

		default:
			throw U.notExpected();
		}
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
			return U.list(DB.getAll(clazz));
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	public FormMode getMode() {
		return mode;
	}

	public void setMode(FormMode mode) {
		this.mode = mode;
	}

	public Property getProp() {
		return prop;
	}

	public void setProp(Property prop) {
		this.prop = prop;
	}

	public FormLayout getLayout() {
		return layout;
	}

	public void setLayout(FormLayout layout) {
		this.layout = layout;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public FieldType getType() {
		return type;
	}

	public void setType(FieldType type) {
		this.type = type;
	}

	public Collection<?> getOptions() {
		return options;
	}

	public void setOptions(Collection<?> options) {
		this.options = options;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public Var<?> getVar() {
		return var;
	}

	public void setVar(Var<?> var) {
		this.var = var;
	}

	public DataPermissions getPermissions() {
		return permissions;
	}

	public void setPermissions(DataPermissions permissions) {
		this.permissions = permissions;
	}

	public Tag getContent() {
		return content;
	}

	public void setContent(Tag content) {
		this.content = content;
	}

	public Tag getLabel() {
		return label;
	}

	public void setLabel(Tag label) {
		this.label = label;
	}

	public Tag getInput() {
		return input;
	}

	public void setInput(Tag input) {
		this.input = input;
	}

	public static String propVarName(Object target, String name) {
		return name;

		// TODO consider complex names (see Wire.propVarName)
		// (e.g. Person.name in future
		// if (Cls.isBean(target)) {
		// return target.getClass().getSimpleName() + "." + name;
		// } else {
		// return name;
		// }
	}

}
