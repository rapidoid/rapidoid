package org.rapidoidx.db;

/*
 * #%L
 * rapidoid-x-db-tests
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.util.concurrent.atomic.AtomicInteger;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.log.Log;
import org.rapidoid.log.LogLevel;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;
import org.rapidoidx.db.DB;
import org.rapidoidx.db.model.Person;
import org.testng.annotations.Test;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
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
