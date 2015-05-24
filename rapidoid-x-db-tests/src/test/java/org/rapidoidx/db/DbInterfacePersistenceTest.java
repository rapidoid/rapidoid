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
import org.rapidoid.config.Conf;
import org.rapidoid.util.OptimisticConcurrencyControlException;
import org.rapidoid.util.Rnd;
import org.rapidoid.util.UTILS;
import org.rapidoidx.db.model.IPerson;
import org.testng.annotations.Test;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DbInterfacePersistenceTest extends DbTestCommons {

	@Test
	public void testPersistence() {

		final int count = 10000;

		System.out.println("inserting...");

		UTILS.startMeasure();

		UTILS.benchmarkMT(Conf.cpus(), "insert", count, new Runnable() {
			@Override
			public void run() {
				XDB.insert(XDB.entity(IPerson.class, "name", "abc", "age", -1));
			}
		});

		System.out.println("updating...");

		final AtomicInteger occN = new AtomicInteger();

		UTILS.benchmarkMT(10, "update", count, new Runnable() {
			@Override
			public void run() {
				int id = Rnd.rnd(count) + 1;
				Long version = XDB.getVersionOf(id);
				IPerson person = XDB.entity(IPerson.class, "version", version, "name", "x", "age", id * 100);
				try {
					XDB.update(id, person);
				} catch (OptimisticConcurrencyControlException e) {
					eq(e.getRecordId(), id);
					occN.incrementAndGet();
				}
			}
		});

		System.out.println("Total OCC exceptions: " + occN.get());
		isTrue(occN.get() < count / 10);

		XDB.shutdown();
		XDB.start();

		checkDb(count);
		checkDb(count);
		checkDb(count);
	}

	private void checkDb(final int count) {
		UTILS.endMeasure("total");

		eq(XDB.size(), count);

		for (int id = 1; id <= count; id++) {
			IPerson p = XDB.get(id);
			eq(p.id(), id);
			Integer age = p.age().get();
			String name = p.name().get();
			isTrue((name.equals("abc") && age == -1) || (name.equals("x") && age == id * 100));
		}

		XDB.shutdown();
	}

}
