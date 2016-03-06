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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Env;
import org.rapidoid.commons.RapidoidInfo;
import org.rapidoid.gui.base.AbstractWidget;
import org.rapidoid.gui.menu.PageMenu;
import org.rapidoid.gui.reqinfo.IReqInfo;
import org.rapidoid.gui.reqinfo.ReqInfo;
import org.rapidoid.html.Tag;
import org.rapidoid.templates.ITemplate;
import org.rapidoid.templates.Templates;
import org.rapidoid.u.U;

import java.util.Map;
import java.util.Set;

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
			PAGE_TEMPLATE = Templates.fromFile("page.html");
			PAGE_CONTENT_TEMPLATE = Templates.fromFile("page-ajax.html");
		}
	}

	private Object brand;

	private String title;

	private Object content;

	private PageMenu menu;

	private boolean embedded;

	private boolean search;

	private boolean cdn = !Env.dev();

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

		model.put("dev", Env.dev());

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
		model.put("brand", brand);
		model.put("title", title);
		model.put("menu", menu);
		model.put("search", search);

		model.put("embedded", embedded || req.attrs().get("_embedded") != null);

		model.put("navbar", true);

		return model;
	}

	public Object brand() {
		return brand;
	}

	public HtmlPage brand(Object brand) {
		this.brand = brand;
		return this;
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

	public boolean search() {
		return search;
	}

	public void search(boolean search) {
		this.search = search;
	}

	public boolean cdn() {
		return cdn;
	}

	public void cdn(boolean cdn) {
		this.cdn = cdn;
	}
}
