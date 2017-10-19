package org.rapidoid.event;

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

import org.junit.After;
import org.junit.Test;
import org.rapidoid.log.Log;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;

import java.util.List;
import java.util.Map;

/**
 * @author Nikolche Mihajlovski
 * @since 5.2.0
 */
public class EventsTest extends TestCommons {

	@After
	public void cleanUp() {
		Events.reset();
	}

	@Test
	public void testLogEvents() {

		final List<String> warnings = U.list();

		Events.LOG_WARN.listener(new EventListener() {
			@Override
			public void onEvent(Event event, Map<String, Object> data) {
				isFalse(data.isEmpty());
				eq(event, Events.LOG_WARN);
				warnings.add(data.get("_").toString());
			}
		});

		Log.warn("WRN!");

		eq(warnings, U.list("WRN!"));
	}

	@Test(timeout = 1000)
	public void firingUnusedEventsMustBeFast() {
		for (int i = 0; i < 100 * 1000 * 1000; i++) {
			Fire.event(Events.LOG_TRACE);
		}
	}

}
