package org.rapidoid.gui;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.RapidoidInfo;
import org.rapidoid.commons.Str;
import org.rapidoid.config.Conf;
import org.rapidoid.gui.menu.PageMenu;
import org.rapidoid.gui.reqinfo.IReqInfo;
import org.rapidoid.gui.reqinfo.ReqInfo;
import org.rapidoid.gui.reqinfo.ReqInfoUtils;
import org.rapidoid.http.HttpVerb;
import org.rapidoid.render.Template;
import org.rapidoid.render.Templates;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.writable.WritableUtils;
import org.rapidoid.writable.WritableOutputStream;
import org.rapidoid.web.ScreenBean;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
@Since("5.0.0")
public class HtmlPage extends ScreenBean {

	private static volatile String commonJs = Msc.isPlatform() ? "_rapidoid/platform.js" : "application.js";
	private static volatile String commonCss = Msc.isPlatform() ? "_rapidoid/platform.css" : "application.css";

	private static volatile Template PAGE_TEMPLATE = Templates.load("page.html");
	private static volatile Template PAGE_AJAX_TEMPLATE = Templates.load("page-ajax.html");

	public HtmlPage(Object[] content) {
		content(content);
	}

	@Override
	public String render() {
		Map<String, Object> model = pageModel();

		if (menu() != null) {
			PageMenu.from(menu()).renderContentTemplates(model);
		}

		String html;
		IReqInfo req = ReqInfo.get();
		if (req.isGetReq() && !GUI.isEvent()) {
			html = PAGE_TEMPLATE.render(model);
		} else {
			html = PAGE_AJAX_TEMPLATE.render(model);
		}

		return html;
	}

	@Override
	public void render(OutputStream out) {
		String html = render();
		WritableUtils.writeUTF8(new WritableOutputStream(out), html);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> pageModel() {
		IReqInfo req = ReqInfo.get();

		Map<String, Object> model = U.map(req.data());

		model.put("req", req);

		model.put("appUrl", ReqInfoUtils.appUrl());
		model.put("adminUrl", ReqInfoUtils.adminUrl());

		model.put("loginUri", Msc.specialUri("login"));
		model.put("logoutUri", Msc.specialUri("logout"));
		model.put("profileUri", Msc.semiSpecialUri("profile"));
		model.put("graphsUri", Msc.specialUri("graphs"));

		model.put("dev", Msc.isDev());
		model.put("admin", "admin".equalsIgnoreCase(req.zone()));

		model.put("host", req.host());
		model.put("verb", req.verb());
		model.put("uri", req.uri());
		model.put("path", req.path());
		model.put("zone", req.zone());

		model.put("username", req.username());

		Set<String> roles = req.roles();
		model.put("roles", roles);

		model.put("has", has());

		model.put("content", GUI.multi(content()));
		model.put("home", req.contextPath() + home());
		model.put("brand", brand());
		model.put("title", title());

		PageMenu pageMenu = PageMenu.from(menu());
		pageMenu.uri(req.path());
		model.put("menu", pageMenu);

		model.put("version", RapidoidInfo.version());
		model.put("embedded", embedded() || req.attrs().get("_embedded") != null || req.params().get("_embedded") != null);

		model.put("search", search());
		model.put("navbar", navbar());
		model.put("fluid", fluid());
		model.put("cdn", cdn());

		List<String> assets = (List<String>) Conf.GUI.entry("assets").getOrNull();

		if (U.notEmpty(assets)) {
			for (String asset : assets) {
				String res = asset.toLowerCase();

				if (res.endsWith(".js")) {
					js().add(res);
				} else if (res.endsWith(".css")) {
					css().add(res);
				} else {
					throw U.rte("Expected .css or .js asset, but found: " + res);
				}
			}
		}

		setupAssets(req, model);

		return model;
	}

	private void setupAssets(IReqInfo req, Map<String, Object> model) {
		String view = req.view();

		if (req.hasRoute(HttpVerb.GET, commonJs)) {
			js().add(commonJs);
		}

		if (req.hasRoute(HttpVerb.GET, commonCss)) {
			css().add(commonCss);
		}

		if (U.notEmpty(view)) {
			String pageJs = "/" + view + ".js";
			if (req.hasRoute(HttpVerb.GET, pageJs)) {
				js().add(pageJs);
			}

			String pageCss = "/" + view + ".css";
			if (req.hasRoute(HttpVerb.GET, pageCss)) {
				css().add(pageCss);
			}
		}

		model.put("js", withContextPath(js(), req.contextPath()));
		model.put("css", withContextPath(css(), req.contextPath()));
	}

	private static Set<String> withContextPath(Set<String> assets, String contextPath) {
		Set<String> withContextPath = U.set();

		for (String asset : assets) {
			if (!asset.startsWith("http://") && !asset.startsWith("https://") && !asset.startsWith("//")) {
				asset = contextPath + "/" + Str.triml(asset, "/");
			}

			withContextPath.add(asset);
		}

		return withContextPath;
	}

	private Map<String, Object> has() {
		Map<String, Object> has = U.map();

		has.put("role", HtmlPageUtils.HAS_ROLE);
		has.put("path", HtmlPageUtils.HAS_PATH);
		has.put("zone", HtmlPageUtils.HAS_ZONE);
		has.put("page", HtmlPageUtils.HAS_PAGE);
		has.put("special_page", HtmlPageUtils.HAS_SPECIAL_PAGE);

		return has;
	}

	public static String commonJs() {
		return commonJs;
	}

	public static void commonJs(String commonJs) {
		HtmlPage.commonJs = commonJs;
	}

	public static String commonCss() {
		return commonCss;
	}

	public static void commonCss(String commonCss) {
		HtmlPage.commonCss = commonCss;
	}

	@Override
	public String toString() {
		return render();
	}
}
