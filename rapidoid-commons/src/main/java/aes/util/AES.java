/*
 * Copyright (c) 2015 Chris Mason <chris@netnix.org>
 *
 * Permission to use, copy, modify, and distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package aes.util;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

public class AES {
	private final static int PBKDF2Iterations = 100000;

	private final static char[] b64CharMap = new char[]{
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
	};

	private static byte[] b64ByteMap = new byte[128];

	static {
		for (int i = 0; i < b64ByteMap.length; i++) {
			b64ByteMap[i] = -1;
		}
		for (int i = 0; i < 64; i++) {
			b64ByteMap[b64CharMap[i]] = (byte) i;
		}
	}

	public AES() {
	}

	private char[] encodeBase64(byte[] in) {
		int iLen = in.length;
		int oDataLen = (iLen * 4 + 2) / 3;
		int oLen = ((iLen + 2) / 3) * 4;
		char[] out = new char[oLen];
		int ip = 0;
		int op = 0;

		while (ip < iLen) {
			int i0 = in[ip++] & 0xff;
			int i1 = ip < iLen ? in[ip++] & 0xff : 0;
			int i2 = ip < iLen ? in[ip++] & 0xff : 0;
			int o0 = i0 >>> 2;
			int o1 = ((i0 & 3) << 4) | (i1 >>> 4);
			int o2 = ((i1 & 0xf) << 2) | (i2 >>> 6);
			int o3 = i2 & 0x3F;

			out[op++] = b64CharMap[o0];
			out[op++] = b64CharMap[o1];
			out[op] = op < oDataLen ? b64CharMap[o2] : '=';
			op++;
			out[op] = op < oDataLen ? b64CharMap[o3] : '=';
			op++;
		}
		return out;
	}

	private byte[] decodeBase64(char[] in) {
		int iLen = in.length;

		if (iLen % 4 != 0) {
			return null;
		}
		while (iLen > 0 && in[iLen - 1] == '=') {
			iLen--;
		}

		int oLen = (iLen * 3) / 4;
		byte[] out = new byte[oLen];
		int ip = 0;
		int op = 0;

		while (ip < iLen) {
			int i0 = in[ip++];
			int i1 = in[ip++];
			int i2 = ip < iLen ? in[ip++] : 'A';
			int i3 = ip < iLen ? in[ip++] : 'A';

			if (i0 > 127 || i1 > 127 || i2 > 127 || i3 > 127) {
				return null;
			}

			int b0 = b64ByteMap[i0];
			int b1 = b64ByteMap[i1];
			int b2 = b64ByteMap[i2];
			int b3 = b64ByteMap[i3];

			if (b0 < 0 || b1 < 0 || b2 < 0 || b3 < 0) {
				return null;
			}

			int o0 = (b0 << 2) | (b1 >>> 4);
			int o1 = ((b1 & 0xf) << 4) | (b2 >>> 2);
			int o2 = ((b2 & 3) << 6) | b3;

			out[op++] = (byte) o0;

			if (op < oLen) {
				out[op++] = (byte) o1;
			}
			if (op < oLen) {
				out[op++] = (byte) o2;
			}
		}
		return out;
	}

	private byte[] deriveKey(String p, byte[] s, int i, int l) throws Exception {
		PBEKeySpec ks = new PBEKeySpec(p.toCharArray(), s, i, l);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		return skf.generateSecret(ks).getEncoded();
	}

	public String encrypt(String s, String p) throws Exception {
		SecureRandom r = SecureRandom.getInstance("SHA1PRNG");

		byte[] esalt = new byte[20];
		r.nextBytes(esalt);
		byte[] dek = deriveKey(p, esalt, PBKDF2Iterations, 128);

		SecretKeySpec eks = new SecretKeySpec(dek, "AES");
		Cipher c = Cipher.getInstance("AES/CTR/NoPadding");
		c.init(Cipher.ENCRYPT_MODE, eks, new IvParameterSpec(new byte[16]));
		byte[] es = c.doFinal(s.getBytes(StandardCharsets.UTF_8));

		byte[] hsalt = new byte[20];
		r.nextBytes(hsalt);
		byte[] dhk = deriveKey(p, hsalt, PBKDF2Iterations, 160);

		SecretKeySpec hks = new SecretKeySpec(dhk, "HmacSHA256");
		Mac m = Mac.getInstance("HmacSHA256");
		m.init(hks);
		byte[] hmac = m.doFinal(es);

		byte[] os = new byte[40 + es.length + 32];
		System.arraycopy(esalt, 0, os, 0, 20);
		System.arraycopy(hsalt, 0, os, 20, 20);
		System.arraycopy(es, 0, os, 40, es.length);
		System.arraycopy(hmac, 0, os, 40 + es.length, 32);
		return new String(encodeBase64(os));
	}

	public String decrypt(String eos, String p) throws Exception {
		byte[] os = decodeBase64(eos.toCharArray());

		if (os.length > 72) {
			byte[] esalt = Arrays.copyOfRange(os, 0, 20);
			byte[] hsalt = Arrays.copyOfRange(os, 20, 40);
			byte[] es = Arrays.copyOfRange(os, 40, os.length - 32);
			byte[] hmac = Arrays.copyOfRange(os, os.length - 32, os.length);

			byte[] dhk = deriveKey(p, hsalt, PBKDF2Iterations, 160);

			SecretKeySpec hks = new SecretKeySpec(dhk, "HmacSHA256");
			Mac m = Mac.getInstance("HmacSHA256");
			m.init(hks);
			byte[] chmac = m.doFinal(es);

			if (MessageDigest.isEqual(hmac, chmac)) {
				byte[] dek = deriveKey(p, esalt, PBKDF2Iterations, 128);

				SecretKeySpec eks = new SecretKeySpec(dek, "AES");
				Cipher c = Cipher.getInstance("AES/CTR/NoPadding");
				c.init(Cipher.DECRYPT_MODE, eks, new IvParameterSpec(new byte[16]));
				byte[] s = c.doFinal(es);
				return new String(s, StandardCharsets.UTF_8);
			}
		}
		throw new Exception();
	}

	public String generate(String p) throws Exception {
		SecureRandom r = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[20];
		r.nextBytes(salt);
		byte[] hash = deriveKey(p, salt, PBKDF2Iterations, 160);
		byte[] os = new byte[20 + 20];
		System.arraycopy(salt, 0, os, 0, 20);
		System.arraycopy(hash, 0, os, 20, 20);
		return new String(encodeBase64(os));
	}

	public boolean authenticate(String p, String h) throws Exception {
		byte[] os = decodeBase64(h.toCharArray());

		if (os.length == 40) {
			byte[] salt = Arrays.copyOfRange(os, 0, 20);
			byte[] hash = Arrays.copyOfRange(os, 20, 40);
			byte[] phash = deriveKey(p, salt, PBKDF2Iterations, 160);
			return MessageDigest.isEqual(hash, phash);
		}
		return false;
	}
}