package org.rapidoid.widget;

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

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.data.YAML;
import org.rapidoid.gui.menu.PageMenu;
import org.rapidoid.gui.menu.PageMenuItem;
import org.rapidoid.gui.menu.PageSubMenuItem;
import org.rapidoid.io.Res;
import org.rapidoid.test.TestCommons;

import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class PageMenuTest extends TestCommons {

	@Test
	public void shouldConstructAppMenu() {
		Res res = Res.from("menu.yaml");

		Map<String, ?> data = YAML.parse(res.getContent(), Map.class);
		System.out.println(data);

		PageMenu menu = PageMenu.from(data);
		System.out.println(menu);

		eq(menu.items().size(), 2);

		PageMenuItem item1 = menu.items().get(0);
		eq(item1.getCaption(), "item1");
		eq(item1.getTarget(), "aaa");

		PageMenuItem item2 = menu.items().get(1);
		eq(item2.getCaption(), "item2");
		isNull(item2.getTarget());

		PageSubMenuItem subitem1 = item2.getSubmenu().getItems().get(0);
		PageSubMenuItem subitem2 = item2.getSubmenu().getItems().get(1);
		PageSubMenuItem subitem3 = item2.getSubmenu().getItems().get(2);

		eq(subitem1.getCaption(), "d");
		eq(subitem1.getTarget(), "");

		eq(subitem2.getCaption(), "e");
		eq(subitem2.getTarget(), "f");

		eq(subitem3.getCaption(), "last");
		eq(subitem3.getTarget(), "");
	}

}
