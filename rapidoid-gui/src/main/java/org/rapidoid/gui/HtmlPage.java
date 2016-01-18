package org.rapidoid.gui;

/*
 * #%L
 * rapidoid-gui
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

import java.util.Map;
import java.util.Set;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.RapidoidInfo;
import org.rapidoid.config.Conf;
import org.rapidoid.gui.base.AbstractWidget;
import org.rapidoid.gui.menu.PageMenu;
import org.rapidoid.gui.reqinfo.IReqInfo;
import org.rapidoid.gui.reqinfo.ReqInfo;
import org.rapidoid.html.Tag;
import org.rapidoid.plugins.Plugins;
import org.rapidoid.plugins.templates.ITemplate;
import org.rapidoid.plugins.templates.MustacheTemplatesPlugin;
import org.rapidoid.plugins.templates.Templates;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.0.0")
public class HtmlPage extends AbstractWidget {

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

	private String title = "";

	private Object content;

	private PageMenu menu;

	private boolean embedded;

	public HtmlPage(Object content) {
		this.content = content;
	}

	@Override
	protected Tag render() {
		String html;

		if (ReqInfo.get().isGetReq()) {
			html = fullTemplate().render(pageModel());
		} else {
			html = ajaxTemplate().render(pageModel());
		}

		return hardcoded(html);
	}

	private Map<String, Object> pageModel() {
		IReqInfo req = ReqInfo.get();

		Map<String, Object> model = U.map(req.data());

		model.put("dev", Conf.dev());

		model.put("verb", req.verb());
		model.put("host", req.host());
		model.put("uri", req.uri());
		model.put("path", req.path());

		model.put("username", req.username());

		Set<String> roles = req.roles();
		model.put("roles", roles);

		for (String role : U.safe(roles)) {
			model.put("role_" + role, true);
		}

		model.put("version", RapidoidInfo.version());

		model.put("content", multi((Object[]) content));
		model.put("result", multi((Object[]) content)); // FIXME rename result to content

		model.put("home", "/");
		model.put("title", title);
		model.put("menu", menu);

		model.put("embedded", embedded || req.attrs().get("_embedded") != null);

		model.put("navbar", true);

		return model;
	}

	public String title() {
		return title;
	}

	public HtmlPage title(String title) {
		this.title = title;
		return this;
	}

	public Object content() {
		return content;
	}

	public HtmlPage content(Object content) {
		this.content = content;
		return this;
	}

	public PageMenu menu() {
		return menu;
	}

	public HtmlPage menu(PageMenu menu) {
		this.menu = menu;
		return this;
	}

	public boolean embedded() {
		return embedded;
	}

	public HtmlPage embedded(boolean embedded) {
		this.embedded = embedded;
		return this;
	}

}
