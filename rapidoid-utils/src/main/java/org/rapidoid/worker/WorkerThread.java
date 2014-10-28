package org.rapidoid.worker;

import java.util.Queue;

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

	private final Queue<IN> input;

	private final Queue<OUT> output;

	private final Mapper<IN, OUT> mapper;

	public WorkerThread(String id, Queue<IN> input, Queue<OUT> output, Mapper<IN, OUT> mapper) {
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
			IN task = input.poll();
			if (task != null) {
				try {
					OUT result = mapper.map(task);
					output.add(result);
				} catch (Exception e) {
					U.warn("Worker error", "id", id, "error", e);
				}
			}
		}
	}

	@Override
	public boolean enqueue(IN task) {
		try {
			return input.add(task);
		} catch (Exception e) {
			return false;
		}
	}

}
