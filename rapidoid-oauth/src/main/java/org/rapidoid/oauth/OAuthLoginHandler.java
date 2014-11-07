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

import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.rapidoid.http.Handler;
import org.rapidoid.http.HttpExchange;

public class OAuthLoginHandler implements Handler {

	private final OAuthProvider provider;
	private final OAuthStateCheck stateCheck;
	private final String clientId;
	private final String clientSecret;
	private final String callbackPath;

	public OAuthLoginHandler(OAuthProvider provider, OAuthStateCheck stateCheck, String clientId, String clientSecret,
			String callbackPath) {
		this.provider = provider;
		this.stateCheck = stateCheck;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.callbackPath = callbackPath;
	}

	@Override
	public Object handle(HttpExchange x) throws Exception {
		String state = stateCheck.generateState(clientSecret, x.sessionId());

		String redirectUrl = x.constructUrl(callbackPath);

		OAuthClientRequest request = OAuthClientRequest.authorizationLocation(provider.getAuthEndpoint())
				.setClientId(clientId).setRedirectURI(redirectUrl).setScope(provider.getEmailScope()).setState(state)
				.setResponseType("code").buildQueryMessage();

		return x.redirect(request.getLocationUri());
	}

}
