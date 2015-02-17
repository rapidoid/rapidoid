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
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.db.model.Person;
import org.rapidoid.util.OptimisticConcurrencyControlException;
import org.rapidoid.util.Rnd;
import org.rapidoid.util.UTILS;
import org.testng.annotations.Test;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class DbClassPersistenceTest extends DbTestCommons {

	@Test
	public void testPersistence() {

		final int count = 10000;

		System.out.println("inserting...");

		UTILS.startMeasure();

		UTILS.benchmarkMT(Conf.cpus(), "insert", count, new Runnable() {
			@Override
			public void run() {
				DB.insert(new Person("abc", -1));
			}
		});

		System.out.println("updating...");

		final AtomicInteger occN = new AtomicInteger();

		UTILS.benchmarkMT(10, "update", count, new Runnable() {
			@Override
			public void run() {
				int id = Rnd.rnd(count) + 1;
				Person person = new Person("x", id * 100);
				person.version(DB.getVersionOf(id));

				try {
					DB.update(id, person);
				} catch (OptimisticConcurrencyControlException e) {
					eq(e.getRecordId(), id);
					occN.incrementAndGet();
				}
			}
		});

		System.out.println("Total OCC exceptions: " + occN.get());
		isTrue(occN.get() < count / 10);

		DB.shutdown();
		DB.init();

		checkDb(count);
		checkDb(count);
		checkDb(count);
	}

	private void checkDb(final int count) {
		UTILS.endMeasure("total");

		eq(DB.size(), count);

		for (int id = 1; id <= count; id++) {
			Person p = DB.get(id);
			isTrue(p.id() == id);
			isTrue((p.name.equals("abc") && p.age == -1) || (p.name.equals("x") && p.age == id * 100));
		}

		DB.shutdown();
	}

}
