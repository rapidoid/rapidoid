package org.rapidoid.plugins;

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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import java.util.Collections;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public abstract class AbstractPlugin implements Plugin {

	private final String name;

	@SuppressWarnings("unchecked")
	private volatile Map<String, ?> config = Collections.EMPTY_MAP;

	private volatile boolean active = false;

	public AbstractPlugin(String name) {
		this.name = name;
	}

	public String name() {
		return name;
	}

	@Override
	public synchronized void configure(Map<String, ?> config) {
		this.config = config;
		Log.debug("Configuring plugin", "name", name, "config", config, "plugin", this, "active", active);
		restart();
	}

	@Override
	public synchronized void restart() {
		Log.debug("Restarting plugin", "name", name, "config", config, "plugin", this, "active", active);

		try {
			doRestart();
		} catch (Exception e) {
			active = false;
			Log.error("Cannot initialize/restart the plugin: " + name, e);
		}

		Log.debug("Plugin is ready", "name", name, "config", config, "plugin", this, "active", active);
		active = true;
	}

	@SuppressWarnings("unchecked")
	public synchronized Map<String, Object> config() {
		return (Map<String, Object>) config;
	}

	protected void doRestart() throws Exception {
	}

	@Override
	public synchronized boolean isActive() {
		return this.active;
	}

	@SuppressWarnings("unchecked")
	public <T> T option(String subname, T defaultValue) {
		Object value = config().get(subname);

		if (value == null) {
			Log.warn(U.frmt("The plugin configuration '%s' was not specified for the plugin '%s', using default: %s",
					subname, name, defaultValue));
			value = defaultValue;
		}

		return (T) value;
	}

}
