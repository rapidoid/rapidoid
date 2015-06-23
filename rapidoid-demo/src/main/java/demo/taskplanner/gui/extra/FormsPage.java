package demo.taskplanner.gui.extra;

/*
 * #%L
 * rapidoid-demo
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Session;
import org.rapidoid.annotation.Since;
import org.rapidoid.html.FieldType;
import org.rapidoid.html.FormLayout;
import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.ATag;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.var.Var;
import org.rapidoid.var.Vars;
import org.rapidoid.widget.BootstrapWidgets;
import org.rapidoid.widget.ButtonWidget;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class FormsPage extends BootstrapWidgets {

	@Session
	private Var<?>[] vars1 = Vars.vars("misc", "niko1", "rapidoid", "niko@rapi.doid", "No",
			arr("Manager", "Moderator"), "Male", arr("A", "C"), "Very interesting!", true);

	@Session
	private Var<?>[] vars2 = Vars.vars("misc", "niko2", "rapidoid", "niko@rapi.doid", "Yes",
			arr("Manager", "Moderator"), "Male", arr("A", "C"), "Very interesting!", true);

	@Session
	private Var<?>[] vars3 = Vars.vars("misc", "niko3", "rapidoid", "niko@rapi.doid", "No",
			arr("Manager", "Moderator"), "Male", arr("A", "C"), "Very interesting!", true);

	public Object content(HttpExchange x) {

		ATag brand = a("Welcome to the Forms!").href("/mix");

		Tag form1 = frm(FormLayout.HORIZONTAL, vars1);
		Tag form2 = frm(FormLayout.VERTICAL, vars2);
		Tag form3 = frm(FormLayout.INLINE, vars3);

		Tag pageContent = row(col3(form1), col3(form2), col6(form3));

		return navbarPage(true, brand, null, pageContent);
	}

	private Tag frm(final FormLayout layout, Var<?>[] vars) {

		final String[] names = { "user", "pass", "email", "driver", "roles", "gender", "accept", "bbb", "comments" };

		final String[] desc = { "Username", "Password", "E-mail address", "Has Driver license", "Roles", "Gender",
				"Bbb", "Comments", "I accept the terms and conditions" };

		final FieldType[] types = { FieldType.TEXT, FieldType.PASSWORD, FieldType.EMAIL, FieldType.DROPDOWN,
				FieldType.MULTI_SELECT, FieldType.RADIOS, FieldType.CHECKBOXES, FieldType.TEXTAREA, FieldType.CHECKBOX };

		Object[] opt1 = { "Yes", "No", "Not sure" };

		Object[] opt2 = { "Admin", "Manager", "Moderator" };

		Object[] opt3 = { "Male", "Female" };

		Object[] opt4 = { "A", "B", "C", "D" };

		final Object[][] options = { null, null, null, opt1, opt2, opt3, opt4, null, null };

		final ButtonWidget[] buttons = { btn("Save").command("saveIt", vars[0]), cmd("Cancel", vars[0]) };

		return div(form_(layout, names, desc, types, options, vars, buttons), ul_li((Object[]) vars));
	}

	public void onSaveIt(Object x) {
		System.out.println("saved " + x);
	}

	public void onCancel(Object x) {
		System.out.println("cancel " + x);
	}

}
