package org.rapidoid.plugins;

/*
 * #%L
 * rapidoid-plugins
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

import java.util.Collections;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public abstract class AbstractPlugin implements Plugin {

	private final String name;

	@SuppressWarnings("unchecked")
	private volatile Map<String, Object> config = Collections.EMPTY_MAP;

	public AbstractPlugin(String name) {
		this.name = name;
	}

	public String name() {
		return name;
	}

	@Override
	public void configure(Map<String, Object> config) {
		this.config = config;
		stop();
		start();
	}

	public Map<String, Object> config() {
		return config;
	}

	protected void start() {}

	protected void stop() {}

}
