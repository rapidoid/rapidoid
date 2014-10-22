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

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest.TokenRequestBuilder;
import org.apache.oltu.oauth2.client.response.GitHubTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.rapidoid.util.U;

import com.rapidoid.http.Handler;
import com.rapidoid.http.HttpExchange;

public class OAuthTokenHandler implements Handler {

	private final OAuthProvider provider;
	private final OAuthStateCheck stateCheck;
	private final String clientId;
	private final String clientSecret;
	private final String callbackPath;

	public OAuthTokenHandler(OAuthProvider provider, OAuthStateCheck stateCheck, String clientId, String clientSecret,
			String callbackPath) {
		this.provider = provider;
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

			String redirectUrl = x.constructUrl(callbackPath);

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

			return res.getBody();

		} else {
			String error = x.param("error");
			if (error != null) {
				U.warn("OAuth error", "error", error);
			}
		}

		return "";
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
