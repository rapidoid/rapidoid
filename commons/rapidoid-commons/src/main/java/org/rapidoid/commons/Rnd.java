package org.rapidoid.commons;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.util.Random;

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
@Since("2.0.0")
public class Rnd extends RapidoidThing {

	protected static final Random RND = new Random();

	public static char rndChar() {
		int rnd = rnd(36);
		return rnd < 10 ? (char) ('0' + rnd) : (char) ('a' + rnd - 10);
	}

	public static String rndStr(int length) {
		return rndStr(length, length);
	}

	public static String rndStr(int minLength, int maxLength) {
		int len = minLength + rnd(maxLength - minLength + 1);
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < len; i++) {
			sb.append(rndChar());
		}

		return sb.toString();
	}

	public static int rnd(int n) {
		return RND.nextInt(n);
	}

	public static int rndExcept(int n, int except) {
		if (n > 1 || except != 0) {
			while (true) {
				int num = RND.nextInt(n);
				if (num != except) {
					return num;
				}
			}
		} else {
			throw new RuntimeException("Cannot produce such number!");
		}
	}

	public static <T> T rnd(T[] arr) {
		return arr[rnd(arr.length)];
	}

	public static int rnd() {
		return RND.nextInt();
	}

	public static long rndL() {
		return RND.nextLong();
	}

	public static <T> T of(T... options) {
		return options[rnd(options.length)];
	}
}
