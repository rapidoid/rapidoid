package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http
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
import org.rapidoid.net.TCPServer;

@Authors("Nikolche Mihajlovski")
@Since("4.3.0")
public class On {

	private static final ServerSetup DEFAULT_SERVER_SETUP = new ServerSetup();

	public static synchronized OnAction get(String path) {
		return DEFAULT_SERVER_SETUP.get(path);
	}

	public static synchronized OnAction post(String path) {
		return DEFAULT_SERVER_SETUP.post(path);
	}

	public static synchronized OnAction put(String path) {
		return DEFAULT_SERVER_SETUP.put(path);
	}

	public static synchronized OnAction delete(String path) {
		return DEFAULT_SERVER_SETUP.delete(path);
	}

	public static synchronized OnAction options(String path) {
		return DEFAULT_SERVER_SETUP.options(path);
	}

	public static TCPServer listen(int port) {
		return DEFAULT_SERVER_SETUP.listen(port);
	}

	public static TCPServer listen(String address, int port) {
		return DEFAULT_SERVER_SETUP.listen(address, port);
	}

}
