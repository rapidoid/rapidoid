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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Config;
import org.rapidoid.config.ConfigEntry;
import org.rapidoid.http.Req;
import org.rapidoid.http.fast.ReqHandler;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class OAuthLoginHandler implements ReqHandler {

	private final OAuthProvider provider;
	private final ConfigEntry oauthDomain;
	private final Config config;

	public OAuthLoginHandler(OAuthProvider provider, ConfigEntry oauthDomain, Config config) {
		this.provider = provider;
		this.oauthDomain = oauthDomain;
		this.config = config;
	}

	@Override
	public Object handle(Req x) throws Exception {
		String domain = oauthDomain.get();
		return x.response().redirect(OAuth.getLoginURL(x, config, provider, domain));
	}

}
