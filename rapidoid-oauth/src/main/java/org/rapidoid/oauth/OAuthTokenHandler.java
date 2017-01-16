package org.rapidoid.oauth;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest.TokenRequestBuilder;
import org.apache.oltu.oauth2.client.response.GitHubTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.ctx.UserInfo;
import org.rapidoid.data.JSON;
import org.rapidoid.http.HttpUtils;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqHandler;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.value.Value;

import java.util.Map;
import java.util.Set;

/*
 * #%L
 * rapidoid-oauth
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class OAuthTokenHandler extends RapidoidThing implements ReqHandler {

	private final OAuthProvider provider;
	private final Customization customization;
	private final Value<String> oauthDomain;
	private final OAuthStateCheck stateCheck;
	private final Value<String> clientId;
	private final Value<String> clientSecret;
	private final String callbackPath;

	public OAuthTokenHandler(OAuthProvider provider, Customization customization, Value<String> oauthDomain,
	                         OAuthStateCheck stateCheck, Value<String> clientId, Value<String> clientSecret, String callbackPath) {

		this.provider = provider;
		this.customization = customization;
		this.oauthDomain = oauthDomain;
		this.stateCheck = stateCheck;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.callbackPath = callbackPath;
	}

	@Override
	public Object execute(Req req) throws Exception {
		String code = req.param("code");
		String state = req.param("state");

		Log.debug("Received OAuth code", "code", code, "state", state);

		if (code != null && !U.isEmpty(state)) {

			String id = clientId.str().get();
			String secret = clientSecret.str().get();

			char statePrefix = state.charAt(0);
			U.must(statePrefix == 'P' || statePrefix == 'N', "Invalid OAuth state prefix!");
			state = state.substring(1);

			U.must(stateCheck.isValidState(state, secret, req.sessionId()), "Invalid OAuth state!");

			boolean popup = statePrefix == 'P';
			Log.debug("OAuth validated", "popup", popup);

			String domain = oauthDomain.getOrNull();
			String redirectUrl = U.notEmpty(domain) ? domain + callbackPath : HttpUtils.constructUrl(req, callbackPath);

			TokenRequestBuilder reqBuilder = OAuthClientRequest.tokenLocation(provider.getTokenEndpoint())
				.setGrantType(GrantType.AUTHORIZATION_CODE).setClientId(id).setClientSecret(secret)
				.setRedirectURI(redirectUrl).setCode(code);

			OAuthClientRequest request = paramsInBody() ? reqBuilder.buildBodyMessage() : reqBuilder.buildBodyMessage();

			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());

			String accessToken = token(request, oAuthClient);

			String profileUrl = Msc.fillIn(provider.getProfileEndpoint(), "token", accessToken);

			OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(profileUrl).setAccessToken(
				accessToken).buildQueryMessage();

			OAuthResourceResponse res = oAuthClient.resource(bearerClientRequest,
				org.apache.oltu.oauth2.common.OAuth.HttpMethod.GET, OAuthResourceResponse.class);

			U.must(res.getResponseCode() == 200, "OAuth response error!");

			Map<String, Object> auth = JSON.parseMap(res.getBody());

			String email = (String) U.or(auth.get("email"), auth.get("emailAddress"));
			String firstName = (String) U.or(auth.get("firstName"), U.or(auth.get("first_name"), auth.get("given_name")));
			String lastName = (String) U.or(auth.get("lastName"), U.or(auth.get("last_name"), auth.get("family_name")));
			String name = U.or((String) auth.get("name"), firstName + " " + lastName);

			String username = email;
			Set<String> roles = customization.rolesProvider().getRolesForUser(req, username);

			UserInfo user = new UserInfo(username, roles, null);
			user.name = name;
			user.email = email;
			user.oauthProvider = provider.getName();
			user.oauthId = String.valueOf(auth.get("id"));

			Ctxs.required().setUser(user);

			// user.saveTo(x.token()); // FIXME use token

			return req.response().redirect("/"); // FIXME use page stack
		} else {
			String error = req.param("error");
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
