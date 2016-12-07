package org.rapidoid.crypto;

/*
 * #%L
 * rapidoid-commons
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

import keywhiz.hkdf.Hkdf;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class CryptoUtil extends RapidoidThing {

	private static final Hkdf HKDF = Hkdf.usingDefaults();

	private static final String HMAC_SHA_256 = "HmacSHA256";

	private static final String AES_CTR_NO_PADDING = "AES/CTR/NoPadding";

	public static final int AES_KEY_LENGTH = calcAESKeyLength();

	private static int calcAESKeyLength() {
		int maxKeyLen;
		try {
			maxKeyLen = Cipher.getMaxAllowedKeyLength("AES");
		} catch (NoSuchAlgorithmException e) {
			throw U.rte(e);
		}

		return maxKeyLen > 256 ? 256 : 128;
	}

	public static byte[] hkdf(byte[] secret, byte[] salt, int bitLength) {
		SecretKeySpec key = new SecretKeySpec(secret, HMAC_SHA_256);
		return HKDF.expand(key, salt, bitLength / 8);
	}

	public static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int length)
		throws NoSuchAlgorithmException, InvalidKeySpecException {

		PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterations, length);

		return getPBKDFInstance().generateSecret(keySpec).getEncoded();
	}

	private static SecretKeyFactory getPBKDFInstance() throws NoSuchAlgorithmException {
		try {
			return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		} catch (NoSuchAlgorithmException e) {
			return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		}
	}

}
