package org.rapidoid.log;

/*
 * #%L
 * rapidoid-log
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * @author Nikolche Mihajlovski
 * @since 2.0.0
 */
public class Log {

	public static final LogLevel LEVEL_TRACE = LogLevel.TRACE;
	public static final LogLevel LEVEL_DEBUG = LogLevel.DEBUG;
	public static final LogLevel LEVEL_AUDIT = LogLevel.AUDIT;
	public static final LogLevel LEVEL_INFO = LogLevel.INFO;
	public static final LogLevel LEVEL_WARN = LogLevel.WARN;
	public static final LogLevel LEVEL_ERROR = LogLevel.ERROR;
	public static final LogLevel LEVEL_SEVERE = LogLevel.SEVERE;

	protected static LogLevel LOG_LEVEL = LEVEL_AUDIT;

	private static Appendable LOG_OUTPUT = System.out;

	private Log() {}

	public static synchronized void args(String... args) {
		for (String arg : args) {
			if (arg.equals("debug") && getLogLevel().ordinal() > LEVEL_DEBUG.ordinal()) {
				setLogLevel(LEVEL_DEBUG);
			}
		}
	}

	public static synchronized void setLogLevel(LogLevel logLevel) {
		LOG_LEVEL = logLevel;
	}

	public static synchronized LogLevel getLogLevel() {
		return LOG_LEVEL;
	}

	private static String getCallingClass() {
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();

		for (int i = 2; i < trace.length; i++) {
			String cls = trace[i].getClassName();
			if (!cls.equals(Log.class.getCanonicalName())) {
				return cls;
			}
		}

		return Log.class.getCanonicalName();
	}

	private static void log(Appendable out, LogLevel level, String msg, String key1, Object value1, String key2,
			Object value2, String key3, Object value3, String key4, Object value4, String key5, Object value5,
			int paramsN) {
		if (isEnabled(level)) {
			try {
				synchronized (out) {
					out.append(level.name());
					out.append(" | ");
					out.append(Thread.currentThread().getName());
					out.append(" | ");
					out.append(getCallingClass());
					out.append(" | ");
					out.append(msg);

					switch (paramsN) {
					case 0:
						break;

					case 1:
						printKeyValue(out, key1, value1);
						break;

					case 2:
						printKeyValue(out, key1, value1);
						printKeyValue(out, key2, value2);
						break;

					case 3:
						printKeyValue(out, key1, value1);
						printKeyValue(out, key2, value2);
						printKeyValue(out, key3, value3);
						break;

					case 4:
						printKeyValue(out, key1, value1);
						printKeyValue(out, key2, value2);
						printKeyValue(out, key3, value3);
						printKeyValue(out, key4, value4);
						break;

					case 5:
						printKeyValue(out, key1, value1);
						printKeyValue(out, key2, value2);
						printKeyValue(out, key3, value3);
						printKeyValue(out, key4, value4);
						printKeyValue(out, key5, value5);
						break;

					default:
						throw new IllegalStateException();
					}

					out.append((char) 10);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static void printKeyValue(Appendable out, String key, Object value) throws IOException {
		out.append(" | ");
		out.append(key);
		out.append("=");
		out.append(printable(value));

		if (value instanceof Throwable) {
			Throwable err = (Throwable) value;
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			err.printStackTrace(new PrintStream(stream));
			out.append("\n");
			out.append(stream.toString());
		}
	}

	private static String printable(Object value) {
		return String.valueOf(value);
	}

	public static synchronized void setLogOutput(Appendable logOutput) {
		LOG_OUTPUT = logOutput;
	}

	private static void log(LogLevel level, String msg, String key1, Object value1, String key2, Object value2,
			String key3, Object value3, String key4, Object value4, String key5, Object value5, int paramsN) {
		log(LOG_OUTPUT, level, msg, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, paramsN);
	}

	public static boolean isEnabled(LogLevel level) {
		return level.ordinal() >= LOG_LEVEL.ordinal();
	}

	public static boolean isTraceEnabled() {
		return isEnabled(LEVEL_TRACE);
	}

	public static boolean isDebugEnabled() {
		return isEnabled(LEVEL_DEBUG);
	}

	public static boolean isAuditEnabled() {
		return isEnabled(LEVEL_AUDIT);
	}

	public static boolean isInfoEnabled() {
		return isEnabled(LEVEL_INFO);
	}

	public static boolean isWarnEnabled() {
		return isEnabled(LEVEL_WARN);
	}

	public static boolean isErrorEnabled() {
		return isEnabled(LEVEL_ERROR);
	}

	public static boolean isSevereEnabled() {
		return isEnabled(LEVEL_SEVERE);
	}

	public static void warn(String msg, Throwable error) {
		warn(msg, "error", error);
	}

	public static void error(String msg, Throwable error) {
		error(msg, "error", error);
	}

	public static void severe(String msg, Throwable error) {
		severe(msg, "error", error);
	}

	/*********************************** AUTOMATICALLY GENERATED: ****************************************/

	public static void trace(String msg) {
		log(LEVEL_TRACE, msg, null, null, null, null, null, null, null, null, null, null, 0);
	}

	public static void trace(String msg, String key, Object value) {
		log(LEVEL_TRACE, msg, key, value, null, null, null, null, null, null, null, null, 1);
	}

	public static void trace(String msg, String key1, Object value1, String key2, Object value2) {
		log(LEVEL_TRACE, msg, key1, value1, key2, value2, null, null, null, null, null, null, 2);
	}

	public static void trace(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3) {
		log(LEVEL_TRACE, msg, key1, value1, key2, value2, key3, value3, null, null, null, null, 3);
	}

	public static void trace(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3, String key4, Object value4) {
		log(LEVEL_TRACE, msg, key1, value1, key2, value2, key3, value3, key4, value4, null, null, 4);
	}

	public static void trace(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3, String key4, Object value4, String key5, Object value5) {
		log(LEVEL_TRACE, msg, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, 5);
	}

	public static void debug(String msg) {
		log(LEVEL_DEBUG, msg, null, null, null, null, null, null, null, null, null, null, 0);
	}

	public static void debug(String msg, String key, Object value) {
		log(LEVEL_DEBUG, msg, key, value, null, null, null, null, null, null, null, null, 1);
	}

	public static void debug(String msg, String key1, Object value1, String key2, Object value2) {
		log(LEVEL_DEBUG, msg, key1, value1, key2, value2, null, null, null, null, null, null, 2);
	}

	public static void debug(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3) {
		log(LEVEL_DEBUG, msg, key1, value1, key2, value2, key3, value3, null, null, null, null, 3);
	}

	public static void debug(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3, String key4, Object value4) {
		log(LEVEL_DEBUG, msg, key1, value1, key2, value2, key3, value3, key4, value4, null, null, 4);
	}

	public static void debug(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3, String key4, Object value4, String key5, Object value5) {
		log(LEVEL_DEBUG, msg, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, 5);
	}

	public static void audit(String msg) {
		log(LEVEL_AUDIT, msg, null, null, null, null, null, null, null, null, null, null, 0);
	}

	public static void audit(String msg, String key, Object value) {
		log(LEVEL_AUDIT, msg, key, value, null, null, null, null, null, null, null, null, 1);
	}

	public static void audit(String msg, String key1, Object value1, String key2, Object value2) {
		log(LEVEL_AUDIT, msg, key1, value1, key2, value2, null, null, null, null, null, null, 2);
	}

	public static void audit(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3) {
		log(LEVEL_AUDIT, msg, key1, value1, key2, value2, key3, value3, null, null, null, null, 3);
	}

	public static void audit(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3, String key4, Object value4) {
		log(LEVEL_AUDIT, msg, key1, value1, key2, value2, key3, value3, key4, value4, null, null, 4);
	}

	public static void audit(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3, String key4, Object value4, String key5, Object value5) {
		log(LEVEL_AUDIT, msg, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, 5);
	}

	public static void info(String msg) {
		log(LEVEL_INFO, msg, null, null, null, null, null, null, null, null, null, null, 0);
	}

	public static void info(String msg, String key, Object value) {
		log(LEVEL_INFO, msg, key, value, null, null, null, null, null, null, null, null, 1);
	}

	public static void info(String msg, String key1, Object value1, String key2, Object value2) {
		log(LEVEL_INFO, msg, key1, value1, key2, value2, null, null, null, null, null, null, 2);
	}

	public static void info(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3) {
		log(LEVEL_INFO, msg, key1, value1, key2, value2, key3, value3, null, null, null, null, 3);
	}

	public static void info(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3, String key4, Object value4) {
		log(LEVEL_INFO, msg, key1, value1, key2, value2, key3, value3, key4, value4, null, null, 4);
	}

	public static void info(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3, String key4, Object value4, String key5, Object value5) {
		log(LEVEL_INFO, msg, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, 5);
	}

	public static void warn(String msg) {
		log(LEVEL_WARN, msg, null, null, null, null, null, null, null, null, null, null, 0);
	}

	public static void warn(String msg, String key, Object value) {
		log(LEVEL_WARN, msg, key, value, null, null, null, null, null, null, null, null, 1);
	}

	public static void warn(String msg, String key1, Object value1, String key2, Object value2) {
		log(LEVEL_WARN, msg, key1, value1, key2, value2, null, null, null, null, null, null, 2);
	}

	public static void warn(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3) {
		log(LEVEL_WARN, msg, key1, value1, key2, value2, key3, value3, null, null, null, null, 3);
	}

	public static void warn(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3, String key4, Object value4) {
		log(LEVEL_WARN, msg, key1, value1, key2, value2, key3, value3, key4, value4, null, null, 4);
	}

	public static void warn(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3, String key4, Object value4, String key5, Object value5) {
		log(LEVEL_WARN, msg, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, 5);
	}

	public static void error(String msg) {
		log(LEVEL_ERROR, msg, null, null, null, null, null, null, null, null, null, null, 0);
	}

	public static void error(String msg, String key, Object value) {
		log(LEVEL_ERROR, msg, key, value, null, null, null, null, null, null, null, null, 1);
	}

	public static void error(String msg, String key1, Object value1, String key2, Object value2) {
		log(LEVEL_ERROR, msg, key1, value1, key2, value2, null, null, null, null, null, null, 2);
	}

	public static void error(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3) {
		log(LEVEL_ERROR, msg, key1, value1, key2, value2, key3, value3, null, null, null, null, 3);
	}

	public static void error(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3, String key4, Object value4) {
		log(LEVEL_ERROR, msg, key1, value1, key2, value2, key3, value3, key4, value4, null, null, 4);
	}

	public static void error(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3, String key4, Object value4, String key5, Object value5) {
		log(LEVEL_ERROR, msg, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, 5);
	}

	public static void severe(String msg) {
		log(LEVEL_SEVERE, msg, null, null, null, null, null, null, null, null, null, null, 0);
	}

	public static void severe(String msg, String key, Object value) {
		log(LEVEL_SEVERE, msg, key, value, null, null, null, null, null, null, null, null, 1);
	}

	public static void severe(String msg, String key1, Object value1, String key2, Object value2) {
		log(LEVEL_SEVERE, msg, key1, value1, key2, value2, null, null, null, null, null, null, 2);
	}

	public static void severe(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3) {
		log(LEVEL_SEVERE, msg, key1, value1, key2, value2, key3, value3, null, null, null, null, 3);
	}

	public static void severe(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3, String key4, Object value4) {
		log(LEVEL_SEVERE, msg, key1, value1, key2, value2, key3, value3, key4, value4, null, null, 4);
	}

	public static void severe(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3, String key4, Object value4, String key5, Object value5) {
		log(LEVEL_SEVERE, msg, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, 5);
	}

}
