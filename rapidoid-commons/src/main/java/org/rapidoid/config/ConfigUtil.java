package org.rapidoid.config;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Str;
import org.rapidoid.data.YAML;
import org.rapidoid.io.Res;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import java.util.List;
import java.util.Map;

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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class ConfigUtil extends RapidoidThing {

	private static final ConfigParser YAML_PARSER = new ConfigParser() {
		@SuppressWarnings("unchecked")
		@Override
		public Map<String, Object> parse(byte[] bytes) {
			return YAML.parse(bytes, Map.class);
		}
	};

	private static final Map<List<String>, Res> tracking = U.map();

	public static synchronized void autoRefresh(final Config config, final String yamlFilename) {
		autoRefresh(config, yamlFilename, YAML_PARSER);
	}

	public static synchronized void autoRefresh(final Config config, final String filename, final ConfigParser parser) {
		Log.debug("Initializing auto-refreshing config", "filename", filename);

		final Res res = Res.from(filename);
		tracking.put(config.keys(), res);

		Runnable reload = new Runnable() {
			@Override
			public void run() {
				Map<String, Object> configData = null;

				byte[] bytes = res.getBytesOrNull();
				if (bytes != null) {
					if (bytes.length > 0) {
						configData = parser.parse(bytes);
					}

					Log.info("Loading (refreshing) configuration file", "filename", filename);
					config.update(U.safe(configData));
				} else {
					Log.debug("Couldn't find configuration file", "filename", filename);
				}
			}
		};

		reload.run();
		res.onChange("config", reload);

		res.trackChanges();
		res.exists(); // trigger loading
	}

	public static synchronized List<List<String>> untrack() {
		List<List<String>> keys = U.list();

		for (Map.Entry<List<String>, Res> e : tracking.entrySet()) {
			keys.add(e.getKey());
			e.getValue().removeChangeListener("config");
		}

		tracking.clear();
		return keys;
	}

	public static synchronized void load(String filename, Config config) {
		byte[] bytes = tryToLoad(filename, config);

		if (bytes != null) {
			if (bytes.length > 0) {
				Map<String, Object> configData = YAML_PARSER.parse(bytes);
				Log.debug("Loading configuration file", "filename", filename);
				config.update(U.safe(configData));
			}
		} else {
			Log.debug("Couldn't find configuration file", "filename", filename);
		}

	}

	private static byte[] tryToLoad(String filename, Config config) {
		if (filename.endsWith(".y?ml")) {
			// flexible extension: YML or YAML
			String basename = Str.trimr(filename, ".y?ml");

			byte[] bytes = Res.from(basename + ".yaml").getBytesOrNull();

			if (bytes != null) {
				return bytes;
			} else {
				return Res.from(basename + ".yml").getBytesOrNull();
			}

		} else {
			return Res.from(filename).getBytesOrNull();
		}
	}

}
