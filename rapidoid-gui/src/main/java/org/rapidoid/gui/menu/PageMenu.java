package org.rapidoid.gui.menu;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.data.YAML;
import org.rapidoid.io.Res;
import org.rapidoid.render.Templates;
import org.rapidoid.u.U;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class PageMenu extends RapidoidThing {

	private final List<PageMenuItem> items;

	private volatile String uri;

	public PageMenu(List<PageMenuItem> items) {
		this.items = items;
	}

	public List<PageMenuItem> items() {
		return items;
	}

	public String uri() {
		return uri;
	}

	public PageMenu uri(String uri) {
		this.uri = uri;

		for (PageMenuItem item : items) {
			item.setActiveUri(uri);
		}

		return this;
	}

	@Override
	public String toString() {
		return U.join("\n", items);
	}

	@SuppressWarnings("unchecked")
	public static PageMenu parse(String filename) {
		byte[] yaml = Res.from(filename).getBytesOrNull();
		Map<String, ?> data = yaml != null ? YAML.parse(yaml, Map.class) : null;
		return from(data);
	}

	public static PageMenu from(Map<String, ?> data) {
		return Cls.struct(PageMenu.class, PageMenuItem.class, U.or(data, U.map()));
	}

	public List<PageMenuItem> leftItems() {
		List<PageMenuItem> left = U.list();

		for (PageMenuItem item : items) {
			if (!item.isRight()) {
				left.add(item);
			}
		}

		return left;
	}

	public List<PageMenuItem> rightItems() {
		List<PageMenuItem> right = U.list();

		for (PageMenuItem item : items) {
			if (item.isRight()) {
				right.add(item);
			}
		}

		return right;
	}

	public List<PageMenuItem> rightItemsReversed() {
		List<PageMenuItem> list = rightItems();
		Collections.reverse(list);
		return list;
	}

	public void renderContentTemplates(Map<String, Object> model) {
		for (PageMenuItem item : items) {

			if (item.getCaption().contains("{")) {
				item.setCaption(Templates.compile(item.getCaption()).render(model));
			}

			if (item.getSubmenu() != null) {
				for (PageSubMenuItem subItem : item.getSubmenu().getItems()) {
					subItem.setCaption(Templates.compile(subItem.getCaption()).render(model));
				}
			}
		}
	}
}
