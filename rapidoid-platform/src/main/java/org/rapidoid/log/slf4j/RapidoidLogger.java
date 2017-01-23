package org.rapidoid.log.slf4j;

/*
 * #%L
 * rapidoid-platform
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.log.LogLevel;
import org.rapidoid.u.U;
import org.slf4j.Logger;
import org.slf4j.Marker;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class RapidoidLogger extends RapidoidThing implements Logger {

	private final String name;

	public RapidoidLogger(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	private boolean isMarkerEnabled(LogLevel level, Marker marker) {
		return Log.isEnabled(name, level); // ignore the marker
	}

	private static String mark(Marker marker, String msg) {
		return U.frmt("[%s] %s", marker.getName(), msg);
	}

	private static String frmt(String format, Object arg) {
		return format.replaceFirst("\\{\\}", U.str(arg));
	}

	private static String frmt(String format, Object arg1, Object arg2) {
		String s = frmt(format, arg1);
		s = frmt(s, arg2);
		return s;
	}

	private static String frmt(String format, Object... args) {
		String s = format;

		for (Object arg : args) {
			s = frmt(s, arg);
		}

		return s;
	}

	/* TRACE */

	@Override
	public boolean isTraceEnabled() {
		return Log.isEnabled(name, LogLevel.TRACE);
	}

	@Override
	public void trace(String msg) {
		Log.log(name, LogLevel.TRACE, msg);
	}

	@Override
	public void trace(String format, Object arg) {
		if (isTraceEnabled()) {
			trace(frmt(format, arg));
		}
	}

	@Override
	public void trace(String format, Object arg1, Object arg2) {
		if (isTraceEnabled()) {
			trace(frmt(format, arg1, arg2));
		}
	}

	@Override
	public void trace(String format, Object... arguments) {
		if (isTraceEnabled()) {
			trace(frmt(format, arguments));
		}
	}

	@Override
	public void trace(String msg, Throwable t) {
		Log.log(name, LogLevel.TRACE, msg, t);
	}

	@Override
	public boolean isTraceEnabled(Marker marker) {
		return isMarkerEnabled(LogLevel.TRACE, marker);
	}

	@Override
	public void trace(Marker marker, String msg) {
		if (isTraceEnabled(marker)) {
			trace(mark(marker, msg));
		}
	}

	@Override
	public void trace(Marker marker, String format, Object arg) {
		if (isTraceEnabled(marker)) {
			trace(mark(marker, format), arg);
		}
	}

	@Override
	public void trace(Marker marker, String format, Object arg1, Object arg2) {
		if (isTraceEnabled(marker)) {
			trace(mark(marker, format), arg1, arg2);
		}
	}

	@Override
	public void trace(Marker marker, String format, Object... arguments) {
		if (isTraceEnabled(marker)) {
			trace(mark(marker, format), arguments);
		}
	}

	@Override
	public void trace(Marker marker, String msg, Throwable t) {
		if (isTraceEnabled(marker)) {
			trace(mark(marker, msg), t);
		}
	}


	/* DEBUG */

	@Override
	public boolean isDebugEnabled() {
		return Log.isEnabled(name, LogLevel.DEBUG);
	}

	@Override
	public void debug(String msg) {
		Log.log(name, LogLevel.DEBUG, msg);
	}

	@Override
	public void debug(String format, Object arg) {
		if (isDebugEnabled()) {
			debug(frmt(format, arg));
		}
	}

	@Override
	public void debug(String format, Object arg1, Object arg2) {
		if (isDebugEnabled()) {
			debug(frmt(format, arg1, arg2));
		}
	}

	@Override
	public void debug(String format, Object... arguments) {
		if (isDebugEnabled()) {
			debug(frmt(format, arguments));
		}
	}

	@Override
	public void debug(String msg, Throwable t) {
		Log.log(name, LogLevel.DEBUG, msg, t);
	}

	@Override
	public boolean isDebugEnabled(Marker marker) {
		return isMarkerEnabled(LogLevel.DEBUG, marker);
	}

	@Override
	public void debug(Marker marker, String msg) {
		if (isDebugEnabled(marker)) {
			debug(mark(marker, msg));
		}
	}

	@Override
	public void debug(Marker marker, String format, Object arg) {
		if (isDebugEnabled(marker)) {
			debug(mark(marker, format), arg);
		}
	}

	@Override
	public void debug(Marker marker, String format, Object arg1, Object arg2) {
		if (isDebugEnabled(marker)) {
			debug(mark(marker, format), arg1, arg2);
		}
	}

	@Override
	public void debug(Marker marker, String format, Object... arguments) {
		if (isDebugEnabled(marker)) {
			debug(mark(marker, format), arguments);
		}
	}

	@Override
	public void debug(Marker marker, String msg, Throwable t) {
		if (isDebugEnabled(marker)) {
			debug(mark(marker, msg), t);
		}
	}


	/* INFO */

	@Override
	public boolean isInfoEnabled() {
		return Log.isEnabled(name, LogLevel.INFO);
	}

	@Override
	public void info(String msg) {
		Log.log(name, LogLevel.INFO, msg);
	}

	@Override
	public void info(String format, Object arg) {
		if (isInfoEnabled()) {
			info(frmt(format, arg));
		}
	}

	@Override
	public void info(String format, Object arg1, Object arg2) {
		if (isInfoEnabled()) {
			info(frmt(format, arg1, arg2));
		}
	}

	@Override
	public void info(String format, Object... arguments) {
		if (isInfoEnabled()) {
			info(frmt(format, arguments));
		}
	}

	@Override
	public void info(String msg, Throwable t) {
		Log.log(name, LogLevel.INFO, msg, t);
	}

	@Override
	public boolean isInfoEnabled(Marker marker) {
		return isMarkerEnabled(LogLevel.INFO, marker);
	}

	@Override
	public void info(Marker marker, String msg) {
		if (isInfoEnabled(marker)) {
			info(mark(marker, msg));
		}
	}

	@Override
	public void info(Marker marker, String format, Object arg) {
		if (isInfoEnabled(marker)) {
			info(mark(marker, format), arg);
		}
	}

	@Override
	public void info(Marker marker, String format, Object arg1, Object arg2) {
		if (isInfoEnabled(marker)) {
			info(mark(marker, format), arg1, arg2);
		}
	}

	@Override
	public void info(Marker marker, String format, Object... arguments) {
		if (isInfoEnabled(marker)) {
			info(mark(marker, format), arguments);
		}
	}

	@Override
	public void info(Marker marker, String msg, Throwable t) {
		if (isInfoEnabled(marker)) {
			info(mark(marker, msg), t);
		}
	}


	/* WARN */

	@Override
	public boolean isWarnEnabled() {
		return Log.isEnabled(name, LogLevel.WARN);
	}

	@Override
	public void warn(String msg) {
		Log.log(name, LogLevel.WARN, msg);
	}

	@Override
	public void warn(String format, Object arg) {
		if (isWarnEnabled()) {
			warn(frmt(format, arg));
		}
	}

	@Override
	public void warn(String format, Object arg1, Object arg2) {
		if (isWarnEnabled()) {
			warn(frmt(format, arg1, arg2));
		}
	}

	@Override
	public void warn(String format, Object... arguments) {
		if (isWarnEnabled()) {
			warn(frmt(format, arguments));
		}
	}

	@Override
	public void warn(String msg, Throwable t) {
		Log.log(name, LogLevel.WARN, msg, t);
	}

	@Override
	public boolean isWarnEnabled(Marker marker) {
		return isMarkerEnabled(LogLevel.WARN, marker);
	}

	@Override
	public void warn(Marker marker, String msg) {
		if (isWarnEnabled(marker)) {
			warn(mark(marker, msg));
		}
	}

	@Override
	public void warn(Marker marker, String format, Object arg) {
		if (isWarnEnabled(marker)) {
			warn(mark(marker, format), arg);
		}
	}

	@Override
	public void warn(Marker marker, String format, Object arg1, Object arg2) {
		if (isWarnEnabled(marker)) {
			warn(mark(marker, format), arg1, arg2);
		}
	}

	@Override
	public void warn(Marker marker, String format, Object... arguments) {
		if (isWarnEnabled(marker)) {
			warn(mark(marker, format), arguments);
		}
	}

	@Override
	public void warn(Marker marker, String msg, Throwable t) {
		if (isWarnEnabled(marker)) {
			warn(mark(marker, msg), t);
		}
	}


	/* ERROR */

	@Override
	public boolean isErrorEnabled() {
		return Log.isEnabled(name, LogLevel.ERROR);
	}

	@Override
	public void error(String msg) {
		Log.log(name, LogLevel.ERROR, msg);
	}

	@Override
	public void error(String format, Object arg) {
		if (isErrorEnabled()) {
			error(frmt(format, arg));
		}
	}

	@Override
	public void error(String format, Object arg1, Object arg2) {
		if (isErrorEnabled()) {
			error(frmt(format, arg1, arg2));
		}
	}

	@Override
	public void error(String format, Object... arguments) {
		if (isErrorEnabled()) {
			error(frmt(format, arguments));
		}
	}

	@Override
	public void error(String msg, Throwable t) {
		Log.log(name, LogLevel.ERROR, msg, t);
	}

	@Override
	public boolean isErrorEnabled(Marker marker) {
		return isMarkerEnabled(LogLevel.ERROR, marker);
	}

	@Override
	public void error(Marker marker, String msg) {
		if (isErrorEnabled(marker)) {
			error(mark(marker, msg));
		}
	}

	@Override
	public void error(Marker marker, String format, Object arg) {
		if (isErrorEnabled(marker)) {
			error(mark(marker, format), arg);
		}
	}

	@Override
	public void error(Marker marker, String format, Object arg1, Object arg2) {
		if (isErrorEnabled(marker)) {
			error(mark(marker, format), arg1, arg2);
		}
	}

	@Override
	public void error(Marker marker, String format, Object... arguments) {
		if (isErrorEnabled(marker)) {
			error(mark(marker, format), arguments);
		}
	}

	@Override
	public void error(Marker marker, String msg, Throwable t) {
		if (isErrorEnabled(marker)) {
			error(mark(marker, msg), t);
		}
	}

}
