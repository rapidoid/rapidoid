package org.rapidoid.pages;

/*
 * #%L
 * rapidoid-pages
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import org.rapidoid.beany.Beany;
import org.rapidoid.html.Cmd;
import org.rapidoid.html.TagContext;
import org.rapidoid.html.Tags;
import org.rapidoid.http.HTTPServer;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.http.HttpExchangeException;
import org.rapidoid.inject.IoC;
import org.rapidoid.json.JSON;
import org.rapidoid.lambda.Lambdas;
import org.rapidoid.log.Log;
import org.rapidoid.pages.impl.BuiltInCmdHandler;
import org.rapidoid.pages.impl.PageRenderer;
import org.rapidoid.pojo.PojoDispatchException;
import org.rapidoid.pojo.PojoHandlerNotFoundException;
import org.rapidoid.rest.WebPojoDispatcher;
import org.rapidoid.rest.WebReq;
import org.rapidoid.util.Arr;
import org.rapidoid.util.Cls;
import org.rapidoid.util.Conf;
import org.rapidoid.util.U;
import org.rapidoid.widget.BootstrapWidgets;

public class Pages {

	private static final String PAGE_RELOAD = "<h2>Reloading...</h2><script>location.reload();</script>";

	public static final String SESSION_CTX = "_ctx_";

	private static final BuiltInCmdHandler BUILT_IN_HANDLER = new BuiltInCmdHandler();

	public static void registerPages(HTTPServer server) {
		server.serve(new PageHandler());
	}

	@SuppressWarnings("unchecked")
	protected static Map<Integer, Object> inputs(HttpExchange x) {
		String inputs = x.data("inputs");
		U.notNull(inputs, "inputs");

		Map<Integer, Object> inputsMap = U.map();

		Map<String, Object> inp = JSON.parse(inputs, Map.class);
		for (Entry<String, Object> e : inp.entrySet()) {
			inputsMap.put(U.num(e.getKey()), e.getValue());
		}

		return inputsMap;
	}

	public static String pageName(HttpExchange x) {
		String path = x.path();

		if (path.endsWith(".html")) {
			path = U.mid(path, 0, -5);
		}

		if (path.equals("/")) {
			path = "/index";
		}

		return U.capitalized(path.substring(1));
	}

	public static String defaultPageTitle(Class<?> pageClass) {
		String pageName = pageClass.getSimpleName();

		if (pageName.endsWith("Page")) {
			pageName = U.mid(pageName, 0, -4);
		}

		return U.camelPhrase(pageName);
	}

	public static String titleOf(HttpExchange x, Object target) {
		Method m = Cls.findMethod(target.getClass(), "title", HttpExchange.class);

		if (m != null) {
			return Cls.invoke(m, target, x);
		}

		try {
			return Beany.getPropValue(target, "title");
		} catch (Exception e) {
			return defaultPageTitle(target.getClass());
		}
	}

	public static Object contentOf(HttpExchange x, Object target) {
		Method m = Cls.findMethod(target.getClass(), "content", HttpExchange.class);
		return m != null ? Cls.invoke(m, target, x) : Beany.getPropValue(target, "content", null);
	}

	public static Object headOf(HttpExchange x, Object target) {
		Method m = Cls.findMethod(target.getClass(), "head", HttpExchange.class);
		return m != null ? Cls.invoke(m, target, x) : Beany.getPropValue(target, "head", null);
	}

	public static Object page(HttpExchange x, Object page) {

		String pageTitle = titleOf(x, page);
		Object pageHead = U.or(headOf(x, page), "");
		Object content = contentOf(x, page);

		if (content == null) {
			return null;
		}

		if (content instanceof HttpExchange) {
			U.must(x == content, "Different HTTP exchange than expected!");
			return x;
		}

		return BootstrapWidgets.page(Conf.dev(), pageTitle, pageHead, content);
	}

	public static Object render(HttpExchange x, Object page) {

		Object fullPage = page(x, page);
		if (fullPage != null) {
			if (fullPage instanceof HttpExchange) {
				return x;
			} else {
				TagContext ctx = Tags.context();
				x.sessionSet(Pages.SESSION_CTX, ctx);
				PageRenderer.get().render(ctx, fullPage, x);
				return x;
			}
		} else {
			throw x.notFound();
		}
	}

	public static Object dispatch(HttpExchange x, WebPojoDispatcher serviceDispatcher, Map<String, Class<?>> pages) {

		if (x.serveStatic()) {
			return x;
		}

		if (serviceDispatcher != null) {
			try {
				return serviceDispatcher.dispatch(new WebReq(x));
			} catch (PojoHandlerNotFoundException e) {
				// / just ignore, will try to dispatch a page next...
			} catch (PojoDispatchException e) {
				return x.errorResponse(e);
			}
		}

		String pageName = Pages.pageName(x);
		if (pageName == null) {
			return null;
		}

		String pageClassName = U.capitalized(pageName) + "Page";

		Class<?> pageClass = pages.get(pageClassName);
		if (pageClass == null) {
			return null;
		}

		Object page = Cls.newInstance(pageClass);

		if (isEmiting(x)) {
			return Pages.emit(x, page);
		} else {
			return serve(x, page);
		}
	}

	public static boolean isEmiting(HttpExchange x) {
		return x.isPostReq();
	}

	public static Object serve(HttpExchange x, Object view) {
		load(x, view);

		Object result = render(x, view);

		x.addToPageStack();
		store(x, view);

		return result;
	}

	public static void load(HttpExchange x, Object target) {
		IoC.autowire(target, Lambdas.mapper(x.session()));
	}

	public static void store(HttpExchange x, Object target) {
		for (Field field : IoC.getSessionFields(target)) {
			Object value = Cls.getFieldValue(field, target);
			x.sessionSet(field.getName(), value);
		}
	}

	public static Object emit(HttpExchange x, Object view) {
		int event = U.num(x.data("event"));

		TagContext ctx = x.session(SESSION_CTX, null);

		// if the context has been lost, reload the page
		if (ctx == null) {
			return changes(x, PAGE_RELOAD);
		}

		Cmd cmd = ctx.getEventCmd(event);

		boolean navigational = cmd != null && cmd.navigational;

		if (!navigational) {
			Map<Integer, String> errors = U.map();

			if (cmd != null) {
				Map<Integer, Object> inputs = Pages.inputs(x);
				ctx.emitValues(inputs, errors);
			} else {
				Log.warn("Invalid event!", "event", event);
			}

			if (!errors.isEmpty()) {
				x.json();
				return U.map("!errors", errors);
			}
		}

		Pages.load(x, view);

		boolean processView = true;

		if (cmd != null) {
			try {
				callCmdHandler(x, view, cmd);
			} catch (Exception e) {
				Throwable cause = U.rootCause(e);
				if (cause instanceof HttpExchangeException) {
					processView = false;
				} else {
					throw U.rte(e);
				}
			}
		}

		String html;
		if (processView) {
			Object content;
			try {
				content = Pages.contentOf(x, view);
				if (content == null || content instanceof HttpExchange) {
					return content;
				}
			} catch (Exception e) {
				Throwable cause = U.rootCause(e);
				if (cause instanceof HttpExchangeException) {
					return null;
				} else {
					throw U.rte(e);
				}
			}
			ctx = Tags.context();
			x.sessionSet(Pages.SESSION_CTX, ctx);
			html = PageRenderer.get().toHTML(ctx, content, x);
		} else {
			html = "Error!";
		}

		Pages.store(x, view);

		if (x.redirectUrl() != null) {
			x.startResponse(200);
			x.json();
			return U.map("_redirect_", x.redirectUrl());
		}

		return changes(x, html);
	}

	private static Object changes(HttpExchange x, String html) {
		x.json();
		return U.map("body", html);
	}

	public static void callCmdHandler(HttpExchange x, Object target, Cmd cmd) {
		if (!callCmdHandler(x, target, cmd, false)) {
			callCmdHandler(x, BUILT_IN_HANDLER, cmd, true);
		}
	}

	private static boolean callCmdHandler(HttpExchange x, Object target, Cmd cmd, boolean failIfNotFound) {

		String handlerName = "on" + U.capitalized(cmd.name);
		Method m = Cls.findMethodByArgs(target.getClass(), handlerName, cmd.args);

		if (m != null) {
			Cls.invoke(m, target, cmd.args);
			return true;
		}

		Object[] args2 = Arr.expand(cmd.args, x);
		m = Cls.findMethodByArgs(target.getClass(), handlerName, args2);

		if (m != null) {
			Cls.invoke(m, target, args2);
			return true;
		}

		args2 = new Object[cmd.args.length + 1];
		args2[0] = x;
		System.arraycopy(cmd.args, 0, args2, 1, cmd.args.length);
		m = Cls.findMethodByArgs(target.getClass(), handlerName, args2);

		if (m != null) {
			Cls.invoke(m, target, args2);
			return true;
		}

		Method on = Cls.findMethod(target.getClass(), "on", String.class, Object[].class);
		if (on != null) {
			Cls.invoke(on, target, cmd.name, cmd.args);
			return true;
		}

		on = Cls.findMethod(target.getClass(), "on", HttpExchange.class, String.class, Object[].class);
		if (on != null) {
			Cls.invoke(on, target, x, cmd.name, cmd.args);
			return true;
		}

		if (failIfNotFound) {
			throw U.rte("Cannot find handler '%s' for the command '%s' and args: %s", handlerName, cmd.name, cmd.args);
		} else {
			return false;
		}
	}

	public static String viewId(HttpExchange x) {
		return x.uri();
	}

}
