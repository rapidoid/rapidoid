package org.rapidoid.util;

import org.rapidoid.test.TestCommons;
import org.testng.annotations.Test;

/*
 * #%L
 * rapidoid-utils
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

public class AppCtxTest extends TestCommons {

	@Test
	public void testAppCtx() {
		multiThreaded(1000, 1000000, new Runnable() {
			@Override
			public void run() {

				AppCtx.reset();

				String username = rndStr(10);
				Integer n = rnd();

				AppCtx.setUsername(username);
				AppCtx.setExchange(n);

				eq(AppCtx.username(), username);
				eq(AppCtx.exchange(), n);

				AppCtx.reset();
			}
		});
	}

}
