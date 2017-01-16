package org.rapidoid.http;

/*
 * #%L
 * rapidoid-integration-tests
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
import org.rapidoid.commons.Err;
import org.rapidoid.ctx.Contextual;
import org.rapidoid.ctx.With;
import org.rapidoid.u.U;
import org.rapidoid.util.Wait;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class WithContextTest extends IsolatedIntegrationTest {

	public static final int JOB_COUNT = 1000;

	@Test
	public void testNestedContext() {
		CountDownLatch latch = new CountDownLatch(JOB_COUNT + 1);

		eq(ctxInfo(), "null:[]:false");

		With.username("root").roles(U.set("admin")).run(() -> {
			eq(ctxInfo(), "root:[admin]:true");

			for (int i = 0; i < JOB_COUNT; i++) {
				int n = i;

				With.username("user" + n).roles(U.set("manager" + n)).run(() -> {
					eq(ctxInfo(), U.frmt("user%s:[manager%s]:true", n, n));
					latch.countDown();
				});
			}

			eq(ctxInfo(), "root:[admin]:true");
			latch.countDown();
		});

		eq(ctxInfo(), "null:[]:false");

		Wait.on(latch, 10, TimeUnit.SECONDS);
		eq(latch.getCount(), 0);

		eq(ctxInfo(), "null:[]:false");
	}

	@Test
	public void testErrorHandling() {
		With.exchange("req1").run(() -> {
			throw Err.intentional();
		});
	}

	private String ctxInfo() {
		return U.join(":", Contextual.username(), Contextual.roles(), Contextual.isLoggedIn());
	}

}
