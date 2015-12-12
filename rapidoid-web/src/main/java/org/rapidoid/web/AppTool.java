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

import java.util.Map;
import java.util.Map.Entry;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.data.YAML;
import org.rapidoid.gui.menu.PageMenu;
import org.rapidoid.io.Res;
import org.rapidoid.log.Log;
import org.rapidoid.plugins.Plugin;
import org.rapidoid.plugins.Plugins;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class AppTool {

	public static WebApp createRootApp() {
		RootWebApp app = WebAppGroup.root();

		setupMenuConfig(app);
		setupAppConfig(app);

		return app;
	}

	private static void setupAppConfig(final WebApp rootApp) {
		String appfile = "app.yaml";
		String firstAppFile = Conf.configPath() + "/" + appfile;
		String defaultAppFile = Conf.configPathDefault() + "/" + appfile;
		final Res confRes = Res.from(appfile, true, firstAppFile, defaultAppFile).trackChanges();

		confRes.onChange("app tool", new Runnable() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				if (confRes.exists()) {
					String appYaml = confRes.getContent().trim();

					Map<String, Object> conf;
					if (!U.isEmpty(appYaml)) {
						conf = YAML.parse(appYaml, Map.class);
					} else {
						conf = U.map();
					}

					Conf.reset();
					rootApp.getConfig().clear();

					for (Entry<String, Object> e : conf.entrySet()) {
						String key = e.getKey();
						Object value = e.getValue();

						Log.info("Configuring", key, value);
						Conf.set(key, value);
						rootApp.getConfig().put(key, value);

						if (value instanceof Map<?, ?>) {
							// if there is a plugin , configure it
							Plugin plugin = Plugins.get(key);

							if (plugin != null) {
								Map<String, Object> pluginConfig = U.cast(value);
								plugin.configure(pluginConfig);
							}
						}
					}
				}
			}
		});

		confRes.exists(); // trigger loading
	}

	private static void setupMenuConfig(final WebApp app) {
		String menufile = "menu.yaml";
		String firstMenuFile = Conf.configPath() + "/" + menufile;
		String defaultMenuFile = Conf.configPathDefault() + "/" + menufile;
		final Res menuRes = Res.from(menufile, true, firstMenuFile, defaultMenuFile).trackChanges();

		menuRes.onChange("app tool", new Runnable() {
			@Override
			public void run() {
				if (menuRes.exists()) {
					Object menuData;
					String menuYaml = menuRes.getContent().trim();

					if (!U.isEmpty(menuYaml)) {
						menuData = YAML.parse(menuYaml, Object.class);
					} else {
						menuData = U.map();
					}

					PageMenu menu = PageMenu.from(menuData);
					app.setMenu(menu);
				}
			}
		});

		menuRes.exists(); // trigger loading
	}

}
