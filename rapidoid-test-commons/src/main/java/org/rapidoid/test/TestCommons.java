/*-
 * #%L
 * rapidoid-test-commons
 * %%
 * Copyright (C) 2014 - 2020 Nikolche Mihajlovski and contributors
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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Nikolche Mihajlovski
 * @since 2.0.0
 */
@SuppressWarnings("unchecked")
public abstract class TestCommons {

	protected static final boolean RAPIDOID_CI = "true".equalsIgnoreCase(System.getenv("RAPIDOID_CI"));

	// don't adjust tests during continuous integration
	private static final boolean ADJUST_TESTS = !RAPIDOID_CI && (TestTool.inDebugMode()
		|| "true".equalsIgnoreCase(System.getProperty("ADJUST_TESTS"))
		|| "true".equalsIgnoreCase(System.getenv("ADJUST_TESTS")));

	private static final String TEST_PROJECTIONS_PATH = "test-results";

	private volatile boolean hasError = false;

	private long waitingFrom;

	private static boolean initialized = false;

	private static final Queue<Object> valuesToShow = new ConcurrentLinkedQueue<>();

	private final TestComparator comparator = new TestComparator();

	@BeforeAll
	public static void beforeTests() {
		initialized = false;
	}

	@BeforeEach
	public final void initTest() {
		System.out.println("--------------------------------------------------------------------------------");
		String info = getTestInfo();
		System.out.println(" @" + ManagementFactory.getRuntimeMXBean().getName() + " TEST " + getClass().getCanonicalName() + info);
		System.out.println("--------------------------------------------------------------------------------");

		hasError = false;
		callIfExists("org.rapidoid.log.LogStats", "reset");
		callIfExists("org.rapidoid.util.Msc", "reset");

		String s = File.separator;
		String resultsDir = "src" + s + "test" + s + "resources" + s + TEST_PROJECTIONS_PATH + s + getTestName();

		if (!initialized && ADJUST_TESTS) {
			File testDir = new File(resultsDir);

			if (testDir.isDirectory()) {
				TestIO.delete(testDir);
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

	@AfterEach
	public void checkForErrors() {
		if (hasError) {

			Assertions.fail("Assertion error(s) occurred, probably were caught or were thrown on non-main thread!");

		} else if (getTestAnnotation(ExpectErrors.class) == null && hasErrorsLogged()) {
			Assertions.fail("Unexpected errors were logged!");
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
			Assertions.fail(msg);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void isNull(Object value) {
		try {
			Assertions.assertNull(value);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void notNull(Object value) {
		try {
			Assertions.assertNotNull(value);
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
			Assertions.assertTrue(cond);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void isFalse(boolean cond) {
		try {
			Assertions.assertFalse(cond);
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
			Assertions.assertNotEquals(unexpected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(Object actual, Object expected) {
		try {
			Assertions.assertEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(String actual, String expected) {
		try {
			Assertions.assertEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(char actual, char expected) {
		try {
			Assertions.assertEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(long actual, long expected) {
		try {
			Assertions.assertEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(double actual, double expected) {
		eq(actual, expected, 0.0000001);
	}

	protected void eq(double actual, double expected, double delta) {
		try {
			Assertions.assertEquals(expected, actual, delta);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(byte[] actual, byte[] expected) {
		try {
			Assertions.assertArrayEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(char[] actual, char[] expected) {
		try {
			Assertions.assertArrayEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(int[] actual, int[] expected) {
		try {
			Assertions.assertArrayEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(long[] actual, long[] expected) {
		try {
			Assertions.assertArrayEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(float[] actual, float[] expected, float delta) {
		try {
			Assertions.assertArrayEquals(expected, actual, delta);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(double[] actual, double[] expected, double delta) {
		try {
			Assertions.assertArrayEquals(expected, actual, delta);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(boolean[] actual, boolean[] expected) {
		try {
			Assertions.assertArrayEquals(expected, actual);
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void eq(Object[] actual, Object[] expected) {
		try {
			Assertions.assertArrayEquals(expected, actual);
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
			Assertions.fail("Expected exception!");
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected void hasType(Object instance, Class<?> expectedClass) {
		try {
			Assertions.assertEquals(expectedClass, instance.getClass());
		} catch (AssertionError e) {
			registerError(e);
			throw e;
		}
	}

	protected URL resource(String filename) {
		return getClass().getClassLoader().getResource(filename);
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
			new Thread(() -> {
				for (int j = 0; j < countPerThread && !failed.get(); j++) {
					try {
						runnable.run();

					} catch (Throwable e) {
						failed.set(true);
						e.printStackTrace();
					}
				}
				latch.countDown();
			}).start();
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

	protected <T extends Annotation> T getTestAnnotation(Class<T> ann) {
		Method testMethod = callIfExists("org.rapidoid.env.RapidoidEnv", "getTestMethod");
		T annotation = testMethod != null ? testMethod.getAnnotation(ann) : null;

		if (annotation == null) {
			annotation = getClass().getAnnotation(ann);
		}

		return annotation;
	}

	protected boolean httpResultsMatch(String actual, String expected) {
		return Objects.equals(TestComparator.platformNeutral(actual), TestComparator.platformNeutral(expected));
	}

	protected void check(String desc, String actual, String expected) {
		comparator.check(desc, actual, expected);
	}

	private TestVerifier verifier() {
		TestContext context = TestTool.getTestContext(getClass());
		return new TestVerifier(context, TEST_PROJECTIONS_PATH, ADJUST_TESTS, comparator);
	}

	protected void verify(String actual) {
		verifier().verify(actual);
	}

	protected void verifyCase(String info, String actual, String testCaseName) {
		verifier().verifyCase(info, actual, testCaseName);
	}

	protected void verify(String name, String actual) {
		verifier().verify(name, actual);
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

}
