package org.rapidoid.test;

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

import org.junit.Assert;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;

@Authors("Nikolche Mihajlovski")
@Since("5.1.6")
public abstract class RapidoidTest extends RapidoidThing {

	private volatile boolean hasError;

	protected void clearErrors() {
		hasError = false;
	}

	protected void registerError(AssertionError e) {
		hasError = true;
		Log.error("Error occurred while executing test!", e);
	}

	protected boolean hasError() {
		return hasError;
	}

	protected void fail(String msg) {
		try {
			Assert.fail(msg);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void isNull(Object value) {
		try {
			Assert.assertNull(value);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void notNull(Object value) {
		try {
			Assert.assertNotNull(value);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
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

	protected void neq(Object unexpected, Object actual) {
		try {
			Assert.assertNotEquals(unexpected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(Object expected, Object actual) {
		try {
			Assert.assertEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(String expected, String actual) {
		try {
			Assert.assertEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(char expected, char actual) {
		try {
			Assert.assertEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(long expected, long actual) {
		try {
			Assert.assertEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(double expected, double actual) {
		eq(actual, expected, 0);
	}

	protected void eq(double expected, double actual, double delta) {
		try {
			Assert.assertEquals(expected, actual, delta);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(byte[] expected, byte[] actual) {
		try {
			Assert.assertArrayEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(char[] expected, char[] actual) {
		try {
			Assert.assertArrayEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(int[] expected, int[] actual) {
		try {
			Assert.assertArrayEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(long[] expected, long[] actual) {
		try {
			Assert.assertArrayEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(float[] expected, float[] actual, float delta) {
		try {
			Assert.assertArrayEquals(expected, actual, delta);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(double[] expected, double[] actual, double delta) {
		try {
			Assert.assertArrayEquals(expected, actual, delta);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(boolean[] expected, boolean[] actual) {
		try {
			Assert.assertArrayEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(Object[] expected, Object[] actual) {
		try {
			Assert.assertArrayEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void contains(String expectedSubstring, String actual) {
		notNull(actual);
		isTrue(actual.contains(expectedSubstring));
	}

}
