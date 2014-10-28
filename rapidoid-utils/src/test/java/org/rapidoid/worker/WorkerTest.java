package org.rapidoid.worker;

/*
 * #%L
 * rapidoid-utils
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import org.rapidoid.lambda.Mapper;
import org.rapidoid.test.TestCommons;
import org.testng.annotations.Test;

public class WorkerTest extends TestCommons {

	@Test
	public void shouldRunWorkers() {

		Worker<String, Integer> wrk1 = Workers.add("wrk1", 0, 0, new Mapper<String, Integer>() {
			@Override
			public Integer map(String x) throws Exception {
				return x.length();
			}
		});

		Worker<Integer, String> wrk2 = Workers.add("wrk2", 1, 2, new Mapper<Integer, String>() {
			@Override
			public String map(Integer x) throws Exception {
				return x + "";
			}
		});

		eq(Workers.get("wrk1"), wrk1);
		eq(Workers.get("wrk2"), wrk2);

		isTrue(Workers.enqueue("wrk1", "abc"));
		isTrue(wrk1.enqueue("xy"));
		isTrue(Workers.enqueue("wrk1", "xy"));

		isTrue(Workers.enqueue("wrk2", 123));
		isFalse(wrk2.enqueue(77));
		isFalse(Workers.enqueue("wrk2", 56789));
	}

}
