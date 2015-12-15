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

import java.util.Set;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.aop.AOP;
import org.rapidoid.commons.RapidoidInfo;
import org.rapidoid.config.Conf;
import org.rapidoid.config.ConfigHelp;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.http.fast.On;
import org.rapidoid.job.Jobs;
import org.rapidoid.log.Log;
import org.rapidoid.plugins.Plugins;
import org.rapidoid.plugins.db.DBPlugin;
import org.rapidoid.plugins.entities.EntitiesPlugin;
import org.rapidoid.plugins.languages.LanguagesPlugin;
import org.rapidoid.plugins.lifecycle.LifecyclePlugin;
import org.rapidoid.plugins.templates.MustacheTemplatesPlugin;
import org.rapidoid.plugins.users.UsersPlugin;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.security.annotation.Admin;
import org.rapidoid.security.annotation.DevMode;
import org.rapidoid.security.annotation.HasRole;
import org.rapidoid.security.annotation.LoggedIn;
import org.rapidoid.security.annotation.Manager;
import org.rapidoid.security.annotation.Moderator;
import org.rapidoid.security.annotation.Role;
import org.rapidoid.u.U;
import org.rapidoid.util.UTILS;

@Authors("Nikolche Mihajlovski")
@Since("4.0.0")
public class Rapidoid {

	private static WebApp webApp;

	public static synchronized WebApp run(String[] args, Object... config) {
		initAndStart(null, args, config);
		return webApp;
	}

	public static synchronized WebApp run(WebApp app, String[] args, Object... config) {
		initAndStart(app, args, config);
		return app;
	}

	public static synchronized boolean isInitialized() {
		return webApp != null;
	}

	private static synchronized void initAndStart(WebApp app, String[] args, Object... config) {
		if (webApp != null) {
			return;
		}

		app = bootstrap(app, args, config);

		WebAppGroup.main().setDefaultApp(app);
		WebAppGroup.main().register(app);

		Ctx ctx = Ctxs.open("web");
		ctx.setApp(app);

		// Apps.serve(app, args, config);
		On.req(new AppHandler(On.getDefaultSetup().http(), app));

		Jobs.execute(new Runnable() {
			@Override
			public void run() {
				Log.info("The executor is ready.");
			}
		});

		Log.info("Rapidoid is ready.");

		Rapidoid.webApp = app;
	}

	@SuppressWarnings("unchecked")
	public static WebApp bootstrap(WebApp app, String[] args, Object... config) {
		Log.info("Starting Rapidoid...", "version", RapidoidInfo.version());

		ConfigHelp.processHelp(args);

		// FIXME make optional
		// print internal state
		// LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

		Conf.init(args, config);

		Log.info("Working directory is: " + System.getProperty("user.dir"));

		inferAndSetRootPackage();

		if (app == null) {
			app = AppTool.createRootApp();
		}

		registerDefaultPlugins();

		Set<String> configArgs = U.set(args);

		for (Object arg : config) {
			processArg(configArgs, arg);
		}

		String[] configArgsArr = configArgs.toArray(new String[configArgs.size()]);
		Conf.args(configArgsArr);
		Log.args(configArgsArr);

		AOP.reset();

		AOP.intercept(new AuthInterceptor(), Admin.class, Manager.class, Moderator.class, LoggedIn.class,
				DevMode.class, Role.class, HasRole.class);

		return app;
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

	private static synchronized void registerDefaultPlugins() {
		Plugins.register(new MustacheTemplatesPlugin());
		Plugins.register(new AppClasspathEntitiesPlugin());
	}

	private static void inferAndSetRootPackage() {
		Class<?> callerCls = UTILS.getCallingClassOf(Rapidoid.class);

		if (callerCls != null) {
			String rootPkg = callerCls.getPackage().getName();
			Log.info("Setting root application package: " + rootPkg);
			ClasspathUtil.setRootPackage(rootPkg);
		} else {
			Log.warn("Couldn't calculate the application root package!");
		}
	}

	public static synchronized void register(WebApp app) {
		WebAppGroup.main().register(app);
	}

	public static synchronized void unregister(WebApp app) {
		WebAppGroup.main().unregister(app);
	}

	static synchronized void notifyGuiInit() {
		initAndStart(null, new String[] { "managed=true" });
	}

}
