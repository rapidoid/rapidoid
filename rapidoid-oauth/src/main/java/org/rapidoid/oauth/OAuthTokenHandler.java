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
import org.rapidoid.http.Handler;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.json.JSON;
import org.rapidoid.util.U;
import org.rapidoid.util.UserInfo;

public class OAuthTokenHandler implements Handler {

	private final OAuthProvider provider;
	private final String oauthDomain;
	private final OAuthStateCheck stateCheck;
	private final String clientId;
	private final String clientSecret;
	private final String callbackPath;

	public OAuthTokenHandler(OAuthProvider provider, String oauthDomain, OAuthStateCheck stateCheck, String clientId,
			String clientSecret, String callbackPath) {
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

		U.debug("Received OAuth code", "code", code, "state", state);

		if (code != null && state != null) {
			U.must(stateCheck.isValidState(state, clientSecret, x.sessionId()), "Invalid OAuth state!");

			String redirectUrl = oauthDomain != null ? oauthDomain + callbackPath : x.constructUrl(callbackPath);

			TokenRequestBuilder reqBuilder = OAuthClientRequest.tokenLocation(provider.getTokenEndpoint())
					.setGrantType(GrantType.AUTHORIZATION_CODE).setClientId(clientId).setClientSecret(clientSecret)
					.setRedirectURI(redirectUrl).setCode(code);

			OAuthClientRequest request = paramsInBody() ? reqBuilder.buildBodyMessage() : reqBuilder.buildBodyMessage();

			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());

			String accessToken = token(request, oAuthClient);

			String profileUrl = U.fillIn(provider.getProfileEndpoint(), "token", accessToken);

			OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(profileUrl).setAccessToken(
					accessToken).buildQueryMessage();

			OAuthResourceResponse res = oAuthClient.resource(bearerClientRequest,
					org.apache.oltu.oauth2.common.OAuth.HttpMethod.GET, OAuthResourceResponse.class);

			U.must(res.getResponseCode() == 200, "OAuth response error!");

			Map<String, Object> auth = JSON.parseMap(res.getBody());

			String firstName = (String) U.or(auth.get("firstName"),
					U.or(auth.get("first_name"), auth.get("given_name")));
			String lastName = (String) U.or(auth.get("lastName"), U.or(auth.get("last_name"), auth.get("family_name")));

			UserInfo user = new UserInfo();

			user.name = U.or((String) auth.get("name"), firstName + " " + lastName);
			user.oauthProvider = provider.getName();
			user.email = (String) U.or(auth.get("email"), auth.get("emailAddress"));
			user.username = user.email;
			user.oauthId = String.valueOf(auth.get("id"));
			user.display = user.email.substring(0, user.email.indexOf('@'));

			x.sessionSet("_user", user);
			U.must(x.user() == user);

			return x.redirect("/");
		} else {
			String error = x.param("error");
			if (error != null) {
				U.warn("OAuth error", "error", error);
				throw U.rte("OAuth error!");
			}
		}

		throw U.rte("OAuth error!");
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
