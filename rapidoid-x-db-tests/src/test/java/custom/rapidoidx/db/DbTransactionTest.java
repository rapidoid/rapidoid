package custom.rapidoidx.db;

/*
 * #%L
 * rapidoid-x-db-tests
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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
import org.rapidoidx.db.XDB;
import org.testng.annotations.Test;

import custom.rapidoidx.db.model.Person;

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
				XDB.transaction(new Runnable() {
					@Override
					public void run() {
						XDB.insert(new Person("a", 1));

						if (yesNo()) {
							throw U.rte("tx error");
						}

						XDB.insert(new Person("b", 2));

						n.addAndGet(2);
					}
				}, false, null);
			}
		});

		XDB.shutdown();

		eq(XDB.size(), n.get());
	}

	@Test
	public void testDeleteRollback() {
		Log.setLogLevel(LogLevel.SEVERE);

		try {

			XDB.transaction(new Runnable() {
				@Override
				public void run() {
					long id = XDB.insert(new Person("a", 1));
					XDB.update(id, new Person("b", 2));
					XDB.delete(id);
					throw U.rte("tx error");
				}
			}, false);

			fail("Expected exception!");
		} catch (Exception e) {
			XDB.shutdown();

			eq(XDB.size(), 0);
		}
	}

	@Test
	public void testReadonlyTransaction() {
		throwsRuntimeException(new Runnable() {
			@Override
			public void run() {
				XDB.transaction(new Runnable() {
					@Override
					public void run() {
						XDB.insert(new Person("a", 1));
					}
				}, true);
			}
		}, "read-only transaction");

		final long id = XDB.insert(new Person("a", 1));

		throwsRuntimeException(new Runnable() {
			@Override
			public void run() {
				XDB.transaction(new Runnable() {
					@Override
					public void run() {
						XDB.delete(id);
					}
				}, true);
			}
		}, "read-only transaction");

		throwsRuntimeException(new Runnable() {
			@Override
			public void run() {
				XDB.transaction(new Runnable() {
					@Override
					public void run() {
						XDB.update(id, new Person());
					}
				}, true);
			}
		}, "read-only transaction");

		XDB.transaction(new Runnable() {
			@Override
			public void run() {
				Object p = XDB.get(id);
				XDB.refresh(p);
				XDB.getAll(Person.class);
				XDB.getAll(id);
				XDB.find(new Predicate<Person>() {
					@Override
					public boolean eval(Person p) throws Exception {
						return true;
					}
				});
			}
		}, true);

	}

}
