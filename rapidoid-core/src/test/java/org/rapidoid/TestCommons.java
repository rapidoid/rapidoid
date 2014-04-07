package org.rapidoid;

/*
 * #%L
 * rapidoid-core
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import java.io.File;
import java.net.URL;
import java.util.Random;

import org.rapidoid.util.U;
import org.testng.Assert;

public abstract class TestCommons {

	protected static final Random RND = new Random();

	protected void isTrue(boolean cond) {
		Assert.assertTrue(cond);
	}

	protected void isFalse(boolean cond) {
		Assert.assertFalse(cond);
	}

	protected void eq(String actual, String expected) {
		Assert.assertEquals(actual, expected);
	}

	protected void eq(char actual, char expected) {
		Assert.assertEquals(actual, expected);
	}

	protected void eq(long actual, long expected) {
		Assert.assertEquals(actual, expected);
	}

	protected void eq(double actual, double expected) {
		Assert.assertEquals(actual, expected);
	}

	protected char rndChar() {
		return (char) (65 + rnd(26));
	}

	protected String rndStr(int length) {
		return rndStr(length, length);
	}

	protected String rndStr(int minLength, int maxLength) {
		int len = minLength + rnd(maxLength - minLength + 1);
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < len; i++) {
			sb.append(rndChar());
		}

		return sb.toString();
	}

	protected int rnd(int n) {
		return RND.nextInt(n);
	}

	protected int rndExcept(int n, int except) {
		U.ensure(n > 1 || except != 0, "Cannot produce such number!");
		while (true) {
			int num = RND.nextInt(n);
			if (num != except) {
				return num;
			}
		}
	}

	protected <T> T rnd(T[] arr) {
		return arr[rnd(arr.length)];
	}

	protected int rnd() {
		return RND.nextInt();
	}

	protected long rndL() {
		return RND.nextLong();
	}

	protected void expectedException() {
		Assert.fail("Expected exception!");
	}

	protected boolean yesNo() {
		return RND.nextBoolean();
	}

	protected URL resource(String filename) {
		return getClass().getClassLoader().getResource(filename);
	}

	protected File resourceFile(String filename) {
		return new File(resource(filename).getFile());
	}

}
