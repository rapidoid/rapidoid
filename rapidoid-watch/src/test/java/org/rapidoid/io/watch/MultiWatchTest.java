package org.rapidoid.io.watch;

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
import org.rapidoid.collection.Coll;
import org.rapidoid.io.IO;
import org.rapidoid.lambda.Operation;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.io.IOException;
import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class MultiWatchTest extends TestCommons {

	@Test(timeout = 60000)
	public void shouldSupportMultipleWatchCalls() throws IOException {
		String dir = createTempDir("watch-service-test");

		if (!TestCommons.RAPIDOID_CI) {
			for (int i = 0; i < 10; i++) {
				exerciseMultiWatch(dir);
			}
		}
	}

	public void exerciseMultiWatch(String dir) {
		final Set<Integer> seen = Coll.synchronizedSet();

		int total = 50;

		for (int i = 0; i < total; i++) {
			final int seenBy = i;
			Msc.watchForChanges(dir, new Operation<String>() {
					@Override
					public void execute(String filename) throws Exception {
						seen.add(seenBy);
					}
				}
			);
		}

		giveItTimeToRefresh();

		IO.save(Msc.path(dir, "a.txt"), "ABC-" + U.time());

		while (seen.size() < total) {
			U.sleep(200);
		}

		eq(seen.size(), total);

		Watch.cancelAll();
	}

	private void giveItTimeToRefresh() {
		U.sleep(3000);
	}

}
