package org.slf4j.impl;

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
import org.rapidoid.log.slf4j.RapidoidLoggerFactory;
import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class StaticLoggerBinder extends RapidoidThing implements LoggerFactoryBinder {

	// required by slf4j, shouldn't be final
	public static String REQUESTED_API_VERSION = "1.7";

	private static final StaticLoggerBinder INSTANCE = new StaticLoggerBinder();

	private final ILoggerFactory loggerFactory = new RapidoidLoggerFactory();

	// required by slf4j
	public static StaticLoggerBinder getSingleton() {
		return INSTANCE;
	}

	public ILoggerFactory getLoggerFactory() {
		return loggerFactory;
	}

	public String getLoggerFactoryClassStr() {
		return loggerFactory.getClass().getName();
	}

}
