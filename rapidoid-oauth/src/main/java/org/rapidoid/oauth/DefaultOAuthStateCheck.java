package org.rapidoid.oauth;

import org.rapidoid.util.U;

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

public class DefaultOAuthStateCheck implements OAuthStateCheck {

	@Override
	public String generateState(String clientSecret, String sessionId) {
		if (U.hasOption("oauth-no-state")) {
			return "OK";
		}

		String rnd = U.rndStr(10);
		String hash = U.md5(clientSecret + rnd);
		return rnd + "_" + hash;
	}

	@Override
	public boolean isValidState(String state, String clientSecret, String sessionId) {
		if (U.hasOption("oauth-no-state")) {
			return state.equals("OK");
		}

		String[] parts = state.split("_");
		if (parts.length != 2) {
			return false;
		}

		String hash = U.md5(clientSecret + parts[0]);
		return parts[1].equals(hash);
	}

}
