package org.rapidoid.ctx;

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

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.job.Jobs;
import org.rapidoid.log.Log;
import org.rapidoid.test.AbstractCommonsTest;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class JobChainingTest extends AbstractCommonsTest {

	@Test
	public void testJobChaining() {
		Ctx ctx = Ctxs.open("root");
		isFalse(ctx.isClosed());

		asyncLoop(1000);

		ctx.close();

		while (!ctx.isClosed()) {
			U.sleep(1);
		}

		Log.info("Done");
	}

	private void asyncLoop(final int n) {
		if (n == 0) return;

		Jobs.after(1).milliseconds(new Runnable() {
			@Override
			public void run() {
				asyncLoop(n - 1);
			}
		});
	}

}
