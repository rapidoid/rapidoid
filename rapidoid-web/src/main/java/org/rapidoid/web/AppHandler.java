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
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.MediaType;
import org.rapidoid.commons.RapidoidInfo;
import org.rapidoid.config.Conf;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.data.JSON;
import org.rapidoid.http.Req;
import org.rapidoid.http.Resp;
import org.rapidoid.http.fast.FastHttp;
import org.rapidoid.http.fast.HttpStatus;
import org.rapidoid.http.fast.HttpUtils;
import org.rapidoid.http.fast.handler.FastHttpHandler;
import org.rapidoid.http.fast.handler.FastParamsAwareHttpHandler;
import org.rapidoid.io.Res;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.plugins.templates.ITemplate;
import org.rapidoid.plugins.templates.Templates;
import org.rapidoid.pojo.*;
import org.rapidoid.pojo.impl.DispatchReqKind;
import org.rapidoid.pojo.web.WebEventReq;
import org.rapidoid.pojo.web.WebReq;
import org.rapidoid.u.U;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class AppHandler extends FastParamsAwareHttpHandler {

	private static final String PAGE_RELOAD = "<h2>&nbsp;Reloading...</h2><script>location.reload();</script>";

	private static final Pattern DIRECTIVE = Pattern.compile("\\s*<!--\\s*#\\s*(\\{.+\\})\\s*-->\\s*");

	private final PojoDispatcher dispatcher;

	private static volatile ITemplate PAGE_TEMPLATE;

	public AppHandler(FastHttp http, PojoDispatcher dispatcher) {
		super(http, null, null);
		this.dispatcher = dispatcher;
	}

	@Override
	protected Object doHandle(Channel channel, boolean isKeepAlive, Req req, Object extra) throws Exception {

		// static files

		FastHttpHandler staticResourcesHandler = http.getStaticResourcesHandler();

		if (HttpUtils.isGetReq(req) && staticResourcesHandler != null) {
			HttpStatus status = staticResourcesHandler.handle(channel, isKeepAlive, req, null);
			if (status != HttpStatus.NOT_FOUND) {
				return status;
			}
		}

		// Prepare GUI state

		// x.loadState();

		// if an event was emitted, process it

		boolean hasEvent = HttpUtils.isPostReq(req) && !U.isEmpty(req.data("_event", null));

		Object result = null;

		if (hasEvent) {
			DispatchResult dispatchResult = doDispatch(dispatcher, new WebReq(req));
			if (dispatchResult != null) {
				U.must(dispatchResult.getKind() == DispatchReqKind.PAGE);
				result = dispatchResult.getResult();
			}

			// in case of binding or validation errors
			// if (x.hasErrors()) {
			// x.json();
			// return U.map("!errors", x.errors());
			// }
		}

		Map<String, Object> config = null;

		// dispatch REST services or PAGES (as POJO methods)

		if (result == null) {
			DispatchResult dres = doDispatch(dispatcher, new WebReq(req));

			if (dres != null) {
				result = dres.getResult();
				config = dres.getConfig();

				if (dres.getKind() == DispatchReqKind.SERVICE) {
					return req.response().contentType(MediaType.JSON_UTF_8).content(result);
				}
			}
		}

		if (config == null) {
			config = U.map();
		}

		// serve dynamic pages from a script
		// if (result == null) {
		// if (Scripting.runDynamicScript(x, hasEvent, config)) {
		// return x;
		// }
		// }

		return view(req, result, hasEvent, config);
	}

	public Resp view(Req x, Object result, boolean hasEvent, Map<String, Object> config) {
		// serve dynamic pages from file templates

		x.response().contentType(MediaType.HTML_UTF_8);

		if (Cls.bool(config.get("raw"))) {
			return x.response().content(result);
		}

		if (serveDynamicPage(x, result, hasEvent, config)) {
			return x.response();
		}

		if (result != null) {
			return x.response().content(result);
		}

		return null;
	}

	private DispatchResult doDispatch(PojoDispatcher dispatcher, PojoRequest req) {
		try {
			return dispatcher.dispatch(req);
		} catch (PojoHandlerNotFoundException e) {
			// / just ignore, will try to dispatch a page next...
			return null;
		} catch (PojoDispatchException e) {
			throw U.rte("Dispatch error!", e);
		}
	}

	public boolean serveDynamicPage(Req req, Object result, boolean hasEvent, Map<String, Object> config) {

		String filename = HttpUtils.resName(req) + ".html";
		Res res = Res.from(filename, U.path(Conf.rootPath(), "pages"));

		Map<String, Object> model = U.cast(U.map("login", true, "profile", true));

		if (res.exists()) {
			model.putAll(generatePageContent(req, result, res));
		} else if (result != null) {
			model.put("result", result);
			model.put("content", result);
		} else {
			return false;
		}

		WebApp app = Ctxs.ctx().app();
		String title = app != null ? app.getTitle() : null;
		title = U.or(title, req.host());

		model.put("title", title);
		model.put("embedded", req.attr("_embedded", null) != null);
		model.put("home", "/");

		// the @Page configuration overrides the previous

		if (config != null) {
			model.putAll(config);
		}

		if (!Cls.bool(model.get("navbar"))) {
			model.put("navbar", !U.isEmpty(model.get("title")));
		}

		if (hasEvent) {
			serveEventResponse(req, renderPageToHTML(req, model));
		} else {
			renderPage(req, model);
		}

		return true;
	}

	private void serveEventResponse(Req x, String html) {
		x.response().code(200);

		if (x.response().redirect() != null) {
			x.response().json(U.map("_redirect_", x.response().redirect()));
		} else {
			Map<String, String> sel = U.map("body", html);
			// x.response().json(U.map("_sel_", sel, "_state_", x.serializeLocals()));
			x.response().json(U.map("_sel_", sel));
		}
	}

	private Map<String, Object> generatePageContent(Req x, Object result, Res resource) {
		String template = U.safe(resource.getContent());

		Map<String, Object> model = model(x);

		model.put("result", result);

		String[] contentParts = template.split("\n", 2);
		if (contentParts.length == 2) {
			String line = contentParts[0];

			Matcher m = DIRECTIVE.matcher(line);
			if (m.matches()) {
				Map<String, Object> directives = JSON.parseMap(m.group(1));
				model.putAll(directives);
				template = contentParts[1]; // without the directive
			}
		}

		String content = Templates.fromString(template).render(result, model);
		model.put("content", content); // content without the directive
		return model;
	}

	public DispatchResult on(Req x, PojoDispatcher dispatcher, String event, Object[] args) {

		// Map<String, Object> state = U.cast(x.locals());
		Map<String, Object> state = null;
		WebEventReq req = new WebEventReq(x.path(), event.toUpperCase(), args, state);

		return doDispatch(dispatcher, req);
	}

	public final void reload(Req x) {
		Map<String, String> sel = U.map("body", PAGE_RELOAD);
		x.response().json(U.map("_sel_", sel));
	}

	public void render(Req req, ITemplate template, Object model) {
		template.render(req.out(), model, model(req));
	}

	private void renderPage(Req x, Map<String, Object> model) {
		pageTemplate().render(x.out(), model);
	}

	private String renderPageToHTML(Req x, Map<String, Object> model) {
		return pageTemplate().render(x, model);
	}

	private ITemplate pageTemplate() {
		if (PAGE_TEMPLATE == null) {
			PAGE_TEMPLATE = Templates.fromFile("page.html");
		}

		return PAGE_TEMPLATE;
	}

	public Map<String, Object> model(Req x) {

		Map<String, Object> model = U.map("req", x, "data", x.data(), "files", x.files(), "cookies", x.cookies(),
				"headers", x.headers());

		model.put("verb", x.verb());
		model.put("uri", x.uri());
		model.put("path", x.path());
		model.put("host", x.host());
		model.put("dev", HttpUtils.isDevMode(x));
		model.put("home", "/");

		model.putAll(x.attrs());
		WebApp app = Ctxs.ctx().app();
		model.put("app", app);
		model.put("menu", app != null ? app.getMenu() : null);

		List<String> providers = U.list("google", "facebook", "linkedin", "github");
		Map<String, Object> oauth = U.map("popup", true, "providers", providers);
		model.put("oauth", oauth);

		boolean loggedIn = Ctxs.ctx().isLoggedIn();
		model.put("loggedIn", loggedIn);
		model.put("user", loggedIn ? Ctxs.ctx().user() : null);

		model.put("version", RapidoidInfo.version());

		return model;
	}

}
