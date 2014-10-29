package org.rapidoid.worker;

import org.rapidoid.activity.AbstractActivity;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;

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

public class WorkerImpl<IN, OUT> extends AbstractActivity<Worker<IN, OUT>> implements Worker<IN, OUT>, Runnable {

	private final String id;

	private final WorkerQueue<IN> input;

	private final WorkerQueue<OUT> output;

	private final Mapper<IN, OUT> mapper;

	private final Thread thread;

	public WorkerImpl(String id, WorkerQueue<IN> input, WorkerQueue<OUT> output, Mapper<IN, OUT> mapper) {
		super("worker-" + id);

		this.id = id;
		this.input = input;
		this.output = output;
		this.mapper = mapper;
		this.thread = new Thread(this, name);
	}

	@Override
	public void run() {
		U.info("Starting worker thread...", "id", id);

		while (!Thread.interrupted()) {
			try {

				IN task = input.take();
				U.notNull(task);

				OUT result = UTILS.eval(mapper, task);
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

	@Override
	public Worker<IN, OUT> start() {
		thread.start();
		return super.start();
	}

	@SuppressWarnings("deprecation")
	@Override
	public Worker<IN, OUT> halt() {
		thread.stop();
		try {
			thread.join();
		} catch (InterruptedException e) {
		}

		return super.halt();
	}

	@Override
	public int pendingTasksCount() {
		return input.queue.size();
	}

	@Override
	public int pendingResultsCount() {
		return output.queue.size();
	}

	@Override
	public OUT nextResult(boolean blocking) {
		if (blocking) {
			return output.take();
		} else {
			return output.queue.poll();
		}
	}

}
