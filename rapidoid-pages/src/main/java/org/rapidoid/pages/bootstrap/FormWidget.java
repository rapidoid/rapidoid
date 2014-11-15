package org.rapidoid.pages.bootstrap;

import java.util.Collection;
import java.util.List;

import org.rapidoid.html.FieldType;
import org.rapidoid.html.FormLayout;
import org.rapidoid.html.Tag;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.model.Item;
import org.rapidoid.model.Property;
import org.rapidoid.reactive.Var;
import org.rapidoid.util.Cls;
import org.rapidoid.util.TypeKind;
import org.rapidoid.util.U;

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

public class FormWidget extends BootstrapWidget {

	private final Item item;

	private final Tag<?>[] buttons;

	public FormWidget(final Item item, final Tag<?>[] buttons) {
		this.item = item;
		this.buttons = buttons;
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

	protected Object[] getPropertyOptions(Property prop) {
		Class<?> type = prop.type();

		if (type.isEnum()) {
			return type.getEnumConstants();
		}

		if (Cls.kindOf(type) == TypeKind.OBJECT) {
			return new Object[] {};
		}

		return null;
	}

	@Override
	public Tag<?> view(HttpExchange x) {
		final List<Property> properties = item.editableProperties();

		int propN = properties.size();

		String[] names = new String[propN];
		String[] desc = new String[propN];
		FieldType[] types = new FieldType[propN];
		Object[][] options = new Object[propN][];
		Var<?>[] vars = new Var[propN];

		for (int i = 0; i < propN; i++) {
			Property prop = properties.get(i);
			names[i] = prop.name();
			desc[i] = prop.caption();
			types[i] = getPropertyFieldType(prop);
			options[i] = getPropertyOptions(prop);
			vars[i] = property(item, prop.name());
		}

		return form_(FormLayout.VERTICAL, names, desc, types, options, vars, buttons);
	}

	@Override
	public String toString() {
		throw U.rte("FFFFFF");
	}
}
