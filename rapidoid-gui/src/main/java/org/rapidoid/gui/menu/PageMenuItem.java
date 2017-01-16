package org.rapidoid.gui.menu;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.Str;

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
public class PageMenuItem extends AbstractPageMenuItem {

	private static final String CONFIG_RIGHT = "(right)";

	private PageSubMenu submenu;

	private boolean right;

	public PageMenuItem(String caption, Object target, Map<String, Object> extra) {
		super(caption, target, extra);

		if (this.caption.endsWith(CONFIG_RIGHT)) {
			this.caption = Str.trimr(this.caption, CONFIG_RIGHT).trim();
			this.right = true;
		}

		if (!Cls.isSimple(target)) {
			this.submenu = Cls.struct(PageSubMenu.class, PageSubMenuItem.class, target);
		}
	}

	public PageSubMenu getSubmenu() {
		return submenu;
	}

	public void setSubmenu(PageSubMenu submenu) {
		this.submenu = submenu;
	}

	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	@Override
	public String toString() {
		return "AppMenuItem [submenu=" + submenu + ", right=" + right + ", caption=" + caption + ", target=" + target
			+ ", javascript=" + javascript + ", icon=" + icon + ", divider=" + divider + ", extra=" + extra + "]";
	}

	@Override
	void setActiveUri(String uri) {
		super.setActiveUri(uri);

		if (submenu != null) {
			submenu.setActiveUri(uri);
		}
	}
}
