/*-
 * #%L
 * rapidoid-test-commons
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
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

package org.rapidoid.test;

import org.junit.After;
import org.junit.Assert;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.util.Map;
import java.util.Random;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
@SuppressWarnings("unchecked")
public abstract class BasicTestCommons {

	private static final Random RND = new Random();

	private volatile boolean hasError = false;

	@After
	public void checkForErrors() {
		if (hasError) {
			Assert.fail("Assertion error(s) occurred, probably were caught or were thrown on non-main thread!");
		}
	}

	protected void isTrue(boolean cond) {
		try {
			Assert.assertTrue(cond);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void isFalse(boolean cond) {
		try {
			Assert.assertFalse(cond);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void registerError(Throwable e) {
		hasError = true;
		e.printStackTrace();
	}

	protected void same(Object... objects) {
		for (int i = 0; i < objects.length - 1; i++) {
			isTrue(objects[i] == objects[i + 1]);
		}
	}

	protected void neq(Object actual, Object unexpected) {
		try {
			Assert.assertNotEquals(unexpected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(Object actual, Object expected) {
		try {
			Assert.assertEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(String actual, String expected) {
		try {
			Assert.assertEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(char actual, char expected) {
		try {
			Assert.assertEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(long actual, long expected) {
		try {
			Assert.assertEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(double actual, double expected) {
		eq(actual, expected, 0);
	}

	protected void eq(double actual, double expected, double delta) {
		try {
			Assert.assertEquals(expected, actual, delta);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(byte[] actual, byte[] expected) {
		try {
			Assert.assertArrayEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(char[] actual, char[] expected) {
		try {
			Assert.assertArrayEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(int[] actual, int[] expected) {
		try {
			Assert.assertArrayEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(long[] actual, long[] expected) {
		try {
			Assert.assertArrayEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(float[] actual, float[] expected, float delta) {
		try {
			Assert.assertArrayEquals(expected, actual, delta);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(double[] actual, double[] expected, double delta) {
		try {
			Assert.assertArrayEquals(expected, actual, delta);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(boolean[] actual, boolean[] expected) {
		try {
			Assert.assertArrayEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(Object[] actual, Object[] expected) {
		try {
			Assert.assertArrayEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(String actual, long expected) {
		eq(Long.parseLong(actual), expected);
	}

	protected <K, V> void eq(Map.Entry<K, V> entry, K key, V value) {
		eq(entry.getKey(), key);
		eq(entry.getValue(), value);
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

	protected <T> T rnd(T[] arr) {
		return arr[rnd(arr.length)];
	}

	protected int rnd() {
		return RND.nextInt();
	}

	protected long rndL() {
		return RND.nextLong();
	}

	protected boolean yesNo() {
		return RND.nextBoolean();
	}

}
