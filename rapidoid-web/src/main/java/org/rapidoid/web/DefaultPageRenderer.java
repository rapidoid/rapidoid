package org.rapidoid.web;

/*
 * #%L
 * rapidoid-web
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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
import org.rapidoid.gui.GUI;
import org.rapidoid.gui.HtmlPage;
import org.rapidoid.gui.menu.PageMenu;
import org.rapidoid.http.Req;
import org.rapidoid.http.Resp;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.customize.PageRenderer;
import org.rapidoid.u.U;

import java.util.Map;
import java.util.regex.Pattern;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class DefaultPageRenderer implements PageRenderer {

	private static final Pattern FULL_PAGE_PATTERN = Pattern.compile("(?s)^\\s*<(!DOCTYPE\\s+html|html)>");

	private final Customization customization;

	public DefaultPageRenderer(Customization customization) {
		this.customization = customization;
	}

	@Override
	public Object renderPage(Req req, Resp resp, String content) throws Exception {
		U.notNull(content, "page content");

		if (isFullPage(content)) return content;

		HtmlPage page = GUI.page(GUI.multi(GUI.hardcoded(content)));

		String title = (String) resp.model().get("title");
		String defaultTitle = "App"; // FIXME
		page = page.title(U.or(title, defaultTitle));

		Map<String, Object> menu = customization.config().sub("menu").toMap();
		page = page.menu(PageMenu.from(menu));

		return page;
	}

	private boolean isFullPage(String content) {
		return FULL_PAGE_PATTERN.matcher(content).find();
	}

}
