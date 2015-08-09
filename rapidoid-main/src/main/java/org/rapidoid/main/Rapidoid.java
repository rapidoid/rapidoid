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

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.app.AppHandler;
import org.rapidoid.cls.Cls;
import org.rapidoid.config.Conf;
import org.rapidoid.io.Res;
import org.rapidoid.jackson.YAML;
import org.rapidoid.log.Log;
import org.rapidoid.quick.Quick;
import org.rapidoid.util.U;
import org.rapidoid.webapp.AppMenu;
import org.rapidoid.webapp.WebApp;
import org.rapidoid.webapp.WebAppGroup;

@Authors("Nikolche Mihajlovski")
@Since("4.0.0")
public class Rapidoid {

	private static boolean initialized = false;

	public static synchronized void run(String[] args, Object... config) {
		WebApp noApp = null;
		run(noApp, args, config);
	}

	public static synchronized void run(WebApp app, String[] args, Object... config) {
		Log.info("Starting Rapidoid...");
		U.must(!initialized, "Already initialized!");
		initialized = true;

		MainHelp.processHelp(args);

		Res confRes = Res.from("config/app.yaml");
		if (confRes.exists()) {
			Map<String, Object> conf = YAML.parseMap(confRes.getContent());

			for (Entry<String, Object> e : conf.entrySet()) {
				String key = e.getKey();
				Object val = e.getValue();
				String value;

				if (val instanceof Collection<?>) {
					value = U.join(",", (Collection<?>) val);
				} else if (Cls.isSimple(val)) {
					value = Cls.str(val);
				} else {
					throw U.rte("Unsupported configuration value type: %s for key: %s", val.getClass(), key);
				}

				Log.info("Configuring", key, value);
				Conf.set(key, value);
			}
		}

		if (app == null) {
			app = WebAppGroup.root();
			app.getRouter().generic(new AppHandler());

			final Res menuRes = Res.from("config/menu.yaml").trackChanges();

			final WebApp rootApp = app;
			menuRes.getChangeListeners().add(new Runnable() {
				@Override
				public void run() {
					if (menuRes.exists()) {
						Object menuData = YAML.parse(menuRes.getContent(), Object.class);
						AppMenu menu = AppMenu.from(menuData);
						rootApp.setMenu(menu);
					}
				}
			});

		}

		Quick.run(app, args, config);
	}

	public static void register(WebApp app) {
		WebAppGroup.main().register(app);
	}

	public static void unregister(WebApp app) {
		WebAppGroup.main().unregister(app);
	}

}
