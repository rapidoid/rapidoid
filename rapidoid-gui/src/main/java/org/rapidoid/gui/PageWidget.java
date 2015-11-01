package org.rapidoid.gui;

/*
 * #%L
 * rapidoid-widget
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

import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.gui.menu.PageMenu;
import org.rapidoid.html.Tag;
import org.rapidoid.plugins.Plugins;
import org.rapidoid.plugins.templates.ITemplate;
import org.rapidoid.plugins.templates.MustacheTemplatesPlugin;
import org.rapidoid.plugins.templates.Templates;
import org.rapidoid.u.U;
import org.rapidoid.util.UTILS;

@Authors("Nikolche Mihajlovski")
@Since("4.5.0")
public class PageWidget extends AbstractWidget {

	private static volatile ITemplate PAGE_TEMPLATE;

	private static volatile ITemplate PAGE_CONTENT_TEMPLATE;

	private static ITemplate fullTemplate() {
		initTemplates();
		return PAGE_TEMPLATE;
	}

	private static ITemplate ajaxTemplate() {
		initTemplates();
		return PAGE_CONTENT_TEMPLATE;
	}

	private static void initTemplates() {
		if (PAGE_TEMPLATE == null || PAGE_CONTENT_TEMPLATE == null) {
			Plugins.register(new MustacheTemplatesPlugin());
			PAGE_TEMPLATE = Templates.fromFile("page.html");
			PAGE_CONTENT_TEMPLATE = Templates.fromFile("page-ajax.html");
		}
	}

	private String title;

	private Object content;

	private PageMenu menu;

	public PageWidget(Object content) {
		this.content = content;
	}

	@Override
	protected Tag render() {
		Ctx ctx = Ctxs.ctx();

		String html;
		if (ctx.verb().equals("GET")) {
			html = fullTemplate().render(pageModel());
		} else {
			html = ajaxTemplate().render(pageModel());
		}

		return hardcoded(html);
	}

	private Map<String, Object> pageModel() {
		Ctx ctx = Ctxs.ctx();

		Map<String, Object> model = U.map(ctx.data());

		model.put("dev", Conf.dev());

		model.put("verb", ctx.verb());
		model.put("host", ctx.host());
		model.put("uri", ctx.uri());

		boolean loggedIn = ctx.isLoggedIn();
		model.put("loggedIn", loggedIn);
		model.put("user", loggedIn ? ctx.user() : null);

		model.put("version", UTILS.version());

		model.put("content", multi((Object[]) content));
		model.put("result", multi((Object[]) content)); // FIXME rename result to content

		model.put("title", title);
		model.put("menu", menu);

		// FIXME model.put("embedded", ctx.data().get("$_embedded") != null);

		model.put("navbar", true);

		return model;
	}

	public String title() {
		return title;
	}

	public PageWidget title(String title) {
		this.title = title;
		return this;
	}

	public Object content() {
		return content;
	}

	public PageWidget content(Object content) {
		this.content = content;
		return this;
	}

	public PageMenu menu() {
		return menu;
	}

	public PageWidget menu(PageMenu menu) {
		this.menu = menu;
		return this;
	}

}
