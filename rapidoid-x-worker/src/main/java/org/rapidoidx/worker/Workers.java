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

import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
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
