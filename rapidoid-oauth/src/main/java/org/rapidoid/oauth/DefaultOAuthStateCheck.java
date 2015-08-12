package org.rapidoid.oauth;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.crypto.Crypto;
import org.rapidoid.util.Rnd;

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

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class DefaultOAuthStateCheck implements OAuthStateCheck {

	@Override
	public String generateState(String clientSecret, String sessionId) {
		if (Boolean.TRUE.equals(Conf.nested("oauth", "stateless"))) {
			return "OK";
		}

		String rnd = Rnd.rndStr(10);
		String hash = Crypto.sha512(clientSecret + rnd);
		return rnd + "_" + hash;
	}

	@Override
	public boolean isValidState(String state, String clientSecret, String sessionId) {
		if (Boolean.TRUE.equals(Conf.nested("oauth", "stateless"))) {
			return state.equals("OK");
		}

		String[] parts = state.split("_");
		if (parts.length != 2) {
			return false;
		}

		String hash = Crypto.sha512(clientSecret + parts[0]);
		return parts[1].equals(hash);
	}

}
