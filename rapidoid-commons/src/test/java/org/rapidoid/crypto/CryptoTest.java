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

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.env.Env;
import org.rapidoid.commons.Str;
import org.rapidoid.test.AbstractCommonsTest;

@Authors("Nikolche Mihajlovski")
@Since("4.0.0")
public class CryptoTest extends AbstractCommonsTest {

	@Test
	public void testDigest() {
		notNull(Crypto.digest("MD5"));
		notNull(Crypto.digest("SHA-1"));
		notNull(Crypto.digest("SHA-512"));
	}

	@Test
	public void testRandomSecret() {
		notNull(Crypto.getSecretKey());
		neq(Str.toHex(Crypto.getSecretKey()), "3CDB902856D13CC88139DD290822DA99E85242F16E575E73ED7953208B88B045");
	}

	@Test
	public void testSpecifiedSecret() {
		Env.setArgs("secret=mysecret");
		String key = Str.toHex(Crypto.getSecretKey());
		isTrue(key.equals("DCE88CB5E440BC26D371D64E55DBB67832BBB44C2887BEE9C1DF17F88BC1764B") || key.equals("3CDB902856D13CC88139DD290822DA99E85242F16E575E73ED7953208B88B045"));
	}

	@Test
	public void testMD5() {
		eq(Crypto.md5("abc"), "900150983cd24fb0d6963f7d28e17f72");
		eq(Crypto.md5("x"), "9dd4e461268c8034f5c8564e155c67a6");
		eq(Crypto.md5(" "), "7215ee9c7d9dc229d2921a40e899ec5f");
		eq(Crypto.md5(""), "d41d8cd98f00b204e9800998ecf8427e");
	}

	@Test
	public void testSHA1() {
		eq(Crypto.sha1("abc"), "a9993e364706816aba3e25717850c26c9cd0d89d");
		eq(Crypto.sha1("x"), "11f6ad8ec52a2984abaafd7c3b516503785c2072");
		eq(Crypto.sha1(" "), "b858cb282617fb0956d960215c8e84d1ccf909c6");
		eq(Crypto.sha1(""), "da39a3ee5e6b4b0d3255bfef95601890afd80709");
	}

	@Test
	public void testSHA512() {
		eq(Crypto.sha512("abc"),
			"ddaf35a193617abacc417349ae20413112e6fa4e89a97ea20a9eeee64b55d39a2192992a274fc1a836ba3c23a3feebbd454d4423643ce80e2a9ac94fa54ca49f");
		eq(Crypto.sha512("x"),
			"a4abd4448c49562d828115d13a1fccea927f52b4d5459297f8b43e42da89238bc13626e43dcb38ddb082488927ec904fb42057443983e88585179d50551afe62");
		eq(Crypto.sha512(" "),
			"f90ddd77e400dfe6a3fcf479b00b1ee29e7015c5bb8cd70f5f15b4886cc339275ff553fc8a053f8ddc7324f45168cffaf81f8c3ac93996f6536eef38e5e40768");
		eq(Crypto.sha512(""),
			"cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e");
	}

	@Test
	public void testEncryptWithAppSecret() {
		for (int i = 0; i < 10000; i++) {
			String msg1 = "" + i;

			byte[] enc = Crypto.encrypt(msg1.getBytes());
			byte[] dec = Crypto.decrypt(enc);

			String msg2 = new String(dec);
			eq(msg2, msg1);
		}
	}

	@Test
	public void testEncryptWithCustomPassword() {
		byte[] key = Crypto.pbkdf2("pass".toCharArray());

		for (int i = 0; i < 10000; i++) {
			String msg1 = "" + i;

			byte[] enc = Crypto.encrypt(msg1.getBytes(), key);
			byte[] dec = Crypto.decrypt(enc, key);

			String msg2 = new String(dec);
			eq(msg2, msg1);
		}
	}

	@Test
	public void testPasswordHash() {
		for (int i = 0; i < 10; i++) {
			String pass = "" + i;
			String hash = Crypto.passwordHash(pass.toCharArray());

			isTrue(Crypto.passwordMatches(pass, hash));
			isFalse(Crypto.passwordMatches(pass + "?", hash));
		}
	}

}
