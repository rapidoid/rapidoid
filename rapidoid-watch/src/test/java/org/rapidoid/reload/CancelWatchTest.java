package org.rapidoid.reload;

/*
 * #%L
 * rapidoid-watch
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

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.io.watch.Watch;
import org.rapidoid.lambda.Operation;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.4.5")
public class CancelWatchTest extends TestCommons {

	@Test
	public void testCancelAll() {
		// expecting a lot of content with frequent changes
		String userHome = System.getProperty("user.home");

		Operation<String> noOp = new Operation<String>() {
			@Override
			public void execute(String obj) {
				// do nothing
			}
		};

		for (int i = 0; i < 30; i++) {
			Watch.dir(userHome, noOp);
			U.sleep(100);
			Watch.cancelAll();
		}
	}

}
