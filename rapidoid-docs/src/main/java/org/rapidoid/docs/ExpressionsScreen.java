package org.rapidoid.docs;

/*
 * #%L
 * rapidoid-docs
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

import java.util.List;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.app.GUI;
import org.rapidoid.extra.demo.DemoMovie;
import org.rapidoid.util.U;
import org.rapidoid.widget.FA;

// expr("%LINE%", i18n(String multiLanguageText, Object... formatArgs));
// expr("%LINE%", property(Item item, String property));
// expr("%LINE%", render(String templateFileName, Object... namesAndValues));
// expr("%LINE%", hardcoded(String content));
// expr("%LINE%", row(ColspanTag... cols));
// expr("%LINE%", icon(String icon));
// expr("%LINE%", glyphicon(String glyphicon));
// expr("%LINE%", a_glyph(String glyphicon, "abc"));
// expr("%LINE%", a_awesome(String fontAwesomeIcon, "abc"));
// expr("%LINE%", nav_(boolean fluid, boolean inverse, Tag brand, Object[] navbarContent));
// expr("%LINE%", containerMaybeFluid(boolean fluid));
// expr("%LINE%", navbarMenu(boolean onLeft, int activeIndex, Object... menuItems));
// expr("%LINE%", navbarDropdown(boolean onLeft, Tag menu, Object... subItems));
// expr("%LINE%", navbarForm(boolean onLeft, Object btnContent, String[] fields, String[] placeholders));
// expr("%LINE%", navbarSearchForm(String action));
// expr("%LINE%", navbarPage(boolean fluid, Tag brand, Object[] navbarContent, Object pageContent));
// expr("%LINE%", modal(Object title, Object content, Object footer));
// expr("%LINE%", xClose(String cmd));
// expr("%LINE%", caret());
// expr("%LINE%", col_(int cols, "abc"));
// expr("%LINE%", cmd(String cmd, Object... args));
// expr("%LINE%", navigate(String cmd));
// expr("%LINE%", cmds(String... commands));
// expr("%LINE%", grid(Class<T>  type, Object[] items, String sortOrder, int pageSize,
// grid(Class<T>  type, Collection<T>  items, String sortOrder, int pageSize,
// grid(Items items, String sortOrder, int pageSize, String... properties));
// expr("%LINE%", pager(int from, int to, Var<Integer>  pageNumber));
// expr("%LINE%", form_(FormLayout layout, String[] fieldsNames, String[] fieldsDesc,
// show(Object bean, String... properties));
// expr("%LINE%", show(final Item item, String... properties));
// expr("%LINE%", show(DataManager dataManager, final Item item, String... properties));
// expr("%LINE%", edit(Object bean, String... properties));
// expr("%LINE%", edit(final Item item, String... properties));
// expr("%LINE%", edit(DataManager dataManager, final Item item, String... properties));
// expr("%LINE%", create(Object bean, String... properties));
// expr("%LINE%", create(final Item item, String... properties));
// expr("%LINE%", create(DataManager dataManager, final Item item, String... properties));
// expr("%LINE%", field(DataManager dataManager, FormMode mode, FormLayout layout, Property prop,
// field(DataManager dataManager, FormMode mode, FormLayout layout, Item item,
// page(boolean devMode, String pageTitle, Object head, Object body));
// expr("%LINE%", page(boolean devMode, String pageTitle, Object body));
// expr("%LINE%", media(Object left, Object title, Object body, String targetUrl));
// expr("%LINE%", mediaList(U.<Object>  list("abc", "xyz")));
// expr("%LINE%", providedVar(String name, T defaultValue));
// expr("%LINE%", sessionVar(String name, T defaultValue));
// expr("%LINE%", localVar(String name, T defaultValue));
// expr("%LINE%", localVar(String name, int defaultValue, int min, int max));
// expr("%LINE%", isEntity(Object obj));
// expr("%LINE%", urlFor(Object entity));

@Authors("Nikolche Mihajlovski")
@Since("2.3.0")
public class ExpressionsScreen extends GUI {

	List<Object> exprs = U.list();

	Object content() {

		expr("%LINE%", btn("abc"));
		expr("%LINE%", btn("abc").primary());
		expr("%LINE%", btn("great").success());

		expr("%LINE%", btn("information").info());
		expr("%LINE%", btn("careful").warning());
		expr("%LINE%", btn("Delete!").danger());

		expr("%LINE%", create(new DemoMovie()));
		expr("%LINE%", show(new DemoMovie("The Matrix", 1999)));
		expr("%LINE%", edit(new DemoMovie("The Imitation Game", 2014)));

		expr("%LINE%", grid(U.orderedMap("Name", "John Doe", "Age", 99, "Address", N_A)));
		expr("%LINE%", highlight("abcde"));
		expr("%LINE%", highlight("abc123-gh7xyz", "\\d+"));

		expr("%LINE%", txt("name", "Joe"));
		expr("%LINE%", password("pass"));
		expr("%LINE%", txtbig("desc", "Some text"));

		expr("%LINE%", email("em"));
		expr("%LINE%", checkbox("ch", true));
		expr("%LINE%", dropdown(U.list("A", "B"), "v0", "B"));

		expr("%LINE%", multiSelect(U.list("A", "B", "C"), "v1", U.list("A", "C")));
		expr("%LINE%", radios(U.list("A", "B"), "v2", "A"));
		expr("%LINE%", checkboxes(U.list("A", "B", "C"), "v3", U.list("A", "C")));

		expr("%LINE%", display(U.list("a", "b", "c")));
		expr("%LINE%", inline("abc", checkbox(providedVar("c", true)), OK));
		expr("%LINE%", panel("abc"));

		expr("%LINE%", jumbotron("abc"));
		expr("%LINE%", well("abc"));
		expr("%LINE%", titleBox("abc"));

		expr("%LINE%", FA.COG);
		expr("%LINE%", FA.FLAG);
		expr("%LINE%", right("abc"));

		expr("%LINE%", row("abc"));
		expr("%LINE%", row(col2("a"), col10("b")));
		expr("%LINE%", row(col4("c"), col4("d"), col4("e")));

		expr("%LINE%", row(col1("g"), col7("g"), col4("e")));
		expr("%LINE%", mid2("ab"));
		expr("%LINE%", mid4("de", right("f")));

		expr("%LINE%", mid6("e", right("f")));
		expr("%LINE%", mid8("g", right("h")));
		expr("%LINE%", NOTHING);

		expr("%LINE%", SAVE);
		expr("%LINE%", CANCEL);
		expr("%LINE%", debug());

		// expr("%LINE%", ADD);
		// expr("%LINE%", UPDATE);
		// expr("%LINE%", INSERT);
		// expr("%LINE%", DELETE);
		// expr("%LINE%", YES_DELETE);
		// expr("%LINE%", REMOVE);
		// expr("%LINE%", DESTROY);
		// expr("%LINE%", YES);
		// expr("%LINE%", NO);
		// expr("%LINE%", OK);
		// expr("%LINE%", BACK);
		// expr("%LINE%", EDIT);

		return generate();
	}

	private void expr(String code, Object expr) {
		code = U.mid(code, 9, -2);
		exprs.add(arr(snippet(code), div(expr)));
	}

	private Object generate() {
		return layout(exprs).cols(3);
	}

}
