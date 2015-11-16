package org.rapidoid.app;

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

import java.util.Map;
import java.util.Set;

import org.rapidoid.annotation.App;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.cls.Cls;
import org.rapidoid.config.Conf;
import org.rapidoid.http.HTTPServer;
import org.rapidoid.http.HttpBuiltins;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.http.WebServer;
import org.rapidoid.log.Log;
import org.rapidoid.oauth.OAuth;
import org.rapidoid.plugins.Plugins;
import org.rapidoid.plugins.db.DBPlugin;
import org.rapidoid.plugins.entities.EntitiesPlugin;
import org.rapidoid.plugins.languages.LanguagesPlugin;
import org.rapidoid.plugins.lifecycle.Lifecycle;
import org.rapidoid.plugins.lifecycle.LifecyclePlugin;
import org.rapidoid.plugins.users.UsersPlugin;
import org.rapidoid.u.U;
import org.rapidoid.util.Usage;
import org.rapidoid.webapp.FindClasses;
import org.rapidoid.webapp.WebApp;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class Apps {

	private static final String BUILT_IN_SCREEN_SUFFIX = "BuiltIn";

	public static void run(WebApp app, String[] args, Object... config) {
		bootstrap(app, args, config);
		serve(app, args, config);
	}

	public static void bootstrap(WebApp app, String[] args, Object... config) {
		Set<String> configArgs = U.set(args);

		for (Object arg : config) {
			processArg(configArgs, arg);
		}

		String[] configArgsArr = configArgs.toArray(new String[configArgs.size()]);
		Conf.args(configArgsArr);
		Log.args(configArgsArr);

		// Plugins.register(new MustacheTemplatesPlugin());
		// AOP.intercept(new TransactionInterceptor(), Transaction.class);

		Lifecycle.onStart(configArgsArr);
	}

	public static HTTPServer serve(WebApp app, String[] args, Object... config) {
		HTTPServer server = WebServer.build();

		OAuth.register(app);
		HttpBuiltins.register(app);

		app.getRouter().serve(new AsyncAppHandler());

		return server.start();
	}

	private static void processArg(Set<String> config, Object arg) {
		Log.info("Processing start-up argument", "arg", arg);

		if (arg instanceof String) {
			config.add((String) arg);
		} else if (arg instanceof DBPlugin) {
			Plugins.register((DBPlugin) arg);
		} else if (arg instanceof EntitiesPlugin) {
			Plugins.register((EntitiesPlugin) arg);
		} else if (arg instanceof LanguagesPlugin) {
			Plugins.register((LanguagesPlugin) arg);
		} else if (arg instanceof LifecyclePlugin) {
			Plugins.register((LifecyclePlugin) arg);
		} else if (arg instanceof UsersPlugin) {
			Plugins.register((UsersPlugin) arg);
		}
	}

	public static String screenName(Class<?> screenClass) {
		String name = screenClass.getSimpleName();

		name = U.trimr(name, BUILT_IN_SCREEN_SUFFIX);
		name = U.trimr(name, "Screen");

		return name;
	}

	public static String screenUrl(Class<?> screenClass) {
		String url = "/" + screenName(screenClass).toLowerCase();
		return url.equals("/home") ? "/" : url;
	}

	public static AppClasses scanAppClasses(HttpExchange x) {
		return scanAppClasses(x, null);
	}

	public static AppClasses scanAppClasses(HttpExchange x, ClassLoader classLoader) {

		Map<String, Class<?>> apps = Cls.classMap(FindClasses.annotated(App.class, classLoader));
		Map<String, Class<?>> services = Cls.classMap(FindClasses.annotated(Controller.class, classLoader));

		final Class<?> appClass = !apps.isEmpty() ? apps.values().iterator().next() : null;

		return new AppClasses(appClass, services);
	}

	public static AppClasses getAppClasses(HttpExchange x, ClassLoader classLoader) {
		return scanAppClasses(x, classLoader);
	}

	@SuppressWarnings("unchecked")
	public static <T> T config(Object obj, String configName, T byDefault) {
		Object val = Beany.getPropValue(obj, configName, null);
		return val != null ? (T) val : byDefault;
	}

	public static boolean addon(Object obj, String configName) {
		return config(obj, configName, false) || config(obj, "full", true);
	}

	public static void terminate(final int afterSeconds) {
		Log.warn("Terminating application in " + afterSeconds + " seconds...");
		new Thread() {
			@Override
			public void run() {
				U.sleep(afterSeconds * 1000);
				terminate();
			}
		}.start();
	}

	public static void terminateIfIdleFor(final int idleSeconds) {
		Log.warn("Will terminate if idle for " + idleSeconds + " seconds...");

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!Thread.interrupted()) {
					U.sleep(500);
					long lastUsed = Usage.getLastAppUsedOn();
					long idleSec = (U.time() - lastUsed) / 1000;
					if (idleSec >= idleSeconds) {
						Usage.touchLastAppUsedOn();
						terminate();
					}
				}
			}
		}).start();
	}

	public static void terminate() {
		Log.warn("Terminating application.");
		Lifecycle.onShutdown();
		System.exit(0);
	}

	public static Object instantiate(Class<?> appClass, HttpExchange x) {
		return appClass != null ? Cls.newInstance(appClass) : new Object();
	}

}
