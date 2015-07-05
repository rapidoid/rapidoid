package org.rapidoidx.worker;

/*
 * #%L
 * rapidoid-x-worker
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.test.TestCommons;
import org.rapidoid.util.UTILS;
import org.junit.Test;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class WorkerTest extends TestCommons {

	@Test
	public void shouldRunWorkers() {

		Worker<String, Integer> wrk1 = Workers.add("wrk1", 0, 0, new Mapper<String, Integer>() {
			@Override
			public Integer map(String x) throws Exception {
				return x.length();
			}
		});

		Worker<Integer, String> wrk2 = Workers.add("wrk2", 2, 1, new Mapper<Integer, String>() {
			@Override
			public String map(Integer x) throws Exception {
				return x + "";
			}
		});

		eq(Workers.get("wrk1"), wrk1);
		eq(Workers.get("wrk2"), wrk2);

		isTrue(Workers.enqueue("wrk1", "abc", false));
		isTrue(wrk1.enqueue("xy", false));
		isTrue(Workers.enqueue("wrk1", "xy", false));

		isTrue(Workers.enqueue("wrk2", 123, false));
		isTrue(wrk2.enqueue(111, false));

		isFalse(wrk2.enqueue(77, false));
		isFalse(Workers.enqueue("wrk2", 56789, false));

		eq(wrk1.pendingTasksCount(), 3);
		eq(wrk2.pendingTasksCount(), 2);

		eq(wrk1.pendingResultsCount(), 0);
		eq(wrk2.pendingResultsCount(), 0);

		wrk1.start();
		wrk2.start();

		UTILS.sleep(1000);

		eq(wrk1.pendingTasksCount(), 0);
		eq(wrk2.pendingTasksCount(), 0);

		eq(wrk1.pendingResultsCount(), 3);
		eq(wrk2.pendingResultsCount(), 1);

		eq(wrk1.nextResult(false).intValue(), 3);
		eq(wrk1.nextResult(false).intValue(), 2);

		isTrue(wrk1.enqueue("aaaaa", false));
		isTrue(wrk2.enqueue(77, false));

		UTILS.sleep(1000);

		wrk1.halt();
		wrk2.shutdown();

		eq(wrk1.pendingTasksCount(), 0);
		eq(wrk2.pendingTasksCount(), 1);

		eq(wrk1.pendingResultsCount(), 2);
		eq(wrk2.pendingResultsCount(), 1);
	}

}
