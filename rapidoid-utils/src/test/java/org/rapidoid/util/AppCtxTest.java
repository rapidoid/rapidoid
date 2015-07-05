package org.rapidoid.util;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.UserInfo;
import org.rapidoid.test.TestCommons;
import org.junit.Test;

/*
 * #%L
 * rapidoid-utils
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class AppCtxTest extends TestCommons {

	@Test
	public void testAppCtx() {
		multiThreaded(1000, 1000000, new Runnable() {
			@Override
			public void run() {

				Ctx.reset();

				UserInfo user = new UserInfo();
				user.username = rndStr(10);

				Ctx.setUser(user);

				eq(Ctx.user(), user);

				Ctx.reset();
			}
		});
	}

}
