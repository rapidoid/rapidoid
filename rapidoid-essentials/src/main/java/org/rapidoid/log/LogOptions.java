package org.rapidoid.log;

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

import org.rapidoid.RapidoidThing;
import org.slf4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.Callable;

/**
 * @author Nikolche Mihajlovski
 * @since 5.3.0
 */
public class LogOptions extends RapidoidThing {

	private volatile Callable<Logger> loggerFactory;

	private volatile boolean styled = System.console() != null;

	private volatile String prefix;

	private volatile boolean showThread = true;

	private volatile boolean showDateTime = true;

	private volatile DateFormat dateTimeFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss");

	public Callable<Logger> loggerFactory() {
		return loggerFactory;
	}

	public LogOptions loggerFactory(Callable<Logger> loggerFactory) {
		this.loggerFactory = loggerFactory;
		return this;
	}

	public boolean styled() {
		return styled;
	}

	public LogOptions styled(boolean styled) {
		this.styled = styled;
		return this;
	}

	public String prefix() {
		return prefix;
	}

	public LogOptions prefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	public boolean showThread() {
		return showThread;
	}

	public LogOptions showThread(boolean showThread) {
		this.showThread = showThread;
		return this;
	}

	public boolean showDateTime() {
		return showDateTime;
	}

	public LogOptions showDateTime(boolean showDateTime) {
		this.showDateTime = showDateTime;
		return this;
	}

	public DateFormat dateTimeFormat() {
		return dateTimeFormat;
	}

	public LogOptions dateTimeFormat(DateFormat dateTimeFormat) {
		this.dateTimeFormat = dateTimeFormat;
		return this;
	}
}
