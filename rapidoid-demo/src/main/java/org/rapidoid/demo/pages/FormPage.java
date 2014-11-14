package org.rapidoid.demo.pages;

/*
 * #%L
 * rapidoid-demo
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

import org.rapidoid.html.FieldType;
import org.rapidoid.html.FormLayout;
import org.rapidoid.html.Tag;
import org.rapidoid.html.TagEventHandler;
import org.rapidoid.html.tag.ATag;
import org.rapidoid.pages.bootstrap.NavbarBootstrapPage;
import org.rapidoid.reactive.Var;
import org.rapidoid.reactive.Vars;

@SuppressWarnings("serial")
public class FormPage extends NavbarBootstrapPage {

	private ATag brand;

	private Object form1;
	private Object form2;
	private Object form3;

	public FormPage() {
		brand = a("Welcome to the Forms page!").href("/form.html");

		form1 = frm(FormLayout.HORIZONTAL);
		form2 = frm(FormLayout.VERTICAL);
		form3 = frm(FormLayout.INLINE);

		setContent(page());
	}

	private Object frm(FormLayout layout) {

		String[] names = { "user", "pass", "email", "driver", "roles", "gender", "accept", "bbb", "comments" };

		String[] desc = { "Username", "Password", "E-mail address", "Has Driver license", "Roles", "Gender", "Bbb",
				"Comments", "I accept the terms and conditions" };

		FieldType[] types = { FieldType.TEXT, FieldType.PASSWORD, FieldType.EMAIL, FieldType.DROPDOWN,
				FieldType.MULTI_SELECT, FieldType.RADIOS, FieldType.CHECKBOXES, FieldType.TEXTAREA, FieldType.CHECKBOX };

		Object[] opt1 = { "Yes", "No", "Not sure" };

		Object[] opt2 = { "Admin", "Manager", "Moderator" };

		Object[] opt3 = { "Male", "Female" };

		Object[] opt4 = { "A", "B", "C", "D" };

		Object[][] options = { null, null, null, opt1, opt2, opt3, opt4, null, null };

		Var<?>[] vars = Vars.vars("niko", "rapidoid", "niko@rapi.doid", "No", arr("Manager", "Moderator"), "Male",
				arr("A", "C"), "Very interesting!", true);

		Object[] buttons = { btn("Save", new TagEventHandler<Tag<?>>() {
			@Override
			public void handle(Tag<?> target) {
				target.append("+");
			}
		}), btn("Cancel") };

		return arr(form_(layout, names, desc, types, options, vars, buttons), ul_li((Object[]) vars));
	}

	@Override
	protected Object pageContent() {
		return row(col3(form1), col3(form2), col6(form3));
	}

	@Override
	protected Tag<?> brand() {
		return brand;
	}

	@Override
	protected Object[] navbarContent() {
		return arr();
	}

}
