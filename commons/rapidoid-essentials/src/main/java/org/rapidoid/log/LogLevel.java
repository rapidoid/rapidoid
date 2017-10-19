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

import org.rapidoid.event.Event;
import org.rapidoid.event.Events;

/**
 * @author Nikolche Mihajlovski
 * @since 2.0.0
 */
public enum LogLevel {

	TRACE(Events.LOG_TRACE),
	DEBUG(Events.LOG_DEBUG),
	INFO(Events.LOG_INFO),
	WARN(Events.LOG_WARN),
	ERROR(Events.LOG_ERROR),
	FATAL(Events.LOG_FATAL),
	NO_LOGS(null);

	private final Event event;

	LogLevel(Event event) {
		this.event = event;
	}

	public Event event() {
		return event;
	}
}
