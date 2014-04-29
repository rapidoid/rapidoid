package org.rapidoid.config;

/*
 * #%L
 * rapidoid-net
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import org.rapidoid.util.U;

public class CLIConfig implements ServerConfig {

	private final ServerConfig defaults;

	public CLIConfig(ServerConfig defaults) {
		this.defaults = defaults;
	}

	@Override
	public int buf() {
		return U.option("buf", defaults.buf());
	}

	@Override
	public int port() {
		return U.option("port", defaults.port());
	}

	@Override
	public int workers() {
		return U.option("workers", defaults.workers());
	}

	@Override
	public boolean nagle() {
		return U.hasOption("nagle");
	}

	@Override
	public boolean nostats() {
		return U.hasOption("nostats");
	}

}
