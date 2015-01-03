package org.rapidoid.oauth;

/*
 * #%L
 * rapidoid-oauth
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

import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.rapidoid.http.HTMLSnippets;
import org.rapidoid.http.HTTPServer;
import org.rapidoid.http.Handler;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.util.U;

public class OAuth {

	private static final String LOGIN_BTN = "<div class=\"row-fluid\"><div class=\"col-md-3\"><a href=\"/_%sLogin\" class=\"btn btn-default btn-block\">Login with %s</a></div></div>";

	private static OAuthStateCheck STATE_CHECK;

	public static void register(HTTPServer server, OAuthProvider... providers) {
		register(server, null, new DefaultOAuthStateCheck(), providers);
	}

	public static void register(HTTPServer server, String oauthDomain, OAuthStateCheck stateCheck,
			OAuthProvider... providers) {

		oauthDomain = U.or(oauthDomain, U.option("oauth-domain", (String) null));

		OAuth.STATE_CHECK = stateCheck;

		if (providers == null || providers.length == 0) {
			providers = OAuthProvider.PROVIDERS;
		}

		final StringBuilder loginHtml = new StringBuilder();
		loginHtml.append("<div class=\"container-fluid text-center\">");

		for (OAuthProvider provider : providers) {
			String name = provider.getName().toLowerCase();

			String loginPath = "/_" + name + "Login";
			String callbackPath = "/_" + name + "OauthCallback";

			String clientId = U.config(name + ".clientId");
			String clientSecret = U.config(name + ".clientSecret");

			server.get(loginPath, new OAuthLoginHandler(provider, oauthDomain));
			server.get(callbackPath, new OAuthTokenHandler(provider, oauthDomain, stateCheck, clientId, clientSecret,
					callbackPath));

			loginHtml.append(U.format(LOGIN_BTN, name, provider.getName()));
		}

		loginHtml.append("</div>");

		server.get("/_oauthLogin", new Handler() {
			@Override
			public Object handle(HttpExchange x) throws Exception {
				return HTMLSnippets.writePage(x, "Login with OAuth provider", loginHtml.toString());
			}
		});
	}

	public static String getLoginURL(HttpExchange x, OAuthProvider provider, String oauthDomain) {

		oauthDomain = U.or(oauthDomain, U.option("oauth-domain", (String) null));

		String name = provider.getName().toLowerCase();

		String clientId = U.config(name + ".clientId");
		String clientSecret = U.config(name + ".clientSecret");

		String callbackPath = "/_" + name + "OauthCallback";
		String redirectUrl = oauthDomain != null ? oauthDomain + callbackPath : x.constructUrl(callbackPath);

		String state = STATE_CHECK.generateState(clientSecret, x.sessionId());

		try {
			OAuthClientRequest request = OAuthClientRequest.authorizationLocation(provider.getAuthEndpoint())
					.setClientId(clientId).setRedirectURI(redirectUrl).setScope(provider.getEmailScope())
					.setState(state).setResponseType("code").buildQueryMessage();
			return request.getLocationUri();
		} catch (OAuthSystemException e) {
			throw U.rte(e);
		}
	}

}
