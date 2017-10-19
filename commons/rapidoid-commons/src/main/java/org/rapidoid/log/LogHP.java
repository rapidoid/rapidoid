package org.rapidoid.log;

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

/*
 * Code generation template:

trace|Trace|TRACE
debug|Debug|DEBUG
audit|Audit|AUDIT
info|Info|INFO
warn|Warn|WARN
error|Error|ERROR
severe|Severe|SEVERE

 public static void {{1}}(String msg, String key, long value) {
 if (Log.is{{2}}Enabled()) {
 Log.{{1}}(msg, key, value);
 }
 }

 public static void {{1}}(String msg, String key, double value) {
 if (Log.is{{2}}Enabled()) {
 Log.{{1}}(msg, key, value);
 }
 }

 public static void {{1}}(String msg, String key, boolean value) {
 if (Log.is{{2}}Enabled()) {
 Log.{{1}}(msg, key, value);
 }
 }

 public static void {{1}}(String msg, String key1, Object value1, String key2, long value2) {
 if (Log.is{{2}}Enabled()) {
 Log.{{1}}(msg, key1, value1, key2, value2);
 }
 }

 public static void {{1}}(String msg, String key1, Object value1, String key2, double value2) {
 if (Log.is{{2}}Enabled()) {
 Log.{{1}}(msg, key1, value1, key2, value2);
 }
 }

 public static void {{1}}(String msg, String key1, Object value1, String key2, boolean value2) {
 if (Log.is{{2}}Enabled()) {
 Log.{{1}}(msg, key1, value1, key2, value2);
 }
 }

 public static void {{1}}(String msg, String key1, long value1, String key2, long value2) {
 if (Log.is{{2}}Enabled()) {
 Log.{{1}}(msg, key1, value1, key2, value2);
 }
 }

 public static void {{1}}(String msg, String key1, double value1, String key2, double value2) {
 if (Log.is{{2}}Enabled()) {
 Log.{{1}}(msg, key1, value1, key2, value2);
 }
 }

 public static void {{1}}(String msg, String key1, boolean value1, String key2, boolean value2) {
 if (Log.is{{2}}Enabled()) {
 Log.{{1}}(msg, key1, value1, key2, value2);
 }
 }

 public static void {{1}}(String msg, String key1, long value1, String key2, double value2) {
 if (Log.is{{2}}Enabled()) {
 Log.{{1}}(msg, key1, value1, key2, value2);
 }
 }

 public static void {{1}}(String msg, String key1, double value1, String key2, boolean value2) {
 if (Log.is{{2}}Enabled()) {
 Log.{{1}}(msg, key1, value1, key2, value2);
 }
 }

 public static void {{1}}(String msg, String key1, long value1, String key2, boolean value2) {
 if (Log.is{{2}}Enabled()) {
 Log.{{1}}(msg, key1, value1, key2, value2);
 }
 }
 */

import org.rapidoid.RapidoidThing;

/**
 * @author Nikolche Mihajlovski
 * @since 3.0.0
 */
public class LogHP extends RapidoidThing {

	private LogHP() {
	}

	public static void trace(String msg, String key, long value) {
		if (Log.isTraceEnabled()) {
			Log.trace(msg, key, value);
		}
	}

	public static void trace(String msg, String key, double value) {
		if (Log.isTraceEnabled()) {
			Log.trace(msg, key, value);
		}
	}

	public static void trace(String msg, String key, boolean value) {
		if (Log.isTraceEnabled()) {
			Log.trace(msg, key, value);
		}
	}

	public static void trace(String msg, String key1, Object value1, String key2, long value2) {
		if (Log.isTraceEnabled()) {
			Log.trace(msg, key1, value1, key2, value2);
		}
	}

	public static void trace(String msg, String key1, Object value1, String key2, double value2) {
		if (Log.isTraceEnabled()) {
			Log.trace(msg, key1, value1, key2, value2);
		}
	}

	public static void trace(String msg, String key1, Object value1, String key2, boolean value2) {
		if (Log.isTraceEnabled()) {
			Log.trace(msg, key1, value1, key2, value2);
		}
	}

	public static void trace(String msg, String key1, long value1, String key2, long value2) {
		if (Log.isTraceEnabled()) {
			Log.trace(msg, key1, value1, key2, value2);
		}
	}

	public static void trace(String msg, String key1, double value1, String key2, double value2) {
		if (Log.isTraceEnabled()) {
			Log.trace(msg, key1, value1, key2, value2);
		}
	}

	public static void trace(String msg, String key1, boolean value1, String key2, boolean value2) {
		if (Log.isTraceEnabled()) {
			Log.trace(msg, key1, value1, key2, value2);
		}
	}

	public static void trace(String msg, String key1, long value1, String key2, double value2) {
		if (Log.isTraceEnabled()) {
			Log.trace(msg, key1, value1, key2, value2);
		}
	}

	public static void trace(String msg, String key1, double value1, String key2, boolean value2) {
		if (Log.isTraceEnabled()) {
			Log.trace(msg, key1, value1, key2, value2);
		}
	}

	public static void trace(String msg, String key1, long value1, String key2, boolean value2) {
		if (Log.isTraceEnabled()) {
			Log.trace(msg, key1, value1, key2, value2);
		}
	}

	public static void debug(String msg, String key, long value) {
		if (Log.isDebugEnabled()) {
			Log.debug(msg, key, value);
		}
	}

	public static void debug(String msg, String key, double value) {
		if (Log.isDebugEnabled()) {
			Log.debug(msg, key, value);
		}
	}

	public static void debug(String msg, String key, boolean value) {
		if (Log.isDebugEnabled()) {
			Log.debug(msg, key, value);
		}
	}

	public static void debug(String msg, String key1, Object value1, String key2, long value2) {
		if (Log.isDebugEnabled()) {
			Log.debug(msg, key1, value1, key2, value2);
		}
	}

	public static void debug(String msg, String key1, Object value1, String key2, double value2) {
		if (Log.isDebugEnabled()) {
			Log.debug(msg, key1, value1, key2, value2);
		}
	}

	public static void debug(String msg, String key1, Object value1, String key2, boolean value2) {
		if (Log.isDebugEnabled()) {
			Log.debug(msg, key1, value1, key2, value2);
		}
	}

	public static void debug(String msg, String key1, long value1, String key2, long value2) {
		if (Log.isDebugEnabled()) {
			Log.debug(msg, key1, value1, key2, value2);
		}
	}

	public static void debug(String msg, String key1, double value1, String key2, double value2) {
		if (Log.isDebugEnabled()) {
			Log.debug(msg, key1, value1, key2, value2);
		}
	}

	public static void debug(String msg, String key1, boolean value1, String key2, boolean value2) {
		if (Log.isDebugEnabled()) {
			Log.debug(msg, key1, value1, key2, value2);
		}
	}

	public static void debug(String msg, String key1, long value1, String key2, double value2) {
		if (Log.isDebugEnabled()) {
			Log.debug(msg, key1, value1, key2, value2);
		}
	}

	public static void debug(String msg, String key1, double value1, String key2, boolean value2) {
		if (Log.isDebugEnabled()) {
			Log.debug(msg, key1, value1, key2, value2);
		}
	}

	public static void debug(String msg, String key1, long value1, String key2, boolean value2) {
		if (Log.isDebugEnabled()) {
			Log.debug(msg, key1, value1, key2, value2);
		}
	}

	public static void info(String msg, String key, long value) {
		if (Log.isInfoEnabled()) {
			Log.info(msg, key, value);
		}
	}

	public static void info(String msg, String key, double value) {
		if (Log.isInfoEnabled()) {
			Log.info(msg, key, value);
		}
	}

	public static void info(String msg, String key, boolean value) {
		if (Log.isInfoEnabled()) {
			Log.info(msg, key, value);
		}
	}

	public static void info(String msg, String key1, Object value1, String key2, long value2) {
		if (Log.isInfoEnabled()) {
			Log.info(msg, key1, value1, key2, value2);
		}
	}

	public static void info(String msg, String key1, Object value1, String key2, double value2) {
		if (Log.isInfoEnabled()) {
			Log.info(msg, key1, value1, key2, value2);
		}
	}

	public static void info(String msg, String key1, Object value1, String key2, boolean value2) {
		if (Log.isInfoEnabled()) {
			Log.info(msg, key1, value1, key2, value2);
		}
	}

	public static void info(String msg, String key1, long value1, String key2, long value2) {
		if (Log.isInfoEnabled()) {
			Log.info(msg, key1, value1, key2, value2);
		}
	}

	public static void info(String msg, String key1, double value1, String key2, double value2) {
		if (Log.isInfoEnabled()) {
			Log.info(msg, key1, value1, key2, value2);
		}
	}

	public static void info(String msg, String key1, boolean value1, String key2, boolean value2) {
		if (Log.isInfoEnabled()) {
			Log.info(msg, key1, value1, key2, value2);
		}
	}

	public static void info(String msg, String key1, long value1, String key2, double value2) {
		if (Log.isInfoEnabled()) {
			Log.info(msg, key1, value1, key2, value2);
		}
	}

	public static void info(String msg, String key1, double value1, String key2, boolean value2) {
		if (Log.isInfoEnabled()) {
			Log.info(msg, key1, value1, key2, value2);
		}
	}

	public static void info(String msg, String key1, long value1, String key2, boolean value2) {
		if (Log.isInfoEnabled()) {
			Log.info(msg, key1, value1, key2, value2);
		}
	}

	public static void warn(String msg, String key, long value) {
		if (Log.isWarnEnabled()) {
			Log.warn(msg, key, value);
		}
	}

	public static void warn(String msg, String key, double value) {
		if (Log.isWarnEnabled()) {
			Log.warn(msg, key, value);
		}
	}

	public static void warn(String msg, String key, boolean value) {
		if (Log.isWarnEnabled()) {
			Log.warn(msg, key, value);
		}
	}

	public static void warn(String msg, String key1, Object value1, String key2, long value2) {
		if (Log.isWarnEnabled()) {
			Log.warn(msg, key1, value1, key2, value2);
		}
	}

	public static void warn(String msg, String key1, Object value1, String key2, double value2) {
		if (Log.isWarnEnabled()) {
			Log.warn(msg, key1, value1, key2, value2);
		}
	}

	public static void warn(String msg, String key1, Object value1, String key2, boolean value2) {
		if (Log.isWarnEnabled()) {
			Log.warn(msg, key1, value1, key2, value2);
		}
	}

	public static void warn(String msg, String key1, long value1, String key2, long value2) {
		if (Log.isWarnEnabled()) {
			Log.warn(msg, key1, value1, key2, value2);
		}
	}

	public static void warn(String msg, String key1, double value1, String key2, double value2) {
		if (Log.isWarnEnabled()) {
			Log.warn(msg, key1, value1, key2, value2);
		}
	}

	public static void warn(String msg, String key1, boolean value1, String key2, boolean value2) {
		if (Log.isWarnEnabled()) {
			Log.warn(msg, key1, value1, key2, value2);
		}
	}

	public static void warn(String msg, String key1, long value1, String key2, double value2) {
		if (Log.isWarnEnabled()) {
			Log.warn(msg, key1, value1, key2, value2);
		}
	}

	public static void warn(String msg, String key1, double value1, String key2, boolean value2) {
		if (Log.isWarnEnabled()) {
			Log.warn(msg, key1, value1, key2, value2);
		}
	}

	public static void warn(String msg, String key1, long value1, String key2, boolean value2) {
		if (Log.isWarnEnabled()) {
			Log.warn(msg, key1, value1, key2, value2);
		}
	}

	public static void error(String msg, String key, long value) {
		if (Log.isErrorEnabled()) {
			Log.error(msg, key, value);
		}
	}

	public static void error(String msg, String key, double value) {
		if (Log.isErrorEnabled()) {
			Log.error(msg, key, value);
		}
	}

	public static void error(String msg, String key, boolean value) {
		if (Log.isErrorEnabled()) {
			Log.error(msg, key, value);
		}
	}

	public static void error(String msg, String key1, Object value1, String key2, long value2) {
		if (Log.isErrorEnabled()) {
			Log.error(msg, key1, value1, key2, value2);
		}
	}

	public static void error(String msg, String key1, Object value1, String key2, double value2) {
		if (Log.isErrorEnabled()) {
			Log.error(msg, key1, value1, key2, value2);
		}
	}

	public static void error(String msg, String key1, Object value1, String key2, boolean value2) {
		if (Log.isErrorEnabled()) {
			Log.error(msg, key1, value1, key2, value2);
		}
	}

	public static void error(String msg, String key1, long value1, String key2, long value2) {
		if (Log.isErrorEnabled()) {
			Log.error(msg, key1, value1, key2, value2);
		}
	}

	public static void error(String msg, String key1, double value1, String key2, double value2) {
		if (Log.isErrorEnabled()) {
			Log.error(msg, key1, value1, key2, value2);
		}
	}

	public static void error(String msg, String key1, boolean value1, String key2, boolean value2) {
		if (Log.isErrorEnabled()) {
			Log.error(msg, key1, value1, key2, value2);
		}
	}

	public static void error(String msg, String key1, long value1, String key2, double value2) {
		if (Log.isErrorEnabled()) {
			Log.error(msg, key1, value1, key2, value2);
		}
	}

	public static void error(String msg, String key1, double value1, String key2, boolean value2) {
		if (Log.isErrorEnabled()) {
			Log.error(msg, key1, value1, key2, value2);
		}
	}

	public static void error(String msg, String key1, long value1, String key2, boolean value2) {
		if (Log.isErrorEnabled()) {
			Log.error(msg, key1, value1, key2, value2);
		}
	}

}
