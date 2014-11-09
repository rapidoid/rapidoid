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
import org.rapidoid.html.tag.ATag;
import org.rapidoid.html.tag.FormTag;
import org.rapidoid.pages.bootstrap.NavbarBootstrapPage;

public class FormPage extends NavbarBootstrapPage {

	private ATag brand;

	private FormTag form1;
	private FormTag form2;
	private FormTag form3;

	public FormPage() {
		brand = a("Welcome to the Forms page!").href("/");

		form1 = frm(FormLayout.HORIZONTAL);
		form2 = frm(FormLayout.VERTICAL);
		form3 = frm(FormLayout.INLINE);

		setContent(page());
	}

	private FormTag frm(FormLayout layout) {

		String[] names = { "user", "pass", "email", "aa", "roles", "gender", "accept", "bbb" };

		String[] desc = { "Username", "Password", "E-mail address", "Aaa", "Roles", "Gender", "Bbb",
				"I accept the terms and conditions" };

		FieldType[] types = { FieldType.TEXT, FieldType.PASSWORD, FieldType.EMAIL, FieldType.DROPDOWN,
				FieldType.MULTI_SELECT, FieldType.RADIOS, FieldType.CHECKBOXES, FieldType.CHECKBOX };

		Object[] opt1 = { "Yes", "No", "Not sure" };

		Object[] opt2 = { "Admin", "Manager", "Moderator" };

		Object[] opt3 = { "Male", "Female" };

		Object[] opt4 = { "A", "B", "C", "D" };

		Object[][] options = { null, null, null, opt1, opt2, opt3, opt4, null };

		return form_(layout, names, desc, types, options, "Save", "Cancel", "Something else");
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
