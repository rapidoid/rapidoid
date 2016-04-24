package org.rapidoid.crypto;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

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

@Authors("Nikolche Mihajlovski")
@Since("4.0.0")
public class Crypto extends RapidoidThing {

	public static final SecureRandom RANDOM = new SecureRandom();

	private static volatile byte[] appSecretBytes;

	private static volatile String appSecret;

	public static void reset() {
		appSecretBytes = null;
		appSecret = null;
	}

	public static MessageDigest digest(String algorithm) {
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw U.rte("Cannot find crypto algorithm: " + algorithm);
		}
	}

	public static Cipher cipher(String transformation) {
		try {
			return Cipher.getInstance(transformation);
		} catch (NoSuchAlgorithmException e) {
			throw U.rte("Cannot find crypto algorithm: " + transformation);
		} catch (NoSuchPaddingException e) {
			throw U.rte("No such padding: " + transformation);
		}
	}

	public static String bytesAsText(byte[] bytes) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < bytes.length; i++) {
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}

	public static byte[] md5Bytes(byte[] bytes) {
		MessageDigest md5 = digest("MD5");
		md5.update(bytes);
		return md5.digest();
	}

	public static String md5(byte[] bytes) {
		return bytesAsText(md5Bytes(bytes));
	}

	public static String md5(String data) {
		return md5(data.getBytes());
	}

	public static byte[] sha1Bytes(byte[] bytes) {
		MessageDigest sha1 = digest("SHA-1");
		sha1.update(bytes);
		return sha1.digest();
	}

	public static String sha1(byte[] bytes) {
		return bytesAsText(sha1Bytes(bytes));
	}

	public static String sha1(String data) {
		return sha1(data.getBytes());
	}

	public static byte[] sha512Bytes(byte[] bytes) {
		MessageDigest sha1 = digest("SHA-512");
		sha1.update(bytes);
		return sha1.digest();
	}

	public static String sha512(byte[] bytes) {
		return bytesAsText(sha512Bytes(bytes));
	}

	public static String sha512(String data) {
		return sha512(data.getBytes());
	}

	public static synchronized String secret() {
		if (appSecret == null) {
			initSecret();
		}

		U.notNull(appSecret, "app secret");
		return appSecret;
	}

	public static synchronized byte[] secretBytes() {
		if (appSecretBytes == null) {
			initSecret();
		}

		U.notNull(appSecretBytes, "app secret bytes");
		return appSecretBytes;
	}

	private static synchronized void initSecret() {
		String secret = Conf.ROOT.entry("secret").str().getOrNull();

		if (secret == null) {
			Log.warn("Application secret was not specified, generating random secret!");

			appSecretBytes = new byte[64];
			RANDOM.nextBytes(appSecretBytes);
			appSecret = DatatypeConverter.printHexBinary(appSecretBytes);

		} else {
			appSecret = secret;
			appSecretBytes = appSecret.getBytes();
		}
	}

	public static byte[] randomBytes(int byteCount) {
		byte[] bytes = new byte[byteCount];
		RANDOM.nextBytes(bytes);
		return bytes;
	}

	public static String randomStr(int byteCount) {
		return DatatypeConverter.printHexBinary(randomBytes(byteCount));
	}

	public static byte[] aes(byte[] key, byte[] data, boolean encrypt) {
		Cipher cipher = cipher("AES");

		final SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
		try {
			cipher.init(encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, secretKey);
		} catch (InvalidKeyException e) {
			throw U.rte("Invalid key for the cypher!");
		}

		byte[] enc;
		try {
			enc = cipher.doFinal(data);
		} catch (IllegalBlockSizeException e) {
			throw U.rte("Illegal block size!");
		} catch (BadPaddingException e) {
			throw U.rte("Bad padding!");
		}

		return enc;
	}

	public static byte[] encrypt(byte[] secret, byte[] dataToEncrypt) {
		byte[] key = md5Bytes(secret);
		return aes(key, dataToEncrypt, true);
	}

	public static byte[] decrypt(byte[] secret, byte[] dataToDecrypt) {
		byte[] key = md5Bytes(secret);
		return aes(key, dataToDecrypt, false);
	}

	public static byte[] encrypt(byte[] dataToEncrypt) {
		return encrypt(secretBytes(), dataToEncrypt);
	}

	public static byte[] decrypt(byte[] dataToDecrypt) {
		return decrypt(secretBytes(), dataToDecrypt);
	}

}
