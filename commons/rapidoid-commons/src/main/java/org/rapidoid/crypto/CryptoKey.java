package org.rapidoid.crypto;

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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Arr;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class CryptoKey extends RapidoidThing {

	final byte[] encryptionKey;

	final byte[] hmacKey;

	public CryptoKey(byte[] encryptionKey, byte[] hmacKey) {
		this.encryptionKey = encryptionKey;
		this.hmacKey = hmacKey;
	}

	public static CryptoKey from(char[] password) {

		int totalSize = AESCypherTool.AES_KEY_LENGTH + Crypto.HMAC_KEY_LENGTH; // bits
		byte[] keys = Crypto.pbkdf2(password, Crypto.DEFAULT_PBKDF2_SALT, 100000, totalSize);

		byte[] encryptionKey = new byte[AESCypherTool.AES_KEY_LENGTH / 8]; // bits to bytes
		byte[] hmacKey = new byte[Crypto.HMAC_KEY_LENGTH / 8]; // bits to bytes

		Arr.split(keys, encryptionKey, hmacKey);

		return new CryptoKey(encryptionKey, hmacKey);
	}

}
