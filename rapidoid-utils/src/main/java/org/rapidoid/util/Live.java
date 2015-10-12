package org.rapidoid.util;

/*
 * #%L
 * rapidoid-utils
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.config.Config;
import org.rapidoid.data.YAML;
import org.rapidoid.io.Res;
import org.rapidoid.log.Log;

@Authors("Nikolche Mihajlovski")
@Since("4.4.0")
public class Live {

	public static Config config(String path, String filename) {
		Log.info("Initializing live config", "path", path, "filename", filename);
		path = U.safe(path);

		String firstFile = U.path(Conf.rootPath(), path, filename);
		String defaultFile = U.path(Conf.rootPathDefault(), path, filename);

		final Config config = new Config();
		final Res res = Res.from(filename, true, firstFile, defaultFile);

		res.onChange(path + ":" + filename, new Runnable() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				Map<String, Object> configData = U.map();

				if (res.exists()) {
					byte[] bytes = res.getBytesOrNull();

					if (bytes != null && bytes.length > 0) {
						configData = YAML.parse(bytes, Map.class);
					}
				}

				config.assign(configData);
			}
		});

		res.trackChanges();
		res.exists(); // trigger loading

		return config;
	}
}
