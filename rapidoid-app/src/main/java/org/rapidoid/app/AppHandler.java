package org.rapidoid.app;

/*
 * #%L
 * rapidoid-app
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
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.config.Conf;
import org.rapidoid.dispatch.DispatchResult;
import org.rapidoid.dispatch.PojoDispatchException;
import org.rapidoid.dispatch.PojoDispatcher;
import org.rapidoid.dispatch.PojoHandlerNotFoundException;
import org.rapidoid.dispatch.PojoRequest;
import org.rapidoid.dispatch.impl.DispatchReqKind;
import org.rapidoid.http.Handler;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.http.HttpExchangeImpl;
import org.rapidoid.io.Res;
import org.rapidoid.jackson.JSON;
import org.rapidoid.plugins.templates.Templates;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;
import org.rapidoid.webapp.AppCtx;
import org.rapidoid.webapp.WebApp;
import org.rapidoid.webapp.WebEventReq;
import org.rapidoid.webapp.WebReq;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class AppHandler implements Handler {

	private static final String PAGE_RELOAD = "<h2>&nbsp;Reloading...</h2><script>location.reload();</script>";

	private static final Pattern DIRECTIVE = Pattern.compile("\\s*<!--\\s*#\\s*(\\{.+\\})\\s*-->\\s*");

	@Override
	public Object handle(final HttpExchange x) throws Exception {
		return dispatch((HttpExchangeImpl) x);
	}

	private Object dispatch(HttpExchangeImpl x) {

		// static files

		if (x.isGetReq() && x.serveStaticFile()) {
			return x;
		}

		WebApp app = AppCtx.app();
		PojoDispatcher dispatcher = app.getDispatcher();

		// Prepare GUI state

		x.loadState();

		// if an event was emitted, process it

		boolean hasEvent = x.isPostReq() && !U.isEmpty(x.data("_event", null));

		Object result = null;

		if (hasEvent) {
			bindInputs(x);

			DispatchResult dispatchResult = doDispatch(dispatcher, new WebReq(x));
			if (dispatchResult != null) {
				U.must(dispatchResult.getKind() == DispatchReqKind.PAGE);
				result = dispatchResult.getResult();
			}

			// in case of binding or validation errors
			if (x.hasErrors()) {
				x.json();
				return U.map("!errors", x.errors());
			}
		}

		Map<String, Object> config = null;

		// dispatch REST services or PAGES (as POJO methods)

		if (result == null) {
			DispatchResult dres = doDispatch(dispatcher, new WebReq(x));

			if (dres != null) {
				result = dres.getResult();
				config = dres.getConfig();

				if (dres.getKind() == DispatchReqKind.SERVICE) {
					x.json();
					return result;
				}
			}
		}

		if (config == null) {
			config = U.map();
		}

		// serve dynamic pages from a script

		if (result == null) {
			if (Scripting.runDynamicScript(x, hasEvent, config)) {
				return x;
			}
		}

		if (result == null) {
			// try generic app screens
			result = GenericGUI.genericScreen();
		}

		return view(x, result, hasEvent, config);
	}

	private void bindInputs(HttpExchangeImpl x) {
		Map<String, Object> inputs = x.data("_inputs", null);

		if (inputs != null) {
			// bind inputs
			for (Entry<String, Object> e : inputs.entrySet()) {
				String inputId = e.getKey();
				Object value = e.getValue();

				x.locals().put(inputId, UTILS.serializable(value));
			}
		}
	}

	public static Object view(HttpExchange x, Object result, boolean hasEvent, Map<String, Object> config) {
		// serve dynamic pages from file templates

		if (Cls.bool(config.get("raw"))) {
			x.html();
			return result;
		}

		if (serveDynamicPage(x, result, hasEvent, config)) {
			return x;
		}

		if (result != null) {
			x.result(result);
			return x;
		}

		throw x.notFound();
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

	public static boolean serveDynamicPage(HttpExchange x, Object result, boolean hasEvent, Map<String, Object> config) {
		String filename = x.resourceName() + ".html";
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

		WebApp app = AppCtx.app();

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
			serveEventResponse((HttpExchangeImpl) x, x.renderPageToHTML(model));
		} else {
			x.renderPage(model);
		}

		return true;
	}

	private static void serveEventResponse(HttpExchangeImpl x, String html) {
		x.startResponse(200);
		x.json();

		if (x.redirectUrl() != null) {
			x.writeJSON(U.map("_redirect_", x.redirectUrl()));
		} else {
			Map<String, String> sel = U.map("body", html);
			x.writeJSON(U.map("_sel_", sel, "_state_", x.serializeLocals()));
		}
	}

	private static Map<String, Object> generatePageContent(HttpExchange x, Object result, Res resource) {
		String template = U.safe(resource.getContent());

		Map<String, Object> model = x.model();

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

	public DispatchResult on(HttpExchange x, PojoDispatcher dispatcher, String event, Object[] args) {

		Map<String, Object> state = U.cast(x.locals());
		WebEventReq req = new WebEventReq(x.path(), event.toUpperCase(), args, state);

		return doDispatch(dispatcher, req);
	}

	public static final void reload(HttpExchange x) {
		Map<String, String> sel = U.map("body", PAGE_RELOAD);
		x.writeJSON(U.map("_sel_", sel));
	}

}
