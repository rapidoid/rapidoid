package org.rapidoid.http;

import org.rapidoid.util.Conf;
import org.rapidoid.util.U;
import org.rapidoid.util.UserInfo;

/*
 * #%L
 * rapidoid-http
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

public class HttpBuiltins {

	public static void register(HTTPServer server) {
		server.get("/_logout", new Handler() {
			@Override
			public Object handle(HttpExchange x) {
				x.goBack(0);
				if (x.hasSession() && x.isLoggedIn()) {
					x.closeSession();
				}
				return x;
			}
		});
		server.get("/_debugLogin", new Handler() {
			@Override
			public Object handle(HttpExchange x) {
				x.accessDeniedIf(!Conf.dev());

				String username = x.param("user");
				U.must(username.matches("\\w+"));

				username += "@debug";

				UserInfo user = new UserInfo();
				user.username = username;
				user.email = username;
				user.name = U.capitalized(username);

				x.sessionSet(HttpExchangeImpl.SESSION_USER, user);
				return x.goBack(0);
			}
		});
	}
}
