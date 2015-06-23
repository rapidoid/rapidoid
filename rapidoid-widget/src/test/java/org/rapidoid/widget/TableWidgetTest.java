package org.rapidoid.widget;

/*
 * #%L
 * rapidoid-widget
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
import org.rapidoid.annotation.Since;
import org.rapidoid.model.Items;
import org.rapidoid.model.Models;
import org.testng.annotations.Test;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class TableWidgetTest extends WidgetTestCommons {

	@Test
	public void testTableWidget() {

		setupMockExchange();

		Person john = new Person("John", 20);
		john.id = 1;

		Person rambo = new Person("Rambo", 50);
		rambo.id = 2;

		Items items = Models.beanItemsInfer(john, rambo);

		GridWidget table = BootstrapWidgets.grid(items, null, 10);
		print(table);

		hasRegex(table, "<th[^>]*?>Name</th>");
		hasRegex(table, "<th[^>]*?>Age</th>");

		hasRegex(table, "<td[^>]*?>John</td>");
		hasRegex(table, "<td[^>]*?>20</td>");

		hasRegex(table, "<td[^>]*?>Rambo</td>");
		hasRegex(table, "<td[^>]*?>50</td>");
	}

}
