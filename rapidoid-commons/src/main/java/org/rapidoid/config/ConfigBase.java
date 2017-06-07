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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.env.RapidoidEnv;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.Map;
import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("5.2.0")
public class ConfigBase extends RapidoidInitializer {

	private final String defaultFilenameBase;

	private final boolean useBuiltInDefaults;

	final Map<String, Object> properties = Coll.synchronizedMap();

	final Map<String, Object> initial = Coll.synchronizedMap();

	final Set<ConfigChangeListener> configChangesListeners = Coll.synchronizedSet();

	volatile boolean initializing;

	volatile boolean initialized;

	volatile String path = "";

	volatile String filenameBase;

	public ConfigBase(String defaultFilenameBase, boolean useBuiltInDefaults) {
		this.defaultFilenameBase = defaultFilenameBase;
		this.filenameBase = defaultFilenameBase;
		this.useBuiltInDefaults = useBuiltInDefaults;
	}

	synchronized void reset() {
		this.properties.clear();
		this.initial.clear();
		this.configChangesListeners.clear();

		this.filenameBase = this.defaultFilenameBase;
		this.path = "";

		this.initialized = false;
		this.initializing = false;
	}

	synchronized void invalidate() {
		RapidoidEnv.touch();
		this.properties.clear();

		this.initialized = false;
		this.initializing = false;
	}

	String getFilenameBase() {
		return filenameBase;
	}

	synchronized boolean setFilenameBase(String filenameBase) {
		RapidoidEnv.touch();

		if (U.neq(this.filenameBase, filenameBase)) {
			if (!Msc.isSilent()) {
				Log.info("Changing configuration filename base", "!from", this.filenameBase, "!to", filenameBase);
			}

			this.filenameBase = filenameBase;
			return true;
		}

		return false;
	}

	synchronized boolean setPath(String path) {
		RapidoidEnv.touch();

		if (U.neq(this.path, path)) {
			if (!Msc.isSilent()) Log.info("Changing configuration path", "!from", this.path, "!to", path);

			this.path = path;
			return true;
		}

		return false;
	}

	String getPath() {
		return path;
	}

	public boolean useBuiltInDefaults() {
		return useBuiltInDefaults;
	}

	void setInitial(String name, Object value) {
		initial.put(name, value);
	}

	synchronized void applyInitialConfig(Config config) {
		config.update(initial);
	}

}
