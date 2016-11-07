package org.rapidoid.log;

/*
 * #%L
 * rapidoid-essentials
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.event.Event;
import org.rapidoid.event.Fire;
import org.rapidoid.u.U;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.Callable;

/**
 * @author Nikolche Mihajlovski
 * @since 2.0.0
 */
public class Log extends RapidoidThing {

	public static final LogLevel LEVEL_TRACE = LogLevel.TRACE;
	public static final LogLevel LEVEL_DEBUG = LogLevel.DEBUG;
	public static final LogLevel LEVEL_INFO = LogLevel.INFO;
	public static final LogLevel LEVEL_WARN = LogLevel.WARN;
	public static final LogLevel LEVEL_ERROR = LogLevel.ERROR;

	protected static volatile LogLevel LOG_LEVEL = LEVEL_INFO;

	private static volatile Callable<Logger> loggerFactory;

	private static volatile boolean styled = System.console() != null;

	private Log() {
	}

	public static synchronized void args(String... args) {
		for (String arg : args) {
			for (LogLevel level : LogLevel.values()) {
				if (arg.equalsIgnoreCase(level.name())) {
					setLogLevel(level);
				}
			}
		}
	}

	public static synchronized void setLogLevel(LogLevel logLevel) {
		if (LOG_LEVEL != logLevel) {
			info("Changing log level", "from", LOG_LEVEL, "to", logLevel);
		}
		LOG_LEVEL = logLevel;
	}

	public static synchronized LogLevel getLogLevel() {
		return LOG_LEVEL;
	}

	public static boolean isStyled() {
		return styled;
	}

	public static void setStyled(boolean styled) {
		Log.styled = styled;
	}

	public static void debugging() {
		setLogLevel(LEVEL_DEBUG);
	}

	private static String getCallingClass() {
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();

		for (int i = 2; i < trace.length; i++) {
			String cls = trace[i].getClassName();
			if (!cls.startsWith(Log.class.getCanonicalName()) && !cls.startsWith("org.rapidoid.util.UTILS")) {
				return cls;
			}
		}

		return Log.class.getCanonicalName();
	}

	private static void formatLogMsg(Appendable out, String msg, String key1, Object value1, String key2,
	                                 Object value2, String key3, Object value3, String key4, Object value4,
	                                 String key5, Object value5, String key6, Object value6, String key7, Object value7,
	                                 int paramsN) {

		try {
			boolean bold = msg.startsWith("!");
			if (bold) {
				msg = msg.substring(1);
			}
			appendStyled(out, msg, bold);

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

				case 6:
					printKeyValue(out, key1, value1);
					printKeyValue(out, key2, value2);
					printKeyValue(out, key3, value3);
					printKeyValue(out, key4, value4);
					printKeyValue(out, key5, value5);
					printKeyValue(out, key6, value6);
					break;

				case 7:
					printKeyValue(out, key1, value1);
					printKeyValue(out, key2, value2);
					printKeyValue(out, key3, value3);
					printKeyValue(out, key4, value4);
					printKeyValue(out, key5, value5);
					printKeyValue(out, key6, value6);
					printKeyValue(out, key7, value7);
					break;

				default:
					throw new IllegalStateException();
			}
		} catch (IOException e) {
			throw new RuntimeException("Cannot render log message!", e);
		}
	}

	private static void printKeyValue(Appendable out, String key, Object value) throws IOException {
		boolean bold = key.startsWith("!");
		if (bold) {
			key = key.substring(1);
		}

		if (key.equalsIgnoreCase("password") || key.endsWith("password")) {
			if (value instanceof String) {
				if (U.notEmpty((String) value)) {
					value = "*****";
				}
			}
		}

		out.append(" | ");
		out.append(key);
		out.append(" = ");

		appendStyled(out, value, bold);

		if (value instanceof Throwable) {
			Throwable err = (Throwable) value;
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			err.printStackTrace(new PrintStream(stream));
			out.append("\n");
			out.append(stream.toString());
		}
	}

	private static void appendStyled(Appendable out, Object value, boolean bold) throws IOException {
		boolean withStyle = styled;

		if (bold && withStyle) {
			out.append("\33[1m");
		}

		out.append(printable(value));

		if (bold && withStyle) {
			out.append("\33[0m");
		}
	}

	private static String printable(Object value) {
		return U.str(value);
	}

	private static void log(LogLevel level, String msg, String key1, Object value1, String key2, Object value2,
	                        String key3, Object value3, String key4, Object value4, String key5, Object value5,
	                        String key6, Object value6, String key7, Object value7, int paramsN) {

		boolean visible = isEnabled(level);

		// fire a log event

		Event ev = level.event();

		switch (paramsN) {
			case 0:
				Fire.event(ev, "_", msg, "_visible", visible);
				break;

			case 1:
				Fire.event(ev, "_", msg, "_visible", visible, key1, value1);
				break;

			case 2:
				Fire.event(ev, "_", msg, "_visible", visible, key1, value1, key2, value2);
				break;

			case 3:
				Fire.event(ev, "_", msg, "_visible", visible, key1, value1, key2, value2, key3, value3);
				break;

			case 4:
				Fire.event(ev, "_", msg, "_visible", visible, key1, value1, key2, value2, key3, value3, key4, value4);
				break;

			case 5:
				Fire.event(ev, "_", msg, "_visible", visible, key1, value1, key2, value2, key3, value3, key4, value4,
					key5, value5);
				break;

			case 6:
				Fire.event(ev, "_", msg, "_visible", visible, key1, value1, key2, value2, key3, value3, key4, value4,
					key5, value5, key6, value6);
				break;

			case 7:
				Fire.event(ev, "_", msg, "_visible", visible, key1, value1, key2, value2, key3, value3, key4, value4,
					key5, value5, key6, value6, key7, value7);
				break;

			default:
				throw new IllegalStateException();
		}

		// process only the visible logs
		if (!visible) {
			return;
		}

		Logger logger = logger();

		if (logger == null || logger instanceof NOPLogger) {
			// no logger is available, so log to stdout
			StringBuilder sb = new StringBuilder();

			sb.append(level.name());
			sb.append(" | ");
			sb.append(Thread.currentThread().getName());
			sb.append(" | ");
			sb.append(getCallingClass());
			sb.append(" | ");

			formatLogMsg(sb, msg, key1, value1, key2, value2, key3, value3, key4, value4,
				key5, value5, key6, value6, key7, value7, paramsN);

			synchronized (System.out) {
				System.out.println(sb.toString());
			}
			return;
		}

		switch (level) {
			case TRACE:
				if (logger.isTraceEnabled()) {
					StringBuilder sb = new StringBuilder();
					formatLogMsg(sb, msg, key1, value1, key2, value2, key3, value3, key4, value4,
						key5, value5, key6, value6, key7, value7, paramsN);
					logger.trace(sb.toString());
				}
				break;

			case DEBUG:
				if (logger.isDebugEnabled()) {
					StringBuilder sb = new StringBuilder();
					formatLogMsg(sb, msg, key1, value1, key2, value2, key3, value3, key4, value4,
						key5, value5, key6, value6, key7, value7, paramsN);
					logger.debug(sb.toString());
				}
				break;

			case INFO:
				if (logger.isInfoEnabled()) {
					StringBuilder sb = new StringBuilder();
					formatLogMsg(sb, msg, key1, value1, key2, value2, key3, value3, key4, value4,
						key5, value5, key6, value6, key7, value7, paramsN);
					logger.info(sb.toString());
				}
				break;

			case WARN:
				if (logger.isWarnEnabled()) {
					StringBuilder sb = new StringBuilder();
					formatLogMsg(sb, msg, key1, value1, key2, value2, key3, value3, key4, value4,
						key5, value5, key6, value6, key7, value7, paramsN);
					logger.warn(sb.toString());
				}
				break;

			case ERROR:
				if (logger.isErrorEnabled()) {
					StringBuilder sb = new StringBuilder();
					formatLogMsg(sb, msg, key1, value1, key2, value2, key3, value3, key4, value4,
						key5, value5, key6, value6, key7, value7, paramsN);
					logger.error(sb.toString());
				}
				break;

			default:
				throw new IllegalStateException();
		}
	}

	public static Logger logger() {
		if (loggerFactory == null) {
			synchronized (Log.class) {
				if (loggerFactory == null) {
					loggerFactory = createLoggerFactory();
				}
			}
		}

		try {
			return loggerFactory.call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Callable<Logger> createLoggerFactory() {
		try {

			Class.forName("org.slf4j.LoggerFactory");
			Class.forName("org.slf4j.impl.StaticLoggerBinder");

			return createSlf4jLoggerFactory();

		} catch (ClassNotFoundException e) {

			return createNullLoggerFactory();
		}
	}

	private static Callable<Logger> createSlf4jLoggerFactory() {
		return new Callable<Logger>() {
			@Override
			public Logger call() throws Exception {
				return LoggerFactory.getLogger(getCallingClass());
			}
		};
	}

	private static Callable<Logger> createNullLoggerFactory() {
		return new Callable<Logger>() {
			@Override
			public Logger call() throws Exception {
				return null;
			}
		};
	}

	public static boolean isTraceEnabled() {
		return isEnabled(LEVEL_TRACE);
	}

	public static boolean isDebugEnabled() {
		return isEnabled(LEVEL_DEBUG);
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

	public static boolean isEnabled(LogLevel level) {
		return level.ordinal() >= LOG_LEVEL.ordinal();
	}

	/* TRACE */

	public static void trace(String msg) {
		log(LEVEL_TRACE, msg, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0);
	}

	public static void trace(String msg, String key, Object value) {
		log(LEVEL_TRACE, msg, key, value, null, null, null, null, null, null, null, null, null, null, null, null, 1);
	}

	public static void trace(String msg, String key1, Object value1, String key2, Object value2) {
		log(LEVEL_TRACE, msg, key1, value1, key2, value2, null, null, null, null, null, null, null, null, null, null, 2);
	}

	public static void trace(String msg, String key1, Object value1, String key2, Object value2, String key3,
	                         Object value3) {
		log(LEVEL_TRACE, msg, key1, value1, key2, value2, key3, value3, null, null, null, null, null, null, null, null, 3);
	}

	public static void trace(String msg, String key1, Object value1, String key2, Object value2, String key3,
	                         Object value3, String key4, Object value4) {
		log(LEVEL_TRACE, msg, key1, value1, key2, value2, key3, value3, key4, value4, null, null, null, null, null, null, 4);
	}

	public static void trace(String msg, String key1, Object value1, String key2, Object value2, String key3,
	                         Object value3, String key4, Object value4, String key5, Object value5) {
		log(LEVEL_TRACE, msg, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, null, null, null, null, 5);
	}

	public static void trace(String msg, String key1, Object value1, String key2, Object value2, String key3,
	                         Object value3, String key4, Object value4, String key5, Object value5, String key6, Object value6) {
		log(LEVEL_TRACE, msg, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, key6, value6, null, null, 6);
	}

	public static void trace(String msg, String key1, Object value1, String key2, Object value2, String key3, Object value3,
	                         String key4, Object value4, String key5, Object value5, String key6, Object value6, String key7, Object value7) {
		log(LEVEL_TRACE, msg, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, key6, value6, key7, value7, 7);
	}

	public static void trace(String msg, Throwable err) {
		if (isTraceEnabled()) {
			trace(msg, "message", err.getMessage());
			err.printStackTrace();
		}
	}

	/* DEBUG */

	public static void debug(String msg) {
		log(LEVEL_DEBUG, msg, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0);
	}

	public static void debug(String msg, String key, Object value) {
		log(LEVEL_DEBUG, msg, key, value, null, null, null, null, null, null, null, null, null, null, null, null, 1);
	}

	public static void debug(String msg, String key1, Object value1, String key2, Object value2) {
		log(LEVEL_DEBUG, msg, key1, value1, key2, value2, null, null, null, null, null, null, null, null, null, null, 2);
	}

	public static void debug(String msg, String key1, Object value1, String key2, Object value2, String key3,
	                         Object value3) {
		log(LEVEL_DEBUG, msg, key1, value1, key2, value2, key3, value3, null, null, null, null, null, null, null, null, 3);
	}

	public static void debug(String msg, String key1, Object value1, String key2, Object value2, String key3,
	                         Object value3, String key4, Object value4) {
		log(LEVEL_DEBUG, msg, key1, value1, key2, value2, key3, value3, key4, value4, null, null, null, null, null, null, 4);
	}

	public static void debug(String msg, String key1, Object value1, String key2, Object value2, String key3,
	                         Object value3, String key4, Object value4, String key5, Object value5) {
		log(LEVEL_DEBUG, msg, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, null, null, null, null, 5);
	}

	public static void debug(String msg, String key1, Object value1, String key2, Object value2, String key3,
	                         Object value3, String key4, Object value4, String key5, Object value5, String key6, Object value6) {
		log(LEVEL_DEBUG, msg, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, key6, value6, null, null, 6);
	}

	public static void debug(String msg, String key1, Object value1, String key2, Object value2, String key3, Object value3,
	                         String key4, Object value4, String key5, Object value5, String key6, Object value6, String key7, Object value7) {
		log(LEVEL_DEBUG, msg, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, key6, value6, key7, value7, 7);
	}

	public static void debug(String msg, Throwable err) {
		if (isDebugEnabled()) {
			debug(msg, "message", err.getMessage());
			err.printStackTrace();
		}
	}

	/* INFO */

	public static void info(String msg) {
		log(LEVEL_INFO, msg, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0);
	}

	public static void info(String msg, String key, Object value) {
		log(LEVEL_INFO, msg, key, value, null, null, null, null, null, null, null, null, null, null, null, null, 1);
	}

	public static void info(String msg, String key1, Object value1, String key2, Object value2) {
		log(LEVEL_INFO, msg, key1, value1, key2, value2, null, null, null, null, null, null, null, null, null, null, 2);
	}

	public static void info(String msg, String key1, Object value1, String key2, Object value2, String key3,
	                        Object value3) {
		log(LEVEL_INFO, msg, key1, value1, key2, value2, key3, value3, null, null, null, null, null, null, null, null, 3);
	}

	public static void info(String msg, String key1, Object value1, String key2, Object value2, String key3,
	                        Object value3, String key4, Object value4) {
		log(LEVEL_INFO, msg, key1, value1, key2, value2, key3, value3, key4, value4, null, null, null, null, null, null, 4);
	}

	public static void info(String msg, String key1, Object value1, String key2, Object value2, String key3,
	                        Object value3, String key4, Object value4, String key5, Object value5) {
		log(LEVEL_INFO, msg, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, null, null, null, null, 5);
	}

	public static void info(String msg, String key1, Object value1, String key2, Object value2, String key3,
	                        Object value3, String key4, Object value4, String key5, Object value5, String key6, Object value6) {
		log(LEVEL_INFO, msg, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, key6, value6, null, null, 6);
	}

	public static void info(String msg, String key1, Object value1, String key2, Object value2, String key3, Object value3,
	                        String key4, Object value4, String key5, Object value5, String key6, Object value6, String key7, Object value7) {
		log(LEVEL_INFO, msg, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, key6, value6, key7, value7, 7);
	}

	public static void info(String msg, Throwable err) {
		if (isInfoEnabled()) {
			info(msg, "message", err.getMessage());
			err.printStackTrace();
		}
	}

	/* WARN */

	public static void warn(String msg) {
		log(LEVEL_WARN, msg, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0);
	}

	public static void warn(String msg, String key, Object value) {
		log(LEVEL_WARN, msg, key, value, null, null, null, null, null, null, null, null, null, null, null, null, 1);
	}

	public static void warn(String msg, String key1, Object value1, String key2, Object value2) {
		log(LEVEL_WARN, msg, key1, value1, key2, value2, null, null, null, null, null, null, null, null, null, null, 2);
	}

	public static void warn(String msg, String key1, Object value1, String key2, Object value2, String key3,
	                        Object value3) {
		log(LEVEL_WARN, msg, key1, value1, key2, value2, key3, value3, null, null, null, null, null, null, null, null, 3);
	}

	public static void warn(String msg, String key1, Object value1, String key2, Object value2, String key3,
	                        Object value3, String key4, Object value4) {
		log(LEVEL_WARN, msg, key1, value1, key2, value2, key3, value3, key4, value4, null, null, null, null, null, null, 4);
	}

	public static void warn(String msg, String key1, Object value1, String key2, Object value2, String key3,
	                        Object value3, String key4, Object value4, String key5, Object value5) {
		log(LEVEL_WARN, msg, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, null, null, null, null, 5);
	}

	public static void warn(String msg, String key1, Object value1, String key2, Object value2, String key3,
	                        Object value3, String key4, Object value4, String key5, Object value5, String key6, Object value6) {
		log(LEVEL_WARN, msg, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, key6, value6, null, null, 6);
	}

	public static void warn(String msg, String key1, Object value1, String key2, Object value2, String key3, Object value3,
	                        String key4, Object value4, String key5, Object value5, String key6, Object value6, String key7, Object value7) {
		log(LEVEL_WARN, msg, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, key6, value6, key7, value7, 7);
	}

	public static void warn(String msg, Throwable err) {
		if (isWarnEnabled()) {
			warn(msg, "message", err.getMessage());
			err.printStackTrace();
		}
	}

	/* ERROR */

	public static void error(String msg) {
		log(LEVEL_ERROR, msg, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0);
	}

	public static void error(String msg, String key, Object value) {
		log(LEVEL_ERROR, msg, key, value, null, null, null, null, null, null, null, null, null, null, null, null, 1);
	}

	public static void error(String msg, String key1, Object value1, String key2, Object value2) {
		log(LEVEL_ERROR, msg, key1, value1, key2, value2, null, null, null, null, null, null, null, null, null, null, 2);
	}

	public static void error(String msg, String key1, Object value1, String key2, Object value2, String key3,
	                         Object value3) {
		log(LEVEL_ERROR, msg, key1, value1, key2, value2, key3, value3, null, null, null, null, null, null, null, null, 3);
	}

	public static void error(String msg, String key1, Object value1, String key2, Object value2, String key3,
	                         Object value3, String key4, Object value4) {
		log(LEVEL_ERROR, msg, key1, value1, key2, value2, key3, value3, key4, value4, null, null, null, null, null, null, 4);
	}

	public static void error(String msg, String key1, Object value1, String key2, Object value2, String key3,
	                         Object value3, String key4, Object value4, String key5, Object value5) {
		log(LEVEL_ERROR, msg, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, null, null, null, null, 5);
	}

	public static void error(String msg, String key1, Object value1, String key2, Object value2, String key3,
	                         Object value3, String key4, Object value4, String key5, Object value5, String key6, Object value6) {
		log(LEVEL_ERROR, msg, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, key6, value6, null, null, 6);
	}

	public static void error(String msg, String key1, Object value1, String key2, Object value2, String key3, Object value3,
	                         String key4, Object value4, String key5, Object value5, String key6, Object value6, String key7, Object value7) {
		log(LEVEL_ERROR, msg, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, key6, value6, key7, value7, 7);
	}

	public static void error(String msg, Throwable err) {
		if (isErrorEnabled()) {
			error(msg, "message", err.getMessage());
			err.printStackTrace();
		}
	}

}
