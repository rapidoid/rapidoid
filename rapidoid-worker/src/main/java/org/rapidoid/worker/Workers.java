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

import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class Workers {

	private static final Map<String, Worker<?, ?>> WORKERS = U.concurrentMap();

	public static <IN, OUT> Worker<IN, OUT> add(String workerId, int inputQueueLimit, int outputQueueLimit,
			Mapper<IN, OUT> mapper) {

		WorkerQueue<IN> input = new WorkerQueue<IN>(U.<IN> queue(inputQueueLimit), inputQueueLimit);
		WorkerQueue<OUT> output = new WorkerQueue<OUT>(U.<OUT> queue(outputQueueLimit), outputQueueLimit);

		WorkerActivity<IN, OUT> worker = new WorkerActivity<IN, OUT>(workerId, input, output, mapper);
		WORKERS.put(workerId, worker);

		return (Worker<IN, OUT>) worker;
	}

	@SuppressWarnings("unchecked")
	public static <IN, OUT> Worker<IN, OUT> get(String workerId) {
		return (WorkerActivity<IN, OUT>) WORKERS.get(workerId);
	}

	public static boolean enqueue(String workerId, Object task, boolean blocking) {
		Worker<Object, Object> worker = get(workerId);
		return worker.enqueue(task, blocking);
	}

	public static int count() {
		return WORKERS.size();
	}

}
