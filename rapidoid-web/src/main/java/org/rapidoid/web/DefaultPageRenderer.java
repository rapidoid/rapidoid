package org.rapidoid.web;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.config.Config;
import org.rapidoid.gui.GUI;
import org.rapidoid.gui.HtmlPage;
import org.rapidoid.gui.menu.PageMenu;
import org.rapidoid.http.Req;
import org.rapidoid.http.Resp;
import org.rapidoid.http.Screen;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.customize.PageRenderer;
import org.rapidoid.u.U;
import org.rapidoid.value.Value;

import java.util.regex.Pattern;

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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class DefaultPageRenderer extends RapidoidThing implements PageRenderer {

	private static final Pattern FULL_PAGE_PATTERN = Pattern.compile("(?s)^(?:\\s*(<!--(?:.*?)-->)*?)*?<(!DOCTYPE\\s+html|html)>");

	private final Customization customization;
	private final Config cfg;
	private final Config segments;

	private final Value<String> home;
	private final Value<String> title;
	private final Value<String> brand;
	private final Value<Boolean> search;
	private final Value<String> cdn;
	private final Value<Boolean> navbar;
	private final Value<Boolean> fluid;
	private final Config menu;

	public DefaultPageRenderer(Customization customization) {
		this.customization = customization;
		this.cfg = customization.appConfig();
		this.segments = cfg.sub("segments");

		this.home = cfg.entry("home").str();
		this.title = cfg.entry("title").str();
		this.brand = cfg.entry("brand").str();
		this.search = cfg.entry("search").bool();
		this.cdn = cfg.entry("cdn").str();
		this.navbar = cfg.entry("navbar").bool();
		this.fluid = cfg.entry("fluid").bool();
		this.menu = cfg.sub("menu");
	}

	@Override
	public Object renderPage(Req req, Resp resp, String content) throws Exception {
		U.notNull(content, "page content");

		if (isFullPage(req, content)) return content;

		Screen screen = resp.screen();
		HtmlPage page = GUI.page(GUI.hardcoded(content));

		Config segment = segments.sub(req.segment());

		if (screen.title() != null) {
			page.title(screen.title());
		} else {
			page.title(segment.entry("title").str().orElse(title).getOrNull());
		}

		Object brand;
		if (screen.brand() != null) {
			brand = screen.brand();
		} else {
			brand = segment.entry("brand").str().orElse(this.brand).getOrNull();
		}
		page.brand(U.or(brand, ""));

		if (screen.search() != null) {
			page.search(screen.search());
		} else {
			page.search(segment.entry("search").bool().orElse(search).or(false));
		}

		if (screen.navbar() != null) {
			page.navbar(screen.navbar());
		} else {
			page.navbar(segment.entry("navbar").bool().orElse(navbar).or(brand != null));
		}

		if (screen.fluid() != null) {
			page.fluid(screen.fluid());
		} else {
			page.fluid(segment.entry("fluid").bool().orElse(fluid).or(false));
		}

		if (screen.cdn() != null) {
			page.cdn(screen.cdn());
		} else {
			String cdnS = segment.entry("cdn").str().orElse(cdn).or("auto");
			if (!"auto".equalsIgnoreCase(cdnS)) {
				page.cdn(Cls.bool(cdnS));
			}
		}

		page.home(segment.entry("home").str().orElse(home).or("/"));

		Config appMenu = segment.has("menu") ? segment.sub("menu") : menu;

		page.menu(PageMenu.from(appMenu.toMap()).uri(req.path()));

		return page;
	}

	private boolean isFullPage(Req req, String content) {
		return (req.attr("_embedded", false) && content.startsWith("<!--EMBEDDED-->")) || FULL_PAGE_PATTERN.matcher(content).find();
	}

}
