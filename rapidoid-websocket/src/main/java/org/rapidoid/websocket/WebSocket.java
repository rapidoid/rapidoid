package org.rapidoid.websocket;

/*
 * #%L
 * rapidoid-websocket
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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
import org.rapidoid.http.HTTP;
import org.rapidoid.http.HTTPServer;
import org.rapidoid.websocket.impl.WebSocketProtocol;
import org.rapidoid.websocket.impl.WebSocketUpgrade;

@Authors("Nikolche Mihajlovski")
@Since("2.3.0")
public class WebSocket {

	public static void serve(HTTPServer server, WSHandler handler) {
		server.addUpgrade("WebSocket", new WebSocketUpgrade(), new WebSocketProtocol(handler));
	}

	public static HTTPServer serve(WSHandler handler) {
		HTTPServer server = HTTP.server().build();
		serve(server, handler);
		return server;
	}

}
