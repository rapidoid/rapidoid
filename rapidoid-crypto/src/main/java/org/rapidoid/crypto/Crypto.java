package org.rapidoid.crypto;

/*
 * #%L
 * rapidoid-crypto
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

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("3.1.0")
public class Crypto {

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

	public static byte[] md5Bytes(byte[] bytes) {
		MessageDigest md5 = digest("MD5");
		md5.update(bytes);
		return md5.digest();
	}

	public static String md5(byte[] bytes) {
		return U.bytesAsText(md5Bytes(bytes));
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
		return U.bytesAsText(sha1Bytes(bytes));
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
		return U.bytesAsText(sha512Bytes(bytes));
	}

	public static String sha512(String data) {
		return sha512(data.getBytes());
	}

	public static synchronized String secret() {
		String secret = Conf.secret();

		if (secret == null) {
			if (Conf.dev()) {
				secret = "";
			} else {
				throw U.rte("Application secret must be specified!");
			}
		}

		return secret;
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

	public static byte[] encrypt(String secret, byte[] dataToEncrypt) {
		byte[] key = md5Bytes(secret.getBytes());
		return aes(key, dataToEncrypt, true);
	}

	public static byte[] decrypt(String secret, byte[] dataToDecrypt) {
		byte[] key = md5Bytes(secret.getBytes());
		return aes(key, dataToDecrypt, false);
	}

	public static byte[] encrypt(byte[] dataToEncrypt) {
		return encrypt(secret(), dataToEncrypt);
	}

	public static byte[] decrypt(byte[] dataToDecrypt) {
		return decrypt(secret(), dataToDecrypt);
	}

}
