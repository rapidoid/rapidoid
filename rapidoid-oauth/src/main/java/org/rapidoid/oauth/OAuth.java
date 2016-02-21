package org.rapidoid.oauth;

/*
 * #%L
 * rapidoid-oauth
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

import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Config;
import org.rapidoid.config.ConfigEntry;
import org.rapidoid.gui.GUI;
import org.rapidoid.http.HttpUtils;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqHandler;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.http.Setup;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class OAuth {

	private static final String LOGIN_BTN = "<div class=\"row-fluid\"><div class=\"col-md-3\"><a href=\"/_%sLogin\" class=\"btn btn-default btn-block\">Login with %s</a></div></div>";

	private static OAuthStateCheck STATE_CHECK;

	public static void register(Setup setup, Config config, OAuthProvider... providers) {
		register(setup, config, new DefaultOAuthStateCheck(), providers);
	}

	public static void register(Setup setup, Config config, OAuthStateCheck stateCheck,
	                            OAuthProvider... providers) {

		if (!config.has("oauth")) {
			Log.warn("OAuth is currently not configured!");
		}

		ConfigEntry oauthDomain = config.entry("domain").byDefault(null);

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

			ConfigEntry clientId = config.entry("oauth", name, "id").byDefault("NO-CLIENT-ID-CONFIGURED");
			ConfigEntry clientSecret = config.entry("oauth", name, "secret").byDefault("NO-CLIENT-SECRET-CONFIGURED");

			setup.get(loginPath).html(new OAuthLoginHandler(provider, oauthDomain, config));
			setup.get(callbackPath).html(
					new OAuthTokenHandler(provider, oauthDomain, stateCheck, clientId, clientSecret, callbackPath));

			loginHtml.append(U.frmt(LOGIN_BTN, name, provider.getName()));
		}

		loginHtml.append("</div>");
		final String loginPage = loginHtml.toString();

		setup.get("/_oauth").html(new ReqHandler() {
			@Override
			public Object execute(Req x) throws Exception {
				return GUI.page(GUI.hardcoded(loginPage)).title("Login with OAuth");
			}
		});
	}

	public static String getLoginURL(Req req, Config appcfg, OAuthProvider provider, String oauthDomain) {

		if (!appcfg.has("oauth")) {
			Log.warn("OAuth is currently not configured!");
		}

		String name = provider.getName().toLowerCase();

		String clientId = appcfg.entry("oauth", name, "id").byDefault("NO-CLIENT-ID-CONFIGURED").get();
		String clientSecret = appcfg.entry("oauth", name, "secret").byDefault("NO-CLIENT-SECRET-CONFIGURED").get();

		String callbackPath = "/_" + name + "OauthCallback";

		boolean popup = req.param("popup", null) != null;

		String redirectUrl = oauthDomain != null ? oauthDomain + callbackPath : HttpUtils.constructUrl(req,
				callbackPath);

		String statePrefix = popup ? "P" : "N";
		String state = statePrefix + STATE_CHECK.generateState(clientSecret, req.sessionId());

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
