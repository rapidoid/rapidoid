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

import org.rapidoid.annotation.Authors;
import org.rapidoid.db.model.Person;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.log.Log;
import org.rapidoid.log.LogLevel;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;
import org.testng.annotations.Test;

@Authors("Nikolche Mihajlovski")
public class DbTransactionTest extends DbTestCommons {

	@Test
	public void testMultiThreadedTransactionAtomicity() {
		Log.setLogLevel(LogLevel.SEVERE);

		final AtomicInteger n = new AtomicInteger();

		UTILS.benchmarkMT(10, "tx", 10000, new Runnable() {
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

	@Test
	public void testReadonlyTransaction() {
		throwsRuntimeException(new Runnable() {
			@Override
			public void run() {
				DB.transaction(new Runnable() {
					@Override
					public void run() {
						DB.insert(new Person("a", 1));
					}
				}, true);
			}
		}, "read-only transaction");

		final long id = DB.insert(new Person("a", 1));

		throwsRuntimeException(new Runnable() {
			@Override
			public void run() {
				DB.transaction(new Runnable() {
					@Override
					public void run() {
						DB.delete(id);
					}
				}, true);
			}
		}, "read-only transaction");

		throwsRuntimeException(new Runnable() {
			@Override
			public void run() {
				DB.transaction(new Runnable() {
					@Override
					public void run() {
						DB.update(id, new Person());
					}
				}, true);
			}
		}, "read-only transaction");

		DB.transaction(new Runnable() {
			@Override
			public void run() {
				Object p = DB.get(id);
				DB.refresh(p);
				DB.getAll(Person.class);
				DB.getAll(id);
				DB.find(new Predicate<Person>() {
					@Override
					public boolean eval(Person p) throws Exception {
						return true;
					}
				});
			}
		}, true);

	}

}
