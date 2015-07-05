package org.rapidoidx.websocket;

/*
 * #%L
 * rapidoid-x-websocket
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.http.HTTPServer;
import org.rapidoid.http.Handler;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.log.Log;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class Demo {

	public static void main(String[] args) {
		Conf.args(args);
		Log.args("debug");

		HTTPServer server = WebSocket.serve(new WSHandler() {
			@Override
			public Object handle(WSExchange x) throws Exception {
				String msg = x.msg();
				Log.debug("Received WebSocket message", "message", msg);
				return msg;
			}
		});

		server.serve(new Handler() {
			@Override
			public Object handle(HttpExchange x) {
				return "Hi: " + x.uri();
			}
		});

		server.start();
	}

}
