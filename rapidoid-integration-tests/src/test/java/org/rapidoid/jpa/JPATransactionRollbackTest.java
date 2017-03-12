package org.rapidoid.jpa;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.annotation.Valid;
import org.rapidoid.http.HTTP;
import org.rapidoid.http.IsolatedIntegrationTest;
import org.rapidoid.job.Jobs;
import org.rapidoid.log.Log;
import org.rapidoid.log.LogLevel;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

import java.util.Map;

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
@Since("5.2.7")
public class JPATransactionRollbackTest extends IsolatedIntegrationTest {

	@Test
	public void testTxRollback() {
		JPA.bootstrap(path());

		On.post("/books").transaction().json((@Valid Book b) -> {
			JPA.save(b);
			throw U.rte("Intentional error!");
		});

		Log.setLogLevel(LogLevel.NO_LOGS);

		Map<String, ?> data = U.map("title", "Java Book", "year", 2016);

		HTTP.post(localhost("/books")).data(data).benchmark(10, 100, 1000);

		int k = 5;
		multiThreaded(1, k, () -> {
			try {
				tx(this::txFailure);
			} catch (Exception e) {
				// do nothing
			}
		});

		Log.setLogLevel(Log.LEVEL_INFO);

		tx(this::checkData);

		eq(Jobs.errorCounter().get(), 0);
	}

	private void txFailure() {
		JPA.save(new Book("book 1"));
		throw U.rte("Intentional error!");
	}

	private void checkData() {
		eq(JPA.of(Book.class).all().size(), 0);
	}

}
