package org.rapidoid.webapp;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.ctx.UserInfo;
import org.rapidoid.job.Jobs;
import org.rapidoid.test.TestCommons;

/*
 * #%L
 * rapidoid-http
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
@Since("4.1.0")
public class JobsTest extends TestCommons {

	@Test(timeout = 20000)
	public void testJobsExecution() {

		int total = 100000;
		final AtomicInteger counter = new AtomicInteger();

		multiThreaded(1000, total, new Runnable() {

			@Override
			public void run() {
				Ctxs.open("test-job");

				final WebApp app = new WebApp();
				final UserInfo user = new UserInfo();
				user.username = rndStr(50);

				Ctxs.ctx().setUser(user);
				Ctxs.ctx().setApp(app);
				ensureProperContext(user, app);

				ScheduledFuture<?> future = Jobs.schedule(new Runnable() {
					@Override
					public void run() {
						ensureProperContext(user, app);
						counter.incrementAndGet();
					}
				}, 100, TimeUnit.MILLISECONDS);

				try {
					future.get();
				} catch (Exception e) {
					e.printStackTrace();
					fail("The job throwed an exception!");
				}

				Ctxs.close();
			}

		});

		eq(counter.get(), total);
	}

	private void ensureProperContext(UserInfo user, WebApp app) {
		eq(Ctxs.ctx().user(), user);
		eq(Ctxs.ctx().app(), app);
	}

}
