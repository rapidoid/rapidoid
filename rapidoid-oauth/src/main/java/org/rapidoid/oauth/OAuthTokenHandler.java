package org.rapidoid.oauth;

/*
 * #%L
 * rapidoid-oauth
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

import java.util.Map;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest.TokenRequestBuilder;
import org.apache.oltu.oauth2.client.response.GitHubTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.ConfigEntry;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.ctx.UserInfo;
import org.rapidoid.http.Handler;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.jackson.JSON;
import org.rapidoid.log.Log;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class OAuthTokenHandler implements Handler {

	private final OAuthProvider provider;
	private final ConfigEntry oauthDomain;
	private final OAuthStateCheck stateCheck;
	private final ConfigEntry clientId;
	private final ConfigEntry clientSecret;
	private final String callbackPath;

	public OAuthTokenHandler(OAuthProvider provider, ConfigEntry oauthDomain, OAuthStateCheck stateCheck,
			ConfigEntry clientId, ConfigEntry clientSecret, String callbackPath) {
		this.provider = provider;
		this.oauthDomain = oauthDomain;
		this.stateCheck = stateCheck;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.callbackPath = callbackPath;
	}

	@Override
	public Object handle(HttpExchange x) throws Exception {
		String code = x.param("code");
		String state = x.param("state");

		Log.debug("Received OAuth code", "code", code, "state", state);

		if (code != null && !U.isEmpty(state)) {

			String id = clientId.get();
			String secret = clientSecret.get();

			char statePrefix = state.charAt(0);
			U.must(statePrefix == 'P' || statePrefix == 'N', "Invalid OAuth state prefix!");
			state = state.substring(1);

			U.must(stateCheck.isValidState(state, secret, x.sessionId()), "Invalid OAuth state!");

			boolean popup = statePrefix == 'P';
			Log.debug("OAuth validated", "popup", popup);

			String domain = oauthDomain.get();
			String redirectUrl = domain != null ? domain + callbackPath : x.constructUrl(callbackPath);

			TokenRequestBuilder reqBuilder = OAuthClientRequest.tokenLocation(provider.getTokenEndpoint())
					.setGrantType(GrantType.AUTHORIZATION_CODE).setClientId(id).setClientSecret(secret)
					.setRedirectURI(redirectUrl).setCode(code);

			OAuthClientRequest request = paramsInBody() ? reqBuilder.buildBodyMessage() : reqBuilder.buildBodyMessage();

			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());

			String accessToken = token(request, oAuthClient);

			String profileUrl = UTILS.fillIn(provider.getProfileEndpoint(), "token", accessToken);

			OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(profileUrl).setAccessToken(
					accessToken).buildQueryMessage();

			OAuthResourceResponse res = oAuthClient.resource(bearerClientRequest,
					org.apache.oltu.oauth2.common.OAuth.HttpMethod.GET, OAuthResourceResponse.class);

			U.must(res.getResponseCode() == 200, "OAuth response error!");

			Map<String, Object> auth = JSON.parseMap(res.getBody());

			String firstName = (String) U.or(auth.get("firstName"),
					U.or(auth.get("first_name"), auth.get("given_name")));
			String lastName = (String) U.or(auth.get("lastName"), U.or(auth.get("last_name"), auth.get("family_name")));

			String name = U.or((String) auth.get("name"), firstName + " " + lastName);
			String oauthProvider = provider.getName();
			String email = (String) U.or(auth.get("email"), auth.get("emailAddress"));
			String username = email;
			String oauthId = String.valueOf(auth.get("id"));

			UserInfo user = new UserInfo(username, email, name, oauthId, oauthProvider);

			Ctxs.ctx().setUser(user);
			user.saveTo(x.cookiepack());

			return x.goBack(1);
		} else {
			String error = x.param("error");
			if (error != null) {
				Log.warn("OAuth error", "error", error);
				throw U.rte("OAuth error!");
			}
		}

		throw U.rte("Invalid OAuth request!");
	}

	private String token(OAuthClientRequest request, OAuthClient oAuthClient) throws Exception {
		String name = provider.getName();
		if (name.equalsIgnoreCase("facebook") || name.equalsIgnoreCase("github")) {
			// application/x-www-form-urlencoded
			GitHubTokenResponse oAuthResponse = oAuthClient.accessToken(request, GitHubTokenResponse.class);
			return oAuthResponse.getAccessToken();
		} else {
			// JSON encoded
			OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient.accessToken(request,
					OAuthJSONAccessTokenResponse.class);
			return oAuthResponse.getAccessToken();
		}
	}

	private boolean paramsInBody() {
		return provider.getName().equalsIgnoreCase("google");
	}

}
