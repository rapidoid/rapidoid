/*-
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2020 Nikolche Mihajlovski and contributors
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

package org.rapidoid.ctx;

import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.log.LogLevel;
import org.rapidoid.test.AbstractCommonsTest;
import org.rapidoid.test.TestRnd;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class AppCtxTest extends AbstractCommonsTest {

	@Test
	public void testAppCtx() {
		Log.setLogLevel(LogLevel.INFO);

		multiThreaded(1000, 1000000, () -> {
			Ctxs.open("test");

			UserInfo user = new UserInfo(TestRnd.rndStr(10), U.set("role1"));

			Ctxs.required().setUser(user);

			eq(Ctxs.required().user(), user);

			Ctxs.close();
		});
	}

}
