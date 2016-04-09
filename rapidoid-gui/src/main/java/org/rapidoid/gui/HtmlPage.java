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
import org.rapidoid.config.Conf;
import org.rapidoid.gui.base.AbstractWidget;
import org.rapidoid.gui.menu.PageMenu;
import org.rapidoid.gui.reqinfo.IReqInfo;
import org.rapidoid.gui.reqinfo.ReqInfo;
import org.rapidoid.html.Tag;
import org.rapidoid.render.Template;
import org.rapidoid.render.Templates;
import org.rapidoid.u.U;

import java.util.Map;
import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("5.0.0")
public class HtmlPage extends AbstractWidget {

	private static volatile Template PAGE_TEMPLATE;

	private static volatile Template PAGE_CONTENT_TEMPLATE;

	private static Template fullTemplate() {
		initTemplates();
		return PAGE_TEMPLATE;
	}

	private static Template ajaxTemplate() {
		initTemplates();
		return PAGE_CONTENT_TEMPLATE;
	}

	private static void initTemplates() {
		if (PAGE_TEMPLATE == null || PAGE_CONTENT_TEMPLATE == null) {
			PAGE_TEMPLATE = Templates.fromFile("page.html");
			PAGE_CONTENT_TEMPLATE = Templates.fromFile("page-ajax.html");
		}
	}

	private volatile String home;

	private volatile Object brand;

	private volatile String title;

	private volatile Object content;

	private volatile PageMenu menu;

	private volatile boolean embedded;

	private volatile boolean search;

	private volatile boolean navbar = true;

	private volatile boolean fluid;

	private volatile boolean cdn = !Env.dev();

	public HtmlPage(Object content) {
		this.content = content;
	}

	@Override
	protected Tag render() {
		Map<String, Object> model = pageModel();

		if (menu != null) {
			menu.renderContentTemplates(model);
		}

		String html;
		if (ReqInfo.get().isGetReq()) {
			html = fullTemplate().render(model);
		} else {
			html = ajaxTemplate().render(model);
		}

		return hardcoded(html);
	}

	private Map<String, Object> pageModel() {
		IReqInfo req = ReqInfo.get();

		Map<String, Object> model = U.map(req.data());

		model.put("dev", Env.dev());

		int appPort = Conf.APP.entry("port").num().or(8888);
		int adminPort = Conf.ADMIN.entry("port").num().or(0);
		String adminUrl = adminPort <= 0 || adminPort == appPort ? "/_" : "http://" + req.host().split(":")[0] + ":" + adminPort + "/_";
		model.put("adminUrl", adminUrl);

		model.put("host", req.host());
		model.put("verb", req.verb());
		model.put("uri", req.uri());
		model.put("path", req.path());
		model.put("sector", req.sector());

		model.put("username", req.username());

		Set<String> roles = req.roles();
		model.put("roles", roles);

		for (String role : U.safe(roles)) {
			model.put("role_" + role, true);
		}

		model.put("content", multi((Object[]) content));
		model.put("home", home);
		model.put("brand", brand);
		model.put("title", title);
		model.put("menu", menu);

		model.put("version", RapidoidInfo.version());
		model.put("embedded", embedded || req.attrs().get("_embedded") != null);

		model.put("search", search);
		model.put("navbar", navbar);
		model.put("fluid", fluid);
		model.put("cdn", cdn);

		return model;
	}

	public String home() {
		return home;
	}

	public HtmlPage home(String homeURI) {
		this.home = homeURI;
		return this;
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

	public boolean navbar() {
		return navbar;
	}

	public HtmlPage navbar(boolean navbar) {
		this.navbar = navbar;
		return this;
	}

	public boolean fluid() {
		return fluid;
	}

	public HtmlPage fluid(boolean fluid) {
		this.fluid = fluid;
		return this;
	}
}
