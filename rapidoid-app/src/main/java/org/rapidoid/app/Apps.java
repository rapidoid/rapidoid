package org.rapidoid.app;

/*
 * #%L
 * rapidoid-app
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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
import java.util.concurrent.ConcurrentMap;

import org.rapidoid.http.HTTP;
import org.rapidoid.http.HTTPServer;
import org.rapidoid.http.HttpBuiltins;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.oauth.OAuth;
import org.rapidoid.util.Cls;
import org.rapidoid.util.U;

public class Apps {

	private static AppClasses APP_CLASSES;

	public static final ConcurrentMap<String, AppClasses> CLASSES = U.concurrentMap();

	public static void main(String[] args) {
		U.args(args);

		HTTPServer server = HTTP.server().build();

		OAuth.register(server);
		HttpBuiltins.register(server);

		server.serve(new AppHandler());

		server.start();
	}

	public static String screenName(Class<?> screenClass) {
		return U.mid(screenClass.getSimpleName(), 0, -6);
	}

	public static String screenUrl(Class<?> screenClass) {
		String url = "/" + screenName(screenClass).toLowerCase();
		return url.equals("/home") ? "/" : url;
	}

	public static AppClasses scanAppClasses(HttpExchange x) {
		return scanAppClasses(x, null);
	}

	public static synchronized AppClasses scanAppClasses(HttpExchange x, ClassLoader classLoader) {

		if (x.hasSession()) {
			String q = x.session("_app_classes_", null);
			if (q != null) {
				AppClasses cls = CLASSES.get(q);
				if (cls != null) {
					return cls;
				}
			}
		}

		if (APP_CLASSES == null) {

			Map<String, Class<?>> services = Cls.classMap(U.classpathClassesBySuffix("Service", null, classLoader));
			Map<String, Class<?>> pages = Cls.classMap(U.classpathClassesBySuffix("Page", null, classLoader));
			Map<String, Class<?>> apps = Cls.classMap(U.classpathClassesByName("App", null, classLoader));
			Map<String, Class<?>> screens = Cls.classMap(U.classpathClassesBySuffix("Screen", null, classLoader));

			final Class<?> appClass = !apps.isEmpty() ? apps.get("App") : TheDefaultApp.class;

			APP_CLASSES = new AppClasses(appClass, services, pages, screens);
		}

		return APP_CLASSES;
	}

	@SuppressWarnings("unchecked")
	public static <T> T config(Object obj, String configName, T byDefault) {
		Object val = Cls.getPropValue(obj, configName, null);
		return val != null ? (T) val : byDefault;
	}

	public static String urlFor(Object entity) {
		long id = Cls.getId(entity);
		String className = entity.getClass().getSimpleName();
		return U.format("/%s/%s", U.uncapitalized(className), id);
	}

}
