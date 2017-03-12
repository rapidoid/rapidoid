package org.rapidoid.jpa;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.HTTP;
import org.rapidoid.http.IsolatedIntegrationTest;
import org.rapidoid.setup.My;
import org.rapidoid.setup.On;

import java.util.concurrent.atomic.AtomicInteger;

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

@Authors("Nikolche Mihajlovski")
@Since("5.2.0")
public class JPACustomizationTest extends IsolatedIntegrationTest {

	final int total = 3;

	@Test
	public void testCustomEMWithReq() {
		JPA.bootstrap(path());

		On.post("/tx").transaction().json(() -> JPA.insert(new Book("posted")));

		AtomicInteger n = new AtomicInteger();

		My.entityManagerProvider(req -> {
			notNull(req);
			n.incrementAndGet();
			return JPA.provideEmf().createEntityManager();
		});

		for (int i = 0; i < total; i++) {
			int expectedId = i + 1;
			HTTP.post(localhost("/tx"))
				.expect()
				.entry("id", expectedId)
				.entry("title", "posted");
		}

		eq(n.get(), total);
	}

	@Test
	public void testCustomEMWithoutReq() {
		JPA.bootstrap(path());

		AtomicInteger n = new AtomicInteger();

		My.entityManagerProvider(req -> {
			isNull(req);
			n.incrementAndGet();
			return JPA.provideEmf().createEntityManager();
		});

		for (int i = 0; i < total; i++) {
			JPA.transaction(() -> {
				Book book = JPA.insert(new Book("b"));
				notNull(book.getId());
			});
		}

		eq(n.get(), total);
	}

	@Test
	public void testCustomEMFWithReq() {
		JPA.bootstrap(path());

		On.post("/tx").transaction().json(() -> JPA.insert(new Book("posted")));

		AtomicInteger n = new AtomicInteger();

		My.entityManagerFactoryProvider(req -> {
			notNull(req);
			n.incrementAndGet();
			return JPA.provideEmf();
		});

		for (int i = 0; i < total; i++) {
			int expectedId = i + 1;
			HTTP.post(localhost("/tx"))
				.expect()
				.entry("id", expectedId)
				.entry("title", "posted");
		}

		eq(n.get(), total);
	}

	@Test
	public void testCustomEMFWithoutReq() {
		JPA.bootstrap(path());

		AtomicInteger n = new AtomicInteger();

		My.entityManagerFactoryProvider(req -> {
			isNull(req);
			n.incrementAndGet();
			return JPA.provideEmf();
		});

		for (int i = 0; i < total; i++) {
			JPA.transaction(() -> {
				Book book = JPA.insert(new Book("b"));
				notNull(book.getId());
			});
		}

		eq(n.get(), total);
	}

}
