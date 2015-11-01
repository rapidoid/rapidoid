package org.rapidoidx.demo.taskplanner.gui.extra;

/*
 * #%L
 * rapidoid-x-demo
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Session;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.Btn;
import org.rapidoid.gui.base.BootstrapWidgets;
import org.rapidoid.html.FieldType;
import org.rapidoid.html.FormLayout;
import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.ATag;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.var.Var;
import org.rapidoid.var.Vars;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
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

		final Btn[] buttons = { btn("Save").command("saveIt", vars[0]), cmd("Cancel", vars[0]) };

		return div(form_(layout, names, desc, types, options, vars, buttons), ul_li((Object[]) vars));
	}

	public void onSaveIt(Object x) {
		System.out.println("saved " + x);
	}

	public void onCancel(Object x) {
		System.out.println("cancel " + x);
	}

}
