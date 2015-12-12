package org.rapidoid.web;

/*
 * #%L
 * rapidoid-web
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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.MediaType;
import org.rapidoid.commons.RapidoidInfo;
import org.rapidoid.config.Conf;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.data.JSON;
import org.rapidoid.dispatch.DispatchResult;
import org.rapidoid.dispatch.PojoDispatchException;
import org.rapidoid.dispatch.PojoDispatcher;
import org.rapidoid.dispatch.PojoHandlerNotFoundException;
import org.rapidoid.dispatch.PojoRequest;
import org.rapidoid.dispatch.impl.DispatchReqKind;
import org.rapidoid.http.Req;
import org.rapidoid.http.fast.FastHttp;
import org.rapidoid.http.fast.HttpStatus;
import org.rapidoid.http.fast.HttpUtils;
import org.rapidoid.http.fast.handler.FastHttpHandler;
import org.rapidoid.http.fast.handler.FastParamsAwareHttpHandler;
import org.rapidoid.io.Res;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.plugins.templates.ITemplate;
import org.rapidoid.plugins.templates.Templates;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class AppHandler extends FastParamsAwareHttpHandler {

	private static final String PAGE_RELOAD = "<h2>&nbsp;Reloading...</h2><script>location.reload();</script>";

	private static final Pattern DIRECTIVE = Pattern.compile("\\s*<!--\\s*#\\s*(\\{.+\\})\\s*-->\\s*");

	private static volatile ITemplate PAGE_TEMPLATE;

	public AppHandler(FastHttp http) {
		super(http, null, null);
	}

	@Override
	protected Object doHandle(Channel channel, boolean isKeepAlive, Req req) throws Exception {

		// static files

		FastHttpHandler staticResourcesHandler = http.getStaticResourcesHandler();

		if (HttpUtils.isGetReq(req) && staticResourcesHandler != null) {
			HttpStatus status = staticResourcesHandler.handle(channel, isKeepAlive, req);
			if (status != HttpStatus.NOT_FOUND) {
				return status;
			}
		}

		WebApp app = Ctxs.ctx().app();
		PojoDispatcher dispatcher = app.getDispatcher();

		// Prepare GUI state

		// x.loadState();

		// if an event was emitted, process it

		boolean hasEvent = HttpUtils.isPostReq(req) && !U.isEmpty(req.data("_event", null));

		Object result = null;

		if (hasEvent) {
			bindInputs(req);

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
					req.response().contentType(MediaType.JSON_UTF_8);
					return result;
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

	private void bindInputs(Req x) {
		Map<String, Object> inputs = x.data("_inputs", null);

		if (inputs != null) {
			// bind inputs
			for (Entry<String, Object> e : inputs.entrySet()) {
				String inputId = e.getKey();
				Object value = e.getValue();

				// x.locals().put(inputId, UTILS.serializable(value));
			}
		}
	}

	public static Object view(Req x, Object result, boolean hasEvent, Map<String, Object> config) {
		// serve dynamic pages from file templates

		if (Cls.bool(config.get("raw"))) {
			x.response().contentType(MediaType.HTML_UTF_8);
			return result;
		}

		if (serveDynamicPage(x, result, hasEvent, config)) {
			return x;
		}

		if (result != null) {
			return x.response().content(result);
		}

		return null;
	}

	private static DispatchResult doDispatch(PojoDispatcher dispatcher, PojoRequest req) {
		try {
			return dispatcher.dispatch(req);
		} catch (PojoHandlerNotFoundException e) {
			// / just ignore, will try to dispatch a page next...
			return null;
		} catch (PojoDispatchException e) {
			throw U.rte("Dispatch error!", e);
		}
	}

	public static boolean serveDynamicPage(Req x, Object result, boolean hasEvent, Map<String, Object> config) {
		String filename = HttpUtils.resName(x) + ".html";
		String firstFile = Conf.rootPath() + "/pages/" + filename;
		String defaultFile = Conf.rootPathDefault() + "/pages/" + filename;
		Res res = Res.from(filename, true, firstFile, defaultFile);

		Map<String, Object> model = U.cast(U.map("login", true, "profile", true));

		if (res.exists()) {
			model.putAll(generatePageContent(x, result, res));
		} else if (result != null) {
			model.put("result", result);
			model.put("content", result);
		} else {
			return false;
		}

		WebApp app = Ctxs.ctx().app();

		String title = U.or(app.getTitle(), x.host());
		model.put("title", title);
		model.put("embedded", hasEvent || x.param("_embedded", null) != null);

		// the @Page configuration overrides the previous

		if (config != null) {
			model.putAll(config);
		}

		if (!Cls.bool(model.get("navbar"))) {
			model.put("navbar", !U.isEmpty(model.get("title")));
		}

		if (hasEvent && x.param("_embedded", null) == null) {
			serveEventResponse((Req) x, renderPageToHTML(x, model));
		} else {
			renderPage(x, model);
		}

		return true;
	}

	private static void renderPage(Req x, Map<String, Object> model) {
		// TODO Auto-generated method stub

	}

	private static String renderPageToHTML(Req x, Map<String, Object> model) {
		// TODO Auto-generated method stub
		return null;
	}

	private static void serveEventResponse(Req x, String html) {
		x.response().code(200);

		if (x.response().redirect() != null) {
			x.response().json(U.map("_redirect_", x.response().redirect()));
		} else {
			Map<String, String> sel = U.map("body", html);
			// x.response().json(U.map("_sel_", sel, "_state_", x.serializeLocals()));
			x.response().json(U.map("_sel_", sel));
		}
	}

	private static Map<String, Object> generatePageContent(Req x, Object result, Res resource) {
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

	public static DispatchResult on(Req x, PojoDispatcher dispatcher, String event, Object[] args) {

		// Map<String, Object> state = U.cast(x.locals());
		Map<String, Object> state = null;
		WebEventReq req = new WebEventReq(x.path(), event.toUpperCase(), args, state);

		return doDispatch(dispatcher, req);
	}

	public static final void reload(Req x) {
		Map<String, String> sel = U.map("body", PAGE_RELOAD);
		x.response().json(U.map("_sel_", sel));
	}

	public static void render(Req req, ITemplate template, Object model) {
		template.render(req.out(), model, model(req));
	}

	public static void renderPage(Req req, Object model) {
		req.response().contentType(MediaType.HTML_UTF_8);
		pageTemplate().render(req.out(), model, model(req));
		req.done();
	}

	public static String renderPageToHTML(Req x, Object model) {
		return pageTemplate().render(model, model(x));
	}

	private static ITemplate pageTemplate() {
		if (PAGE_TEMPLATE == null) {
			PAGE_TEMPLATE = Templates.fromFile("page.html");
		}
		return PAGE_TEMPLATE;
	}

	public static Map<String, Object> model(Req x) {

		Map<String, Object> model = U.map("req", x, "data", x.data(), "files", x.files(), "cookies", x.cookies(),
				"headers", x.headers());

		model.put("verb", x.verb());
		model.put("uri", x.uri());
		model.put("path", x.path());
		model.put("host", x.host());
		model.put("dev", HttpUtils.isDevMode(x));

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
