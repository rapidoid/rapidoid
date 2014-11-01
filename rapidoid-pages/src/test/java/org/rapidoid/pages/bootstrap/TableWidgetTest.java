package org.rapidoid.pages.bootstrap;

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

import org.rapidoid.model.Items;
import org.rapidoid.model.Model;
import org.rapidoid.pages.PagesTestCommons;
import org.rapidoid.pages.bootstrap.TableWidget;
import org.rapidoid.pages.entity.Person;
import org.rapidoid.util.U;
import org.testng.annotations.Test;

public class TableWidgetTest extends PagesTestCommons {

	@Test
	public void testTableWidget() {
		Items items = Model.beanItems(new Person("John", 20), new Person("Rambo", 50));

		TableWidget table = new TableWidget(items);
		U.print(table);

		has(table, "<th>name</th>");
		has(table, "<th>age</th>");

		has(table, "<td>John</td>");
		has(table, "<td>20</td>");

		has(table, "<td>Rambo</td>");
		has(table, "<td>50</td>");
	}

}
