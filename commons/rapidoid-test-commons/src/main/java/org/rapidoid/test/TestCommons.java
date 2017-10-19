package org.rapidoid.test;

/*
 * #%L
 * rapidoid-test-commons
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

import org.junit.*;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Nikolche Mihajlovski
 * @since 2.0.0
 */
public abstract class TestCommons {

	protected static final boolean RAPIDOID_CI = "true".equalsIgnoreCase(System.getenv("RAPIDOID_CI"));

	// don't adjust tests during continuous integration
	private static final boolean ADJUST_TESTS = !RAPIDOID_CI && (inDebugMode()
		|| "true".equalsIgnoreCase(System.getProperty("ADJUST_TESTS"))
		|| "true".equalsIgnoreCase(System.getenv("ADJUST_TESTS")));

	protected static final Random RND = new Random();

	private static final String TEST_PROJECTIONS_FOLDER = "test-results";

	private volatile boolean hasError = false;

	private long waitingFrom;

	private static boolean initialized = false;

	private static String OS = System.getProperty("os.name").toLowerCase();

	private static final Queue<Object> valuesToShow = new ConcurrentLinkedQueue<>();

	@BeforeClass
	public static void beforeTests() {
		initialized = false;
	}

	@Before
	public final void initTest() {
		System.out.println("--------------------------------------------------------------------------------");
		String info = getTestInfo();
		System.out.println(" @" + ManagementFactory.getRuntimeMXBean().getName() + " TEST " + getClass().getCanonicalName() + info);
		System.out.println("--------------------------------------------------------------------------------");

		hasError = false;
		callIfExists("org.rapidoid.log.LogStats", "reset");
		callIfExists("org.rapidoid.util.Msc", "reset");

		String s = File.separator;
		String resultsDir = "src" + s + "test" + s + "resources" + s + TEST_PROJECTIONS_FOLDER + s + getTestName();

		if (!initialized && ADJUST_TESTS) {
			File testDir = new File(resultsDir);

			if (testDir.isDirectory()) {
				delete(testDir);
			}

			initialized = true;
		}

		__clear();
	}

	private String getTestInfo() {
		String info = ADJUST_TESTS ? " [ADJUST] " : "";

		if (System.getenv("RAPIDOID_TEST_HEAVY") != null || System.getProperty("RAPIDOID_TEST_HEAVY") != null) {
			info += "[HEAVY]";
		}

		return info;
	}

	@After
	public void checkForErrors() {
		if (hasError) {
			Assert.fail("Assertion error(s) occurred, probably were caught or were thrown on non-main thread!");

		} else if (getTestAnnotation(ExpectErrors.class) == null && hasErrorsLogged()) {
			Assert.fail("Unexpected errors were logged!");
		}
	}

	private boolean hasErrorsLogged() {
		Boolean hasErr = callIfExists("org.rapidoid.log.LogStats", "hasErrors");
		return hasErr != null ? hasErr : false;
	}

	private <T> T callIfExists(String className, String methodName) {
		try {
			Class<?> logCls = Class.forName(className);
			Method hasErrors = logCls.getMethod(methodName);
			return (T) hasErrors.invoke(null);

		} catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			return null;
		}
	}

	protected void registerError(Throwable e) {
		hasError = true;
		e.printStackTrace();
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
		InputStream input = TestCommons.class.getClassLoader().getResourceAsStream(filename);

		if (input == null) {
			throw new RuntimeException("Cannot find resource: " + filename);
		}

		return readBytes(input);
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

		final AtomicBoolean failed = new AtomicBoolean();

		eq(count % threadsN, 0);
		final int countPerThread = count / threadsN;

		final CountDownLatch latch = new CountDownLatch(threadsN);

		for (int i = 1; i <= threadsN; i++) {
			new Thread() {
				public void run() {
					for (int j = 0; j < countPerThread && !failed.get(); j++) {
						try {
							runnable.run();

						} catch (Throwable e) {
							failed.set(true);
							e.printStackTrace();
						}
					}
					latch.countDown();
				}
			}.start();
		}

		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		isFalse(failed.get());
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

	protected void throwsRTE(String errMsg, Runnable code) {
		try {
			code.run();
		} catch (RuntimeException e) {
			Throwable err = rootCause(e);
			isTrue(err.getMessage().equals(errMsg));
			return;
		}
		fail(String.format("Expected RuntimeException(%s) to be thrown!", errMsg));
	}

	protected void throwsRuntimeExceptionContaining(String errMsgPart, Runnable code) {
		try {
			code.run();
		} catch (RuntimeException e) {
			Throwable err = rootCause(e);
			isTrue(err.getMessage().contains(errMsgPart));
			return;
		}
		fail(String.format("Expected RuntimeException(...%s...) to be thrown!", errMsgPart));
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

	protected String createTempDir(String name) {
		Path tmpDir = null;
		try {
			tmpDir = Files.createTempDirectory(name);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't create temporary directory!", e);
		}

		String tmpPath = tmpDir.toAbsolutePath().toString();
		tmpDir.toFile().deleteOnExit();
		return tmpPath;
	}

	protected String getTestName() {
		return getClass().getSimpleName();
	}

	protected String getTestPackageName() {
		return getClass().getPackage().getName();
	}

	protected String getTestNamespace() {
		Doc doc = getTestAnnotation(Doc.class);
		String namespace = (doc != null) ? getTestPackageName() : getTestName();
		return namespace.replace('.', '/');
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
			for (StackTraceElement el : trace) {
				System.out.println(el);
			}
			throw new RuntimeException("Cannot calculate the test name!");
		}

		return method;
	}

	protected Method getTestMethod() {
		String testMethodName = getTestMethodName();

		for (Method method : getClass().getDeclaredMethods()) {
			if (method.isAnnotationPresent(Test.class) && method.getName().equals(testMethodName)) {
				return method;
			}
		}

		throw new RuntimeException("Cannot detect the test method!");
	}

	protected <T extends Annotation> T getTestAnnotation(Class<T> ann) {
		Method testMethod = callIfExists("org.rapidoid.env.RapidoidEnv", "getTestMethod");
		T annotation = testMethod != null ? testMethod.getAnnotation(ann) : null;

		if (annotation == null) {
			annotation = getClass().getAnnotation(ann);
		}

		return annotation;
	}

	protected boolean isEq(Object a, Object b) {
		return a == null ? b == null : a.equals(b);
	}

	protected boolean httpResultsMatch(String actual, String expected) {
		return isEq(platformNeutral(actual), platformNeutral(expected));
	}

	private String platformNeutral(String httpResponse) {
		if (OS.contains("win")) {

			// remove carriage returns to make tests platform independent
			httpResponse = httpResponse.replaceAll("(\\r)", "");

			// remove the content length line to make tests pass on any platform
			httpResponse = httpResponse.replaceAll("Content-Length:([0-9\\n ]+)", "");
		}

		return httpResponse;
	}

	protected void check(String desc, String actual, String expected) {
		actual = platformNeutral(actual);
		expected = platformNeutral(expected);

		if (!isEq(actual, expected)) {
			System.out.println("FAILURE: " + desc);
		}

		eq(actual, expected);
	}

	protected void delete(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (File f : files) {
					delete(f);
				}
			}
		}

		if (!file.delete()) {
			throw new RuntimeException("Couldn't delete: " + file);
		}
	}

	protected void verify(String actual) {
		verify("result", actual);
	}

	protected void verifyCase(String info, String actual, String testCaseName) {
		String s = File.separator;
		String resname = TEST_PROJECTIONS_FOLDER + s + getTestName() + s + getTestMethodName() + s + testCaseName;
		String filename = "src" + s + "test" + s + "resources" + s + resname;

		if (ADJUST_TESTS) {
			synchronized (this) {
				File testDir = new File(filename).getParentFile();

				if (!testDir.exists()) {
					if (!testDir.mkdirs()) {
						throw new RuntimeException("Couldn't create the test result folder: " + testDir.getAbsolutePath());
					}
				}

				FileOutputStream out;
				try {
					out = new FileOutputStream(filename);
					out.write(actual.getBytes());
					out.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

		} else {
			byte[] bytes = loadRes(resname);
			String expected = bytes != null ? new String(bytes) : "";
			check(info, actual, expected);
		}
	}

	protected void verify(String name, String actual) {
		verifyCase(name, actual, name);
	}

	protected String[] path() {
		return new String[]{getClass().getPackage().getName()};
	}

	protected static <T> T __(T value) {
		valuesToShow.add(value);
		return value;
	}

	@SuppressWarnings("unchecked")
	protected static <K, V> Map<K, V> __(Map<K, V> value) {
		return (Map<K, V>) __((Object) value);
	}

	protected static Object __get() {
		return valuesToShow.poll();
	}

	private static void __clear() {
		while (__get() != null) {
		}
	}

	protected static boolean inDebugMode() {
		RuntimeMXBean runtimeMX = ManagementFactory.getRuntimeMXBean();

		for (String arg : runtimeMX.getInputArguments()) {
			if (arg.contains("jdwp")) return true;
		}

		return false;
	}

}
