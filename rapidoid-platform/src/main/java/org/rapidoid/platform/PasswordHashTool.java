package org.rapidoid.platform;

/*
 * #%L
 * rapidoid-platform
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
import org.rapidoid.crypto.Crypto;
import org.rapidoid.u.U;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class PasswordHashTool extends RapidoidThing {

	public static void main(String[] args) {
		generatePasswordHash(args);
	}

	public static void generatePasswordHash(String[] args) {
		U.must(U.isEmpty(args), "Expecting no arguments!");

		char[] password = readPassword();

		U.print("\nYour salted password hash is:\n");

		String hash = Crypto.passwordHash(password);
		U.must(Crypto.passwordMatches(password, hash), "Password hash verification error!");

		U.print(hash);
		U.print("");
	}

	private static char[] readPassword() {
		char[] password = readPassword("Enter a new password: ");
		char[] password2 = readPassword("Enter the same password again: ");

		if (Arrays.equals(password, password2)) {
			return password;
		} else {
			U.print("[ERROR] The passwords don't match!\n");
			return readPassword();
		}
	}

	private static char[] readPassword(String msg) {
		Console console = System.console();

		if (console != null) {
			return console.readPassword(msg);

		} else {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			U.print(msg);
			try {
				return reader.readLine().toCharArray();
			} catch (IOException e) {
				throw U.rte(e);
			}
		}
	}

}
