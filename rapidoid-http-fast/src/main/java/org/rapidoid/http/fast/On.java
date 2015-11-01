package org.rapidoid.http.fast;

/*
 * #%L
 * rapidoid-http-fast
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("4.3.0")
public class On {

	private static final ServerSetup DEFAULT_SERVER_SETUP = new ServerSetup();

	private static boolean initialized = false;

	private static ServerSetup setup() {
		if (!initialized) {
			initialize();
			initialized = true;
		}

		return DEFAULT_SERVER_SETUP;
	}

	public static ServerSetup custom() {
		return new ServerSetup();
	}

	private static void initialize() {
		DEFAULT_SERVER_SETUP.listen();
	}

	public static synchronized OnAction get(String path) {
		return setup().get(path);
	}

	public static synchronized OnAction post(String path) {
		return setup().post(path);
	}

	public static synchronized OnAction put(String path) {
		return setup().put(path);
	}

	public static synchronized OnAction delete(String path) {
		return setup().delete(path);
	}

	public static synchronized OnAction options(String path) {
		return setup().options(path);
	}

	public static synchronized OnPage page(String path) {
		return setup().page(path);
	}

	public static synchronized ServerSetup port(int port) {
		return DEFAULT_SERVER_SETUP.port(port);
	}

	public static synchronized ServerSetup address(String address) {
		return DEFAULT_SERVER_SETUP.address(address);
	}

}
