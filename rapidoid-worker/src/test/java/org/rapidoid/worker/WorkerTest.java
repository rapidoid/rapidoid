package org.rapidoid.worker;

/*
 * #%L
 * rapidoid-worker
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.test.TestCommons;
import org.rapidoid.util.UTILS;
import org.testng.annotations.Test;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
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
