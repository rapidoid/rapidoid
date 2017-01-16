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
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class AESCypherTool extends RapidoidThing {

	private static final String AES_MODE = "AES/CBC/PKCS5Padding";

	static final int AES_KEY_LENGTH = calcAESKeyLength(); // bits

	private static int calcAESKeyLength() {
		int maxKeyLen;

		try {
			maxKeyLen = Cipher.getMaxAllowedKeyLength("AES");
		} catch (NoSuchAlgorithmException e) {
			throw U.rte(e);
		}

		return maxKeyLen > 256 ? 256 : 128;
	}

	public byte[] encrypt(byte[] input, CryptoKey key) throws Exception {

		byte[] aesIV = Crypto.randomBytes(16);
		byte[] encrypted = aes(input, key.encryptionKey, aesIV, Cipher.ENCRYPT_MODE);

		byte[] hmacSalt = Crypto.randomBytes(20);
		byte[] hmac = Crypto.hmac(Arr.merge(encrypted, aesIV), key.hmacKey, hmacSalt);

		return Arr.merge(aesIV, encrypted, hmacSalt, hmac);
	}

	public byte[] decrypt(byte[] input, CryptoKey key) throws Exception {

		U.must(input.length >= 68, "Not enough data to decrypt!");

		byte[] aesIV = new byte[16];
		byte[] encrypted = new byte[input.length - 16 - 20 - 32];

		byte[] hmacSalt = new byte[20];
		byte[] hmac = new byte[32];

		Arr.split(input, aesIV, encrypted, hmacSalt, hmac);

		if (Crypto.hmacMatches(hmac, Arr.merge(encrypted, aesIV), key.hmacKey, hmacSalt)) {
			return aes(encrypted, key.encryptionKey, aesIV, Cipher.DECRYPT_MODE);

		} else {
			Log.debug("Cannot decrypt invalid data. Has the secret changed?");
			return null;
		}
	}

	private byte[] aes(byte[] data, byte[] key, byte[] iv, int mode) throws Exception {
		Cipher cipher = Cipher.getInstance(AES_MODE);
		cipher.init(mode, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
		return cipher.doFinal(data);
	}

}
