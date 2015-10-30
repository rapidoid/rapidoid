package org.rapidoidx.worker;

import org.rapidoid.activity.AbstractThreadActivity;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.Lambdas;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.u.U;

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

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
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
