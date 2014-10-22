package org.rapidoid.oauth;

/*
 * #%L
 * rapidoid-oauth
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

import com.rapidoid.http.HTMLSnippets;
import com.rapidoid.http.HTTPServer;
import com.rapidoid.http.Handler;
import com.rapidoid.http.HttpExchange;

public class OAuth {

	private static final String LOGIN_BTN = "<div class=\"row-fluid\"><div class=\"col-md-3\"><a href=\"/%sLogin\" class=\"btn btn-default btn-block\">Login with %s</a></div></div>";

	public static void register(HTTPServer server, OAuthProvider... providers) {
		register(server, new DefaultOAuthStateCheck(), providers);
	}

	public static void register(HTTPServer server, OAuthStateCheck stateCheck, OAuthProvider... providers) {

		if (providers == null || providers.length == 0) {
			providers = OAuthProvider.PROVIDERS;
		}

		final StringBuilder loginHtml = new StringBuilder();
		loginHtml.append("<div class=\"container-fluid text-center\">");

		for (OAuthProvider provider : providers) {
			String name = provider.getName().toLowerCase();

			String loginPath = "/" + name + "Login";
			String callbackPath = "/" + name + "OauthCallback";

			String clientId = U.config(name + ".clientId");
			String clientSecret = U.config(name + ".clientSecret");

			server.get(loginPath, new OAuthLoginHandler(provider, stateCheck, clientId, clientSecret, callbackPath));
			server.get(callbackPath, new OAuthTokenHandler(provider, stateCheck, clientId, clientSecret, callbackPath));

			loginHtml.append(U.format(LOGIN_BTN, name, provider.getName()));
		}

		loginHtml.append("</div>");

		server.get("/oauthLogin", new Handler() {
			@Override
			public Object handle(HttpExchange x) throws Exception {
				return HTMLSnippets.writePage(x, "Login with OAuth provider", loginHtml.toString());
			}
		});
	}

}
