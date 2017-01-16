package org.rapidoid.log;

import org.junit.Test;
import org.rapidoid.test.TestCommons;

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

/**
 * @author Nikolche Mihajlovski
 * @since 3.0.0
 */
public class LogHPTest extends TestCommons {

	@Test
	public void testHPLoggingOverhead() {

		// force real mutability of the log level field,
		// to disable unrealistic compiler optimization
		Log.setLogLevel(LogLevel.DEBUG);
		LogHP.debug("some msg", "key1", "abccc", "key2", 2234);

		Log.setLogLevel(LogLevel.INFO);
		int total = 2000000000;

		long before = System.currentTimeMillis();

		for (int i = 0; i < total; i++) {
			LogHP.debug("some msg", "key1", "abccc", "key2", i);
		}

		long ms = System.currentTimeMillis() - before;
		long perMs = total / ms;

		System.out.println(String.format("%d logging operations in %d ms (%d ops / ms)", total, ms, perMs));

		// this is a risk for test failure on show machines, but it's useful
		isTrue(perMs > 100000);
	}

}
