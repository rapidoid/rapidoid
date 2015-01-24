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

import java.util.concurrent.CountDownLatch;

import org.rapidoid.config.Conf;
import org.rapidoid.db.model.Person;
import org.rapidoid.util.U;
import org.testng.annotations.Test;

public class DbClassPersistenceTest extends DbTestCommons {

	@Test
	public void testPersistence() {

		final int count = 10000;

		System.out.println("inserting...");

		U.startMeasure();

		U.benchmarkMT(Conf.cpus(), "insert", count, new Runnable() {
			@Override
			public void run() {
				DB.insert(new Person("abc", -1));
			}
		});

		System.out.println("updating...");

		final CountDownLatch latch = new CountDownLatch(count);

		U.benchmarkMT(10, "update", count, latch, new Runnable() {
			@Override
			public void run() {
				int id = U.rnd(count) + 1;
				DB.update(id, new Person("x", id * 100));
				latch.countDown();
			}
		});

		DB.shutdown();
		DB.init();

		checkDb(count);
		checkDb(count);
		checkDb(count);
	}

	private void checkDb(final int count) {
		U.endMeasure("total");

		eq(DB.size(), count);

		for (int id = 1; id <= count; id++) {
			Person p = DB.get(id);
			isTrue(p.id == id);
			isTrue((p.name.equals("abc") && p.age == -1) || (p.name.equals("x") && p.age == id * 100));
		}

		DB.shutdown();
	}

}
