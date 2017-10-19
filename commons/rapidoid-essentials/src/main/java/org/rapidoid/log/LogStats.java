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

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Nikolche Mihajlovski
 * @since 5.3.0
 */
public class LogStats extends RapidoidThing {

	private static final AtomicBoolean HAS_ERRORS = new AtomicBoolean();

	public static synchronized void reset() {
		HAS_ERRORS.set(false);
	}

	public static boolean hasErrors() {
		return HAS_ERRORS.get();
	}

	public static void hasErrors(boolean hasErrors) {
		HAS_ERRORS.set(hasErrors);
	}
}
