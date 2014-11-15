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

import java.util.List;

import org.rapidoid.http.HTTP;
import org.rapidoid.http.HTTPServer;
import org.rapidoid.http.HttpBuiltins;
import org.rapidoid.oauth.OAuth;
import org.rapidoid.pages.Pages;
import org.rapidoid.util.Cls;
import org.rapidoid.util.U;

public class Apps {

	public static void main(String[] args) {
		U.args(args);

		HTTPServer server = HTTP.server().build();

		OAuth.register(server);
		HttpBuiltins.register(server);
		Pages.registerEmitHandler(server);

		server.serve(new AppHandler(scanAppClasses(null)));
		server.start();
	}

	public static String screenName(Object screen) {
		return U.mid(screen.getClass().getSimpleName(), 0, -6);
	}

	public static String screenUrl(Object screen) {
		String url = "/" + screenName(screen).toLowerCase();
		return url.equals("/home") ? "/" : url;
	}

	public static AppStructure scanAppClasses() {
		return scanAppClasses(null);
	}

	public static AppStructure scanAppClasses(ClassLoader classLoader) {
		List<Class<?>> services = U.classpathClassesBySuffix("Service", null, classLoader);
		List<Class<?>> apps = U.classpathClassesByName("App", null, classLoader);
		List<Class<?>> screens = U.classpathClassesBySuffix("Screen", null, classLoader);

		U.must(apps.size() <= 1, "Found more than one applications (classes named 'App')!", "classes", apps);
		final Class<?> appClass = !apps.isEmpty() ? apps.get(0) : TheDefaultApp.class;

		Object app = U.newInstance(appClass);
		Object[] screensConfig = Apps.config(app, "screens", null);

		if (screensConfig != null) {
			screens.clear();
			for (Object scr : screensConfig) {
				screens.add((Class<?>) scr);
			}
		}

		return new AppStructure(appClass, screens, services);
	}

	@SuppressWarnings("unchecked")
	public static <T> T config(Object obj, String configName, T byDefault) {
		Object val = Cls.getFieldValue(obj, configName, null);
		return val != null ? (T) val : byDefault;
	}

}
