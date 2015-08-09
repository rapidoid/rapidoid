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
import org.rapidoid.dispatch.DispatchResult;
import org.rapidoid.dispatch.PojoDispatchException;
import org.rapidoid.dispatch.PojoDispatcher;
import org.rapidoid.dispatch.PojoHandlerNotFoundException;
import org.rapidoid.dispatch.PojoRequest;
import org.rapidoid.dispatch.impl.DispatchReqKind;
import org.rapidoid.http.Handler;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.http.HttpExchangeImpl;
import org.rapidoid.http.HttpExchangeInternals;
import org.rapidoid.http.HttpNotFoundException;
import org.rapidoid.io.CustomizableClassLoader;
import org.rapidoid.io.Res;
import org.rapidoid.jackson.JSON;
import org.rapidoid.log.Log;
import org.rapidoid.plugins.templates.Templates;
import org.rapidoid.util.Constants;
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

	private static final Pattern DIRECTIVE = Pattern.compile("\\s*\\Q<!--#\\E\\s*(\\{.+\\})\\s*\\Q-->\\E\\s*");

	private CustomizableClassLoader classLoader;

	public AppHandler() {
		this(null);
	}

	public AppHandler(CustomizableClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public Object handle(final HttpExchange x) throws Exception {

		HttpExchangeInternals xi = (HttpExchangeInternals) x;
		xi.setClassLoader(classLoader);

		Object result;

		try {
			result = processReq(x);
		} catch (Exception e) {
			if (UTILS.rootCause(e) instanceof HttpNotFoundException) {
				throw U.rte(e);
			} else {
				// Log.error("Exception occured while processing request!", UTILS.rootCause(e));
				throw U.rte(e);
			}
		}

		return result;
	}

	public Object processReq(HttpExchange x) {
		Object result = dispatch((HttpExchangeImpl) x);

		if (result != null) {
			return result;
		} else {
			throw x.notFound();
		}
	}

	@SuppressWarnings("unchecked")
	public Object dispatch(HttpExchangeImpl x) {

		// static files

		if (x.isGetReq() && x.serveStaticFile()) {
			return x;
		}

		WebApp app = AppCtx.app();
		PojoDispatcher dispatcher = app.getDispatcher();

		boolean hasEvent = false;

		// Prepare GUI state

		x.loadState();

		// if an event was emitted, process it

		if (x.isPostReq()) {
			String event = x.posted("_event", null);
			if (!U.isEmpty(event)) {
				hasEvent = true;

				String evArgs = x.posted("_args", null);
				Object[] args = evArgs != null ? JSON.jacksonParse(evArgs, Object[].class) : Constants.EMPTY_ARRAY;

				String inputstr = x.posted("_inputs");
				U.notNull(inputstr, "inputs");
				Map<String, Object> inputs = JSON.parse(inputstr, Map.class);

				// bind inputs
				for (Entry<String, Object> e : inputs.entrySet()) {
					String inputId = e.getKey();
					Object value = e.getValue();

					x.locals().put(inputId, UTILS.serializable(value));
				}

				DispatchResult dispatchResult = doDispatch(dispatcher, new WebReq(x));
				U.must(dispatchResult != null && dispatchResult.getKind() == DispatchReqKind.PAGE);

				// in case of binding or validation errors
				if (x.hasErrors()) {
					x.json();
					return U.map("!errors", x.errors());
				}

				// call the command handler
				DispatchResult dr = on(x, dispatcher, event, args);
				if (dr == null) {
					x.json();
					Log.warn("No event handler was found!", "event", event, "page", x.path());
					return U.map();
				}
			}
		}

		// dispatch REST services or views (as POJO methods)

		DispatchResult dispatchResult = doDispatch(dispatcher, new WebReq(x));

		Object result = null;
		if (dispatchResult != null) {
			result = dispatchResult.getResult();

			if (dispatchResult.getKind() == DispatchReqKind.SERVICE) {
				return result;
			}
		}

		if (result == null) {
			// try generic app screens
			result = GenericGUI.genericScreen();
		}

		// serve dynamic pages from file templates

		if (serveDynamicPage(x, result, hasEvent)) {
			return x;
		}

		if (result != null) {
			return result;
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

	public boolean serveDynamicPage(HttpExchangeImpl x, Object result, boolean hasEvent) {
		String filename = "dynamic/" + x.resourceName() + ".html";
		Res resource = Res.from(filename);

		Map<String, Object> model;
		if (resource.exists()) {
			model = pageModel(filename, result, resource);
		} else if (result != null) {
			model = U.map("result", result, "content", result, "navbar", true);
		} else {
			return false;
		}

		model.put("embedded", hasEvent || x.param("embedded", null) != null);

		if (hasEvent) {
			serveEventResponse(x, x.renderPageToHTML(model));
		} else {
			x.renderPage(model);
		}

		return true;
	}

	private void serveEventResponse(HttpExchangeImpl x, String html) {
		x.startResponse(200);
		x.json();

		if (x.redirectUrl() != null) {
			x.writeJSON(U.map("_redirect_", x.redirectUrl()));
		} else {
			Map<String, String> sel = U.map("body", html);
			x.writeJSON(U.map("_sel_", sel, "_state_", x.serializeLocals()));
		}
	}

	private static Map<String, Object> pageModel(String filename, Object result, Res resource) {
		String template = U.safe(resource.getContent());

		Map<String, Object> model = U.map("result", result);

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

		String content = Templates.fromString(template).render(model, result);
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
