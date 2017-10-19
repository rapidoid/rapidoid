package org.rapidoid.util;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Str;
import org.rapidoid.crypto.Crypto;
import org.rapidoid.serialize.Serialize;
import org.rapidoid.u.U;

import java.io.Serializable;
import java.nio.BufferOverflowException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/*
 * #%L
 * rapidoid-commons
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
@Since("5.3.0")
public class Tokens extends RapidoidThing {

	public static final String _USER = "_user";
	public static final String _SCOPE = "_scope";
	public static final String _EXPIRES = "_expires";

	public static String serialize(Map<String, ? extends Serializable> token) {
		if (U.notEmpty(token)) {
			byte[] tokenBytes = serializeToken(token);
			byte[] tokenEncrypted = Crypto.encrypt(tokenBytes);
			return Str.toWebSafeBase64(tokenEncrypted);

		} else {
			return "";
		}
	}

	private static byte[] serializeToken(Map<String, ? extends Serializable> token) {
		byte[] dest = new byte[2500];

		try {
			int size = Serialize.serialize(dest, token);
			dest = Arrays.copyOf(dest, size);

		} catch (BufferOverflowException e) {
			throw U.rte("The token is too big!");
		}

		return dest;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Serializable> deserialize(String token) {
		if (!U.isEmpty(token)) {
			byte[] decoded = Str.fromWebSafeBase64(token);
			byte[] tokenDecrypted = Crypto.decrypt(decoded);
			return tokenDecrypted != null ? (Map<String, Serializable>) Serialize.deserialize(tokenDecrypted) : null;
		} else {
			return null;
		}
	}

	public static TokenAuthData getAuth(Map<String, ? extends Serializable> token) {
		TokenAuthData data = new TokenAuthData();

		data.user = (String) token.get(_USER);
		data.scope = scope((String) token.get(_SCOPE));
		data.expires = (Long) token.get(_EXPIRES);

		if (data.expires == null || data.expires > U.time()) {
			return data;
		} else {
			return null; // expired
		}
	}

	private static Set<String> scope(String scope) {
		if (U.isEmpty(scope)) return null;

		Set<String> scopes = U.set();

		for (String sc : scope.split("\\,")) {
			String[] parts = sc.trim().split("\\:");
			String uri = parts[parts.length - 1];

			if (parts.length == 1) {
				scopes.add(uri);
			} else {
				for (int i = 0; i < parts.length - 1; i++) {
					scopes.add(parts[i] + " " + uri);
				}
			}
		}

		return scopes;
	}

}
