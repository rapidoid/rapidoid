package org.rapidoid.main;

/*
 * #%L
 * rapidoid-main
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.log.Log;
import org.rapidoid.plugins.Plugins;
import org.rapidoid.plugins.cache.guava.GuavaCachePlugin;
import org.rapidoid.plugins.templates.MustacheTemplatesPlugin;
import org.rapidoid.quick.Quick;
import org.rapidoid.util.U;
import org.rapidoid.webapp.AppClasspathEntitiesPlugin;
import org.rapidoid.webapp.WebApp;
import org.rapidoid.webapp.WebAppGroup;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

@Authors("Nikolche Mihajlovski")
@Since("4.0.0")
public class Rapidoid {

	private static boolean initialized = false;

	public static synchronized WebApp run(String[] args, Object... config) {
		return run(null, args, config);
	}

	public static synchronized WebApp run(WebApp app, String[] args, Object... config) {
		Log.info("Starting Rapidoid...\n");
		U.must(!initialized, "Already initialized!");
		initialized = true;

		MainHelp.processHelp(args);

		// print internal state
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		StatusPrinter.print(lc);

		Conf.args(args, config);

		Log.info("Working directory is: " + System.getProperty("user.dir"));

		if (app == null) {
			app = AppTool.createRootApp();
		}

		Plugins.register(new MustacheTemplatesPlugin());
		Plugins.register(new AppClasspathEntitiesPlugin());
		Plugins.register(new GuavaCachePlugin());

		Quick.run(app, args, config);
		return app;
	}

	public static void register(WebApp app) {
		WebAppGroup.main().register(app);
	}

	public static void unregister(WebApp app) {
		WebAppGroup.main().unregister(app);
	}

}
