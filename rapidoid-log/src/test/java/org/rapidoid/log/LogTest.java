package org.rapidoid.log;

import org.rapidoid.test.TestCommons;
import org.testng.annotations.Test;

/*
 * #%L
 * rapidoid-log
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

/**
 * @author Nikolche Mihajlovski
 * @since 2.5.0
 */
public class LogTest extends TestCommons {

	@Test
	public void testLogLevel() {
		Log.setLogLevel(LogLevel.TRACE);

		isTrue(Log.isTraceEnabled());
		isTrue(Log.isDebugEnabled());
		isTrue(Log.isAuditEnabled());
		isTrue(Log.isInfoEnabled());
		isTrue(Log.isWarnEnabled());
		isTrue(Log.isErrorEnabled());
		isTrue(Log.isSevereEnabled());

		Log.setLogLevel(LogLevel.INFO);

		isFalse(Log.isTraceEnabled());
		isFalse(Log.isDebugEnabled());
		isFalse(Log.isAuditEnabled());
		isTrue(Log.isInfoEnabled());
		isTrue(Log.isWarnEnabled());
		isTrue(Log.isErrorEnabled());
		isTrue(Log.isSevereEnabled());

		Log.setLogLevel(LogLevel.SEVERE);

		isFalse(Log.isTraceEnabled());
		isFalse(Log.isDebugEnabled());
		isFalse(Log.isAuditEnabled());
		isFalse(Log.isInfoEnabled());
		isFalse(Log.isWarnEnabled());
		isFalse(Log.isErrorEnabled());
		isTrue(Log.isSevereEnabled());
	}

	@Test
	public void testHPLoggingOverhead() {

		// force real mutability of the log level field,
		// to disable unrealistic compiler optimization
		Log.setLogLevel(LogLevel.TRACE);
		LogHP.trace("some msg", "key1", "abccc", "key2", 2234);

		Log.setLogLevel(LogLevel.INFO);
		int total = 2000000000;

		long before = System.currentTimeMillis();

		for (int i = 0; i < total; i++) {
			LogHP.trace("some msg", "key1", "abccc", "key2", i);
		}

		long ms = System.currentTimeMillis() - before;
		long perMs = total / ms;

		System.out.println(String.format("%d logging operations in %d ms (%d ops / ms)", total, ms, perMs));

		// this is a risk for test failure on show machines, but it's useful
		isTrue(perMs > 100000);
	}

}
