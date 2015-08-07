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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.dispatch.PojoDispatchException;
import org.rapidoid.dispatch.PojoHandlerNotFoundException;
import org.rapidoid.http.Handler;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.http.HttpExchangeInternals;
import org.rapidoid.http.HttpNotFoundException;
import org.rapidoid.io.CustomizableClassLoader;
import org.rapidoid.io.Res;
import org.rapidoid.log.Log;
import org.rapidoid.plugins.templates.Templates;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class AppHandler implements Handler {

	private static final Pattern DIRECTIVE = Pattern.compile("\\s*\\Q<!--\\E\\s+([\\w\\+\\-\\, ]+)\\s+\\Q-->\\E\\s*");

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
		HttpExchangeInternals xi = (HttpExchangeInternals) x;

		final AppClasses appCls = Apps.getAppClasses(x, xi.getClassLoader());

		Object result = dispatch(x, appCls);

		if (result != null) {
			return result;
		} else {
			throw x.notFound();
		}
	}

	public Object dispatch(HttpExchange x, AppClasses appCls) {
		HttpExchangeInternals xi = (HttpExchangeInternals) x;

		// static files

		if (x.isGetReq() && x.serveStaticFile()) {
			return x;
		}

		// REST services or views (as POJO methods)

		Object result = null;
		if (appCls.dispatcher != null) {
			try {
				result = appCls.dispatcher.dispatch(new WebReq(x));
			} catch (PojoHandlerNotFoundException e) {
				// / just ignore, will try to dispatch a page next...
			} catch (PojoDispatchException e) {
				return x.error(e);
			}
		}

		// Prepare GUI state

		xi.loadState();

		// Instantiate main app object (if possible)

		Object app = Apps.instantiate(appCls.main, x);

		if (result == null) {
			// try generic app screens
			result = genericScreen();
		}

		// serve pages from file templates

		if (smartServeFromFile(x, "dynamic/" + x.resourceName() + ".html", app, result)) {
			return x;
		}

		if (result != null) {
			return result;
		}

		throw x.notFound();
	}

	public static boolean smartServeFromFile(HttpExchange x, String filename, Object app, Object result) {
		Res resource = Res.from(filename);

		if (resource.exists()) {
			x.html();

			String template = U.safe(resource.getContent());

			Map<String, Object> model = U.map("result", result);

			String[] contentParts = template.split("\n", 2);
			if (contentParts.length == 2) {
				String line = contentParts[0];

				Matcher m = DIRECTIVE.matcher(line);
				if (m.matches()) {
					String directives = m.group(1);
					for (String directive : directives.split(",")) {
						directive = directive.trim();
						if (!U.isEmpty(directive)) {
							if (directive.startsWith("+")) {
								model.put(directive.substring(1), true);
							} else if (directive.startsWith("-")) {
								model.put(directive.substring(1), false);
							} else {
								Log.warn("Unknown directive!", "directive", directive, "file", filename);
							}
						}
					}

					template = contentParts[1]; // without the directive
				}
			}

			model.put("content", template);
			String content = Templates.fromString(template).render(model, result);

			model.put("content", content); // content without the directive

			x.renderPage(model);

			return true;
		} else {
			return false;
		}
	}

	protected Object genericScreen() {
		// String path = x.path();
		//
		// if (path.equals("/")) {
		// return appCls.main != null ? app : new Object();
		// }
		//
		// for (Class<?> scr : BUILT_IN_SCREENS) {
		// if (Apps.screenUrl(scr).equals(path)) {
		// return Cls.newInstance(scr);
		// }
		// }
		//
		// if (!x.query().isEmpty()) {
		// return null;
		// }
		//
		// Matcher m = ENTITY_EDIT.matcher(path);
		//
		// if (m.find()) {
		// String type = m.group(1);
		// String id = m.group(2);
		//
		// Class<?> entityType = Scaffolding.getScaffoldingEntity(type);
		// if (entityType == null) {
		// return null;
		// }
		//
		// Object entity = DB.getIfExists(entityType, id);
		//
		// String entityClass = Cls.entityName(entity);
		// String reqType = U.capitalized(type);
		//
		// if (entityClass.equals(reqType)) {
		// return new EditEntityScreenGeneric(entityType);
		// }
		// }
		//
		// m = ENTITY_NEW.matcher(path);
		//
		// if (m.find()) {
		// String type = m.group(1);
		//
		// Class<?> entityType = Scaffolding.getScaffoldingEntity(type);
		// if (entityType == null) {
		// return null;
		// }
		//
		// return new NewEntityScreenGeneric(entityType);
		// }
		//
		// m = ENTITY_VIEW.matcher(path);
		//
		// if (m.find()) {
		// String type = m.group(1);
		// String id = m.group(2);
		//
		// Class<?> entityType = Scaffolding.getScaffoldingEntity(type);
		// if (entityType == null) {
		// return null;
		// }
		//
		// Object entity = DB.getIfExists(entityType, id);
		//
		// String entityClass = Cls.entityName(entity);
		// String reqType = U.capitalized(type);
		//
		// if (entityClass.equals(reqType)) {
		// return new ViewEntityScreenGeneric(entityType);
		// }
		// }
		//
		// m = ENTITY_LIST.matcher(path);
		//
		// if (m.find()) {
		// String type = m.group(1);
		// String type2 = U.or(Languages.pluralToSingular(type), type);
		//
		// Class<?> entityType = Scaffolding.getScaffoldingEntity(type2);
		// if (entityType == null) {
		// return null;
		// }
		//
		// return new ListEntityScreenGeneric(entityType);
		// }
		//
		// return null;

		return null;
	}

	// public void on(String cmd, Object[] args) {
	// try {
	// Pages.callCmdHandler(x, screen, new Cmd(cmd, false, args));
	// } catch (Exception e) {
	// Throwable cause = UTILS.rootCause(e);
	// if (cause instanceof HttpSuccessException || cause instanceof HttpNotFoundException) {
	// Pages.store(x, screen);
	// }
	// throw U.rte(e);
	// }
	// }

}
