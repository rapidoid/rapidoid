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

import org.junit.jupiter.api.Test;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author Nikolche Mihajlovski
 * @since 6.0.0
 */
public class TestTool {

	protected static boolean inDebugMode() {
		RuntimeMXBean runtimeMX = ManagementFactory.getRuntimeMXBean();

		for (String arg : runtimeMX.getInputArguments()) {
			if (arg.contains("jdwp")) return true;
		}

		return false;
	}

	private static String getTestMethodName(Class<?> testClass) {
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();

		String method = null;

		for (StackTraceElement trc : trace) {
			String cls = trc.getClassName();
			if (cls.equals(testClass.getName())) {
				method = trc.getMethodName();
			}
		}

		if (method == null) {
			for (StackTraceElement el : trace) {
				System.err.println(el);
			}
			throw new RuntimeException("Cannot calculate the test name!");
		}

		return method;
	}

	public static TestContext getTestContext(Class<?> testClass) {
		Class<?> cls = testClass;

		while (cls != Object.class) {
			String testMethodName = getTestMethodName(cls);

			for (Method method : cls.getDeclaredMethods()) {
				if (isTestMethod(testMethodName, method)) {
					return new TestContext(testClass, method);
				}
			}

			for (Method method : cls.getMethods()) {
				if (isTestMethod(testMethodName, method)) {
					return new TestContext(testClass, method);
				}
			}

			cls = cls.getSuperclass(); // try the super-class
		}

		throw new RuntimeException("Cannot detect the test context for class: " + testClass);
	}

	private static boolean isTestMethod(String testMethodName, Method method) {
		return !Modifier.isNative(method.getModifiers())
			&& method.getDeclaringClass() != Object.class
			&& method.getName().equals(testMethodName)
			&& (method.isAnnotationPresent(Test.class) || method.isAnnotationPresent(TestCtx.class));
	}

}
