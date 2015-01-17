package org.rapidoid.db;

/*
 * #%L
 * rapidoid-db-tests
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import java.util.concurrent.atomic.AtomicInteger;

import org.rapidoid.db.model.Person;
import org.rapidoid.util.Log;
import org.rapidoid.util.LogLevel;
import org.rapidoid.util.U;
import org.testng.annotations.Test;

public class DbTransactionTest extends DbTestCommons {

	@Test
	public void testMultiThreadedTransactionAtomicity() {
		Log.setLogLevel(LogLevel.SEVERE);

		final AtomicInteger n = new AtomicInteger();

		U.benchmarkMT(10, "tx", 10000, new Runnable() {
			@Override
			public void run() {
				DB.transaction(new Runnable() {
					@Override
					public void run() {
						DB.insert(new Person("a", 1));

						if (yesNo()) {
							throw U.rte("tx error");
						}

						DB.insert(new Person("b", 2));

						n.addAndGet(2);
					}
				}, false, null);
			}
		});

		DB.shutdown();

		eq(DB.size(), n.get());
	}

	@Test
	public void testDeleteRollback() {
		Log.setLogLevel(LogLevel.SEVERE);

		try {

			DB.transaction(new Runnable() {
				@Override
				public void run() {
					long id = DB.insert(new Person("a", 1));
					DB.update(id, new Person("b", 2));
					DB.delete(id);
					throw U.rte("tx error");
				}
			}, false);

			fail("Expected exception!");
		} catch (Exception e) {
			DB.shutdown();

			eq(DB.size(), 0);
		}
	}

}
