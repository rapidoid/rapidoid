package org.rapidoid.config;

/*
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Env;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.2.0")
public class ConfigLoaderUtil extends RapidoidThing {

	public static void loadConfig(Config config, List<List<String>> detached, List<String> loaded) {
		String nameBase = config.getFilenameBase();

		if (U.notEmpty(nameBase)) {
			String configFilenamePattern = nameBase + ConfigUtil.YML_OR_YAML;
			String configProfilePattern = nameBase + "-%s" + ConfigUtil.YML_OR_YAML;

			ConfigUtil.load(Msc.path(config.getPath(), configFilenamePattern), config, loaded);

			for (String profile : Env.profiles()) {
				ConfigUtil.load(Msc.path(config.getPath(), U.frmt(configProfilePattern, profile)), config, loaded);
			}

			for (List<String> keys : detached) {
				autoRefresh(keys.isEmpty() ? config : config.sub(keys));
			}
		}
	}

	static void loadBuiltInConfig(Config config, List<String> loaded) {
		String nameBase = config.getFilenameBase();

		if (U.notEmpty(nameBase)) {

			ConfigUtil.load("default-config.yml", config, loaded);

			for (String profile : Env.profiles()) {
				String filename = U.frmt("default-config-%s.yml", profile);
				ConfigUtil.load(filename, config, loaded);
			}
		}
	}

	static void loadDefaultConfig(Config config, List<String> loaded) {
		String nameBase = config.getFilenameBase();

		if (U.notEmpty(nameBase)) {
			String name = "default-" + nameBase;

			String filename = name + ".yml";
			filename = Msc.path(config.getPath(), filename);

			ConfigUtil.load(filename, config, loaded);

			for (String profile : Env.profiles()) {

				filename = U.frmt(name + "-%s.yml", profile);
				filename = Msc.path(config.getPath(), filename);

				ConfigUtil.load(filename, config, loaded);
			}
		}
	}

	private static void autoRefresh(Config... configs) {
		for (Config config : configs) {
			List<String> keys = config.keys();
			ConfigUtil.autoRefresh(config, filename(config, keys));
		}
	}

	private static String filename(Config config, List<String> keys) {
		U.must(keys.size() < 2);
		String configName = keys.isEmpty() ? config.getFilenameBase() : keys.get(0);
		return Msc.path(config.getPath(), configName + ConfigUtil.YML_OR_YAML);
	}

}
