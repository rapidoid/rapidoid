package org.rapidoid.worker;

import org.rapidoid.activity.AbstractThreadActivity;
import org.rapidoid.annotation.Authors;
import org.rapidoid.lambda.Lambdas;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.util.U;

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

@Authors("Nikolche Mihajlovski")
public class WorkerActivity<IN, OUT> extends AbstractThreadActivity<Worker<IN, OUT>> implements Worker<IN, OUT> {

	private final WorkerQueue<IN> input;

	private final WorkerQueue<OUT> output;

	private final Mapper<IN, OUT> mapper;

	public WorkerActivity(String id, WorkerQueue<IN> input, WorkerQueue<OUT> output, Mapper<IN, OUT> mapper) {
		super("worker-" + id);

		this.input = input;
		this.output = output;
		this.mapper = mapper;
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

	@Override
	protected void loop() {
		IN task = input.take();
		U.notNullAll(task);

		OUT result = Lambdas.eval(mapper, task);
		U.notNull(result, "worker mapper result");

		output.put(result);
	}

}
