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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class LogbackUtil extends RapidoidThing {

	public static void setupLogger() {
		ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();

		if (loggerFactory instanceof LoggerContext) {
			LoggerContext lc = (LoggerContext) loggerFactory;

			if (U.isEmpty(lc.getCopyOfPropertyMap())) {
				Logger root = lc.getLogger(Logger.ROOT_LOGGER_NAME);
				root.setLevel(Level.INFO);
			}
		}
	}

}
