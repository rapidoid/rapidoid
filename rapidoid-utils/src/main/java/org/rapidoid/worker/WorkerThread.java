package org.rapidoid.worker;

import org.rapidoid.lambda.Mapper;
import org.rapidoid.util.U;

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

public class WorkerThread<IN, OUT> extends Thread implements Worker<IN, OUT> {

	private final String id;

	private final WorkerQueue<IN> input;

	private final WorkerQueue<OUT> output;

	private final Mapper<IN, OUT> mapper;

	public WorkerThread(String id, WorkerQueue<IN> input, WorkerQueue<OUT> output, Mapper<IN, OUT> mapper) {
		super("worker-" + id);

		this.id = id;
		this.input = input;
		this.output = output;
		this.mapper = mapper;
	}

	@Override
	public void run() {
		U.info("Starting worker thread...", "id", id);

		while (!Thread.interrupted()) {
			try {

				IN task = input.take();
				U.notNull(task);

				OUT result = U.eval(mapper, task);
				U.notNull(result, "worker mapper result");

				output.put(result);
			} catch (Exception e) {
				U.error("Worker processing error!", "id", id, "error", e);
			}
		}
	}

	@Override
	public boolean enqueue(IN task, boolean blocking) {
		if (blocking) {
			input.put(task);
			return true;
		} else {
			return input.queue.offer(task);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void halt() {
		stop();
		try {
			join();
		} catch (InterruptedException e) {
		}
	}

	@Override
	public int pendingTasksCount() {
		return input.queue.size();
	}

	@Override
	public int pendingResultsCount() {
		return output.queue.size();
	}

}
