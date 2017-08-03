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
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.Since;
import org.rapidoid.cache.Cached;
import org.rapidoid.setup.App;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

import java.util.concurrent.atomic.AtomicInteger;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class HttpCachingTest extends IsolatedIntegrationTest {

	@Test
	public void testHttpCaching() {

		// without caching
		AtomicInteger x = new AtomicInteger();
		On.get("/x").plain(() -> x.incrementAndGet());

		// with caching
		AtomicInteger y = new AtomicInteger();
		On.get("/y").cacheTTL(1000).plain(() -> y.incrementAndGet());

		exerciseCaching();
	}

	private void exerciseCaching() {
		int next = 1;
		for (int n = 1; n <= 3; n++) {

			for (int i = 0; i < 10; i++) {
				Self.get("/x").expect("" + next++);
				Self.get("/y").expect("" + n);
			}

			U.sleep(1100);
		}
	}

	@Test
	public void testHttpCachingWithAnnotations() {

		App.beans(CachingCtrl.class);

		exerciseCaching();
	}

	static class CachingCtrl {

		// without caching
		AtomicInteger x = new AtomicInteger();

		// with caching
		AtomicInteger y = new AtomicInteger();

		@GET
		public Object x() {
			return x.incrementAndGet();
		}

		@GET
		@Cached(ttl = 1000)
		public Object y() {
			return y.incrementAndGet();
		}
	}

}
