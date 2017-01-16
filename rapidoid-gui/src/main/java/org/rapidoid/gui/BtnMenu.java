package org.rapidoid.gui;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.base.AbstractWidget;
import org.rapidoid.html.Tag;
import org.rapidoid.u.U;

import java.util.Iterator;
import java.util.LinkedHashMap;
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

@Authors({"Marjan Ilievski", "Nikolche Mihajlovski"})
@Since("5.0.11")
public class BtnMenu extends AbstractWidget<BtnMenu> {

	private String title;

	private List<Map<Object, String>> menuItems;

	public BtnMenu() {
		menuItems = U.list();
		menuItems.add(new LinkedHashMap<Object, String>());
	}

	public void addMenuItem(String text, String url) {
		menuItems.get(menuItems.size() - 1).put(text, url);
	}

	public void addSeparator() {
		menuItems.add(new LinkedHashMap<Object, String>());
	}

	private Tag generateButtonHtmlContent() {
		Tag span = span().class_("caret");
		return GUI.button().type("button").class_("btn btn-default dropdown-toggle").attr("data-toggle", "dropdown")
			.attr("aria-haspopup", "true").attr("aria-expanded", "false").contents(title + " ", span);
	}

	private Tag generateMenuHtmlContent() {
		if (!menuItems.get(0).isEmpty()) {
			List<Tag> content = U.list();

			for (Iterator<Map<Object, String>> iterator = menuItems.iterator(); iterator.hasNext(); ) {

				generateMenuItems(content, iterator);

				if (iterator.hasNext()) {
					content.add(li().role("separator").class_("divider"));
				}
			}

			return GUI.ul().class_("dropdown-menu").contents(content);
		} else {
			return GUI.ul().class_("dropdown-menu");
		}
	}

	private void generateMenuItems(List<Tag> content, Iterator<Map<Object, String>> subMenuIterator) {
		for (Map.Entry<Object, String> menuItem : subMenuIterator.next().entrySet()) {
			Tag a = a().href(menuItem.getValue()).contents(menuItem.getKey());
			Tag li = li().contents(a);
			content.add(li);
		}
	}

	@Override
	public String toString() {
		return render();
	}

	public String render() {
		List<Tag> content = U.list();
		content.add(generateButtonHtmlContent());
		Tag menuContent = generateMenuHtmlContent();
		content.add(menuContent);
		return div().class_("btn-group").contents(content).toString();
	}

	public String title() {
		return title;
	}

	public BtnMenu title(String title) {
		this.title = title;
		return this;
	}

	public List<Map<Object, String>> items() {
		return menuItems;
	}

	public BtnMenu items(Map<Object, String> items) {
		this.menuItems.add(items);
		return this;
	}

}
