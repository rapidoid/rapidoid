package org.rapidoid.log;

/*
 * #%L
 * rapidoid-u
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class Log {

	public static final LogLevel TRACE = LogLevel.TRACE;
	public static final LogLevel DEBUG = LogLevel.DEBUG;
	public static final LogLevel AUDIT = LogLevel.AUDIT;
	public static final LogLevel INFO = LogLevel.INFO;
	public static final LogLevel WARN = LogLevel.WARN;
	public static final LogLevel ERROR = LogLevel.ERROR;
	public static final LogLevel SEVERE = LogLevel.SEVERE;

	protected static LogLevel LOG_LEVEL = INFO;

	private static Appendable LOG_OUTPUT = System.out;

	private Log() {
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
			Object value2, String key3, Object value3, int paramsN) {
		if (level.ordinal() >= LOG_LEVEL.ordinal()) {
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
		out.append(pretty(value));

		if (value instanceof Throwable) {
			Throwable err = (Throwable) value;
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			err.printStackTrace(new PrintStream(stream));
			out.append("\n");
			out.append(stream.toString());
		}
	}

	private static String pretty(Object value) {
		return null;
	}

	public static synchronized void setLogOutput(Appendable logOutput) {
		LOG_OUTPUT = logOutput;
	}

	private static void log(LogLevel level, String msg, String key1, Object value1, String key2, Object value2,
			String key3, Object value3, int paramsN) {
		log(LOG_OUTPUT, level, msg, key1, value1, key2, value2, key3, value3, paramsN);
	}

	public static void trace(String msg) {
		log(TRACE, msg, null, null, null, null, null, null, 0);
	}

	public static void trace(String msg, String key, Object value) {
		log(TRACE, msg, key, value, null, null, null, null, 1);
	}

	public static void trace(String msg, String key1, Object value1, String key2, Object value2) {
		log(TRACE, msg, key1, value1, key2, value2, null, null, 2);
	}

	public static void trace(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3) {
		log(TRACE, msg, key1, value1, key2, value2, key3, value3, 3);
	}

	public static void debug(String msg) {
		log(DEBUG, msg, null, null, null, null, null, null, 0);
	}

	public static void debug(String msg, String key, Object value) {
		log(DEBUG, msg, key, value, null, null, null, null, 1);
	}

	public static void debug(String msg, String key1, Object value1, String key2, Object value2) {
		log(DEBUG, msg, key1, value1, key2, value2, null, null, 2);
	}

	public static void debug(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3) {
		log(DEBUG, msg, key1, value1, key2, value2, key3, value3, 3);
	}

	public static void audit(String msg) {
		log(AUDIT, msg, null, null, null, null, null, null, 0);
	}

	public static void audit(String msg, String key, Object value) {
		log(AUDIT, msg, key, value, null, null, null, null, 1);
	}

	public static void audit(String msg, String key1, Object value1, String key2, Object value2) {
		log(AUDIT, msg, key1, value1, key2, value2, null, null, 2);
	}

	public static void audit(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3) {
		log(AUDIT, msg, key1, value1, key2, value2, key3, value3, 3);
	}

	public static void info(String msg) {
		log(INFO, msg, null, null, null, null, null, null, 0);
	}

	public static void info(String msg, String key, Object value) {
		log(INFO, msg, key, value, null, null, null, null, 1);
	}

	public static void info(String msg, String key1, Object value1, String key2, Object value2) {
		log(INFO, msg, key1, value1, key2, value2, null, null, 2);
	}

	public static void info(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3) {
		log(INFO, msg, key1, value1, key2, value2, key3, value3, 3);
	}

	public static void warn(String msg) {
		log(WARN, msg, null, null, null, null, null, null, 0);
	}

	public static void warn(String msg, String key, Object value) {
		log(WARN, msg, key, value, null, null, null, null, 1);
	}

	public static void warn(String msg, String key1, Object value1, String key2, Object value2) {
		log(WARN, msg, key1, value1, key2, value2, null, null, 2);
	}

	public static void warn(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3) {
		log(WARN, msg, key1, value1, key2, value2, key3, value3, 3);
	}

	public static void warn(String msg, Throwable error) {
		warn(msg, "error", error);
	}

	public static void error(String msg) {
		log(ERROR, msg, null, null, null, null, null, null, 0);
	}

	public static void error(String msg, String key, Object value) {
		log(ERROR, msg, key, value, null, null, null, null, 1);
	}

	public static void error(String msg, String key1, Object value1, String key2, Object value2) {
		log(ERROR, msg, key1, value1, key2, value2, null, null, 2);
	}

	public static void error(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3) {
		log(ERROR, msg, key1, value1, key2, value2, key3, value3, 3);
	}

	public static void error(String msg, Throwable error) {
		error(msg, "error", error);
	}

	public static void error(Throwable error) {
		error("error occured!", "error", error);
	}

	public static void severe(String msg) {
		log(SEVERE, msg, null, null, null, null, null, null, 0);
	}

	public static void severe(String msg, String key, Object value) {
		log(SEVERE, msg, key, value, null, null, null, null, 1);
	}

	public static void severe(String msg, String key1, Object value1, String key2, Object value2) {
		log(SEVERE, msg, key1, value1, key2, value2, null, null, 2);
	}

	public static void severe(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3) {
		log(SEVERE, msg, key1, value1, key2, value2, key3, value3, 3);
	}

	public static void severe(String msg, Throwable error) {
		severe(msg, "error", error);
	}

}
