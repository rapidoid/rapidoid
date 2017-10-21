package org.rapidoid.log;

import org.junit.Test;
import org.rapidoid.test.TestCommons;

/*
 * #%L
 * rapidoid-essentials
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
public class LogTest extends TestCommons {

	@Test
	public void testLogLevel() {
		Log.setLogLevel(LogLevel.TRACE);

		isTrue(Log.isTraceEnabled());
		isTrue(Log.isDebugEnabled());
		isTrue(Log.isInfoEnabled());
		isTrue(Log.isWarnEnabled());
		isTrue(Log.isErrorEnabled());
		isTrue(Log.isFatalEnabled());

		Log.setLogLevel(LogLevel.INFO);

		isFalse(Log.isTraceEnabled());
		isFalse(Log.isDebugEnabled());
		isTrue(Log.isInfoEnabled());
		isTrue(Log.isWarnEnabled());
		isTrue(Log.isErrorEnabled());
		isTrue(Log.isFatalEnabled());

		Log.setLogLevel(LogLevel.ERROR);

		isFalse(Log.isTraceEnabled());
		isFalse(Log.isDebugEnabled());
		isFalse(Log.isInfoEnabled());
		isFalse(Log.isWarnEnabled());
		isTrue(Log.isErrorEnabled());
		isTrue(Log.isFatalEnabled());

		Log.setLogLevel(LogLevel.NO_LOGS);

		isFalse(Log.isTraceEnabled());
		isFalse(Log.isDebugEnabled());
		isFalse(Log.isInfoEnabled());
		isFalse(Log.isWarnEnabled());
		isFalse(Log.isErrorEnabled());
		isFalse(Log.isFatalEnabled());
	}

}
