package org.rapidoid.config;

/*
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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
import org.rapidoid.env.Env;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.2.0")
public class ConfigLoaderUtil extends RapidoidThing {

	public static void loadConfig(Config config, List<String> loaded) {
		String nameBase = config.getFilenameBase();

		if (U.notEmpty(nameBase)) {
			String configFilenamePattern = nameBase + ConfigUtil.YML_OR_YAML_OR_JSON;
			String configProfilePattern = nameBase + "-%s" + ConfigUtil.YML_OR_YAML_OR_JSON;

			ConfigUtil.load(Msc.path(config.getPath(), configFilenamePattern), config, loaded);

			for (String profile : Env.profiles()) {
				ConfigUtil.load(Msc.path(config.getPath(), U.frmt(configProfilePattern, profile)), config, loaded);
			}
		}
	}

	static void loadBuiltInConfig(Config config, List<String> loaded) {
		String nameBase = config.getFilenameBase();

		if (U.notEmpty(nameBase)) {

			ConfigUtil.load("built-in-config.yml", config, loaded);

			for (String profile : Env.profiles()) {
				String filename = U.frmt("built-in-config-%s.yml", profile);
				ConfigUtil.load(filename, config, loaded);
			}
		}
	}

}
