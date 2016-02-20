package org.rapidoid.test;

/*
 * #%L
 * rapidoid-test-commons
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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.io.*;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * @author Nikolche Mihajlovski
 * @since 2.0.0
 */
public abstract class TestCommons {

	protected static final boolean ADJUST_RESULTS = true;

	protected static final Random RND = new Random();

	private volatile boolean hasError = false;

	private long waitingFrom;

	private static boolean initialized = false;

	@BeforeClass
	public static void beforeTests() {
		initialized = false;
	}

	@Before
	public void init() {
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("TEST " + getClass().getCanonicalName());
		System.out.println("--------------------------------------------------------------------------------");

		hasError = false;

		if (!initialized && ADJUST_RESULTS) {
			String s = File.separator;
			String resultsDir = "src" + s + "test" + s + "resources" + s + "results" + s + testName();
			File dir = new File(resultsDir);
			if (dir.isDirectory()) {
				System.out.println("DELETING: " + resultsDir);
				dir.delete();
			} else {
				System.out.println("NOT FOUND: " + resultsDir);
			}
		}

		initialized = true;
	}

	@After
	public void checkForErrors() {
		if (hasError) {
			Assert.fail("Assertion error(s) occured, probably were caught or were thrown on non-main thread!");
		}
	}

	protected void registerError(AssertionError e) {
		hasError = true;
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

	protected void notNullAll(Object... value) {
		for (Object object : value) {
			notNull(object);
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

	protected void eqApprox(double actual, double expected) {
		eq(actual, expected, 0.0000000001);
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

	protected <K, V> void eq(Entry<K, V> entry, K key, V value) {
		eq(entry.getKey(), key);
		eq(entry.getValue(), value);
	}

	protected void expectedException() {
		try {
			Assert.fail("Expected exception!");
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void hasType(Object instance, Class<?> expectedClass) {
		try {
			Assert.assertEquals(expectedClass, instance.getClass());
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
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

	protected URL resource(String filename) {
		return getClass().getClassLoader().getResource(filename);
	}

	protected byte[] readBytes(InputStream input) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		byte[] buffer = new byte[16 * 1024];

		try {
			int readN = 0;
			while ((readN = input.read(buffer)) != -1) {
				output.write(buffer, 0, readN);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return output.toByteArray();
	}

	protected byte[] loadRes(String filename) {
		try {
			URL res = resource(filename);
			return res != null ? readBytes(new FileInputStream(new File(res.getFile()))) : null;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	protected <T> T mock(Class<T> classToMock) {
		return Mockito.mock(classToMock);
	}

	protected <T> OngoingStubbing<T> when(T methodCall) {
		return Mockito.when(methodCall);
	}

	protected <T> void returns(T methodCall, T result) {
		Mockito.when(methodCall).thenReturn(result);
	}

	protected <T> T verify(T mock) {
		return Mockito.verify(mock);
	}

	protected void multiThreaded(int threadsN, final int count, final Runnable runnable) {

		eq(count % threadsN, 0);
		final int countPerThread = count / threadsN;

		final CountDownLatch latch = new CountDownLatch(threadsN);

		for (int i = 1; i <= threadsN; i++) {
			new Thread() {
				public void run() {
					for (int j = 0; j < countPerThread; j++) {
						runnable.run();
					}
					latch.countDown();
				}

				;
			}.start();
		}

		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	protected Throwable rootCause(Throwable e) {
		while (e.getCause() != null) {
			e = e.getCause();
		}
		return e;
	}

	protected void throwsSecurityException(Runnable code) {
		try {
			code.run();
		} catch (SecurityException e) {
			return;
		}
		fail("Expected SecurityException to be thrown!");
	}

	protected void throwsRuntimeException(Runnable code, String errMsgPart) {
		try {
			code.run();
		} catch (RuntimeException e) {
			Throwable err = rootCause(e);
			isTrue(err.getMessage().contains(errMsgPart));
			return;
		}
		fail("Expected RuntimeException to be thrown!");
	}

	protected void waiting() {
		waitingFrom = System.currentTimeMillis();
	}

	protected void timeout(int ms) {
		if (System.currentTimeMillis() - waitingFrom > ms) {
			fail("Reached waiting timeout: " + ms + " ms!");
		}
	}

	protected static long num(String num) {
		return Long.parseLong(num);
	}

	protected File createTempFile() {
		File file;
		try {
			file = File.createTempFile("temp", "" + System.nanoTime());
		} catch (IOException e) {
			throw new RuntimeException("Couldn't create temporary file!", e);
		}

		file.deleteOnExit();
		return file;
	}

	protected String testName() {
		return getClass().getSimpleName();
	}

	protected String getTestMethodName() {
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();

		String method = null;

		for (StackTraceElement trc : trace) {
			String cls = trc.getClassName();
			if (cls.equals(getClass().getName())) {
				method = trc.getMethodName();
			}
		}

		if (method == null) {
			throw new RuntimeException("Cannot calculate the test name!");
		}

		return method;
	}

	protected boolean isEq(Object a, Object b) {
		return a == null ? b == null : a.equals(b);
	}

	protected void check(String desc, String actual, String expected) {
		if (!isEq(actual, expected)) {
			System.out.println("FAILURE: " + desc);
		}

		eq(actual, expected);
	}

	protected void verifyCase(String info, String actual, String testCaseName) {
		String s = File.separator;
		String resname = "results" + s + testName() + s + getTestMethodName() + s + testCaseName;
		String filename = "src" + s + "test" + s + "resources" + s + resname;

		if (ADJUST_RESULTS) {
			File testDir = new File(filename).getParentFile();

			if (!testDir.exists()) {
				testDir.mkdirs();
			}

			FileOutputStream out = null;
			try {
				out = new FileOutputStream(filename);
				out.write(actual.getBytes());
				out.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		} else {
			byte[] bytes = loadRes(resname);
			String expected = bytes != null ? new String(bytes) : "";
			check(info, actual, expected);
		}
	}

	protected void verify(String actual) {
		verifyCase(null, actual, "result");
	}

}
