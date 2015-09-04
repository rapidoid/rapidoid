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

import java.util.Map;
import java.util.Map.Entry;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.app.AppHandler;
import org.rapidoid.config.Conf;
import org.rapidoid.io.Res;
import org.rapidoid.jackson.YAML;
import org.rapidoid.log.Log;
import org.rapidoid.webapp.AppMenu;
import org.rapidoid.webapp.RootWebApp;
import org.rapidoid.webapp.WebApp;
import org.rapidoid.webapp.WebAppGroup;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class AppTool {

	public static WebApp createRootApp() {
		RootWebApp app = WebAppGroup.root();
		app.getRouter().generic(new AppHandler());

		String menufile = "menu.yaml";
		String firstMenuFile = Conf.configPath() + "/" + menufile;
		String defaultMenuFile = Conf.configPathDefault() + "/" + menufile;
		final Res menuRes = Res.from(menufile, true, firstMenuFile, defaultMenuFile).trackChanges();

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

		menuRes.getBytes(); // trigger loading

		String appfile = "app.yaml";
		String firstAppFile = Conf.configPath() + "/" + appfile;
		String defaultAppFile = Conf.configPathDefault() + "/" + appfile;
		final Res confRes = Res.from(appfile, true, firstAppFile, defaultAppFile).trackChanges();

		confRes.getChangeListeners().add(new Runnable() {
			@Override
			public void run() {
				if (confRes.exists()) {
					Map<String, Object> conf = YAML.parseMap(confRes.getContent());

					Conf.reset();
					rootApp.getConfig().clear();

					for (Entry<String, Object> e : conf.entrySet()) {
						String key = e.getKey();
						Object value = e.getValue();

						Log.info("Configuring", key, value);
						Conf.set(key, value);
						rootApp.getConfig().put(key, value);
					}
				}
			}
		});

		confRes.getBytes(); // trigger loading

		return app;
	}

}
