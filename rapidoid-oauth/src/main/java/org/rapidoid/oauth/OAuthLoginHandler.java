package org.rapidoid.oauth;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqHandler;
import org.rapidoid.value.Value;

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
public class OAuthLoginHandler extends RapidoidThing implements ReqHandler {

	private final OAuthProvider provider;
	private final Value<String> domain;

	public OAuthLoginHandler(OAuthProvider provider, Value<String> domain) {
		this.provider = provider;
		this.domain = domain;
	}

	@Override
	public Object execute(Req x) throws Exception {
		String domain = this.domain.getOrNull();
		String loginURL = OAuth.getLoginURL(x, provider, domain);
		return x.response().redirect(loginURL);
	}

}
