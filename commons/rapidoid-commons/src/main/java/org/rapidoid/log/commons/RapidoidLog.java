package org.rapidoid.log.commons;

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

import org.apache.commons.logging.Log;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.io.Serializable;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class RapidoidLog extends RapidoidThing implements Log, Serializable {

	private static final long serialVersionUID = 1203497152501297512L;

	private final String name; // FIXME use the name in the logs

	RapidoidLog(String name) {
		this.name = name;
	}

	public void trace(Object message) {
		org.rapidoid.log.Log.trace(message + "");
	}

	public void trace(Object message, Throwable t) {
		org.rapidoid.log.Log.trace(message + "", "error", t);
	}

	public void debug(Object message) {
		org.rapidoid.log.Log.debug(message + "");
	}

	public void debug(Object message, Throwable t) {
		org.rapidoid.log.Log.debug(message + "", "error", t);
	}

	public void info(Object message) {
		org.rapidoid.log.Log.info(message + "");
	}

	public void info(Object message, Throwable t) {
		org.rapidoid.log.Log.info(message + "", "error", t);
	}

	public void warn(Object message) {
		org.rapidoid.log.Log.warn(message + "");
	}

	@Override
	public void warn(Object message, Throwable t) {
		org.rapidoid.log.Log.warn(message + "", t);
	}

	@Override
	public void error(Object message) {
		org.rapidoid.log.Log.error(message + "");
	}

	@Override
	public void error(Object message, Throwable t) {
		org.rapidoid.log.Log.error(message + "", t);
	}

	@Override
	public void fatal(Object message) {
		org.rapidoid.log.Log.fatal(message + "");
	}

	@Override
	public void fatal(Object message, Throwable t) {
		org.rapidoid.log.Log.fatal(message + "", t);
	}

	@Override
	public boolean isTraceEnabled() {
		return org.rapidoid.log.Log.isTraceEnabled();
	}

	@Override
	public boolean isDebugEnabled() {
		return org.rapidoid.log.Log.isDebugEnabled();
	}

	@Override
	public boolean isInfoEnabled() {
		return org.rapidoid.log.Log.isInfoEnabled();
	}

	@Override
	public boolean isWarnEnabled() {
		return org.rapidoid.log.Log.isWarnEnabled();
	}

	@Override
	public boolean isErrorEnabled() {
		return org.rapidoid.log.Log.isErrorEnabled();
	}

	@Override
	public boolean isFatalEnabled() {
		return org.rapidoid.log.Log.isFatalEnabled();
	}
}
