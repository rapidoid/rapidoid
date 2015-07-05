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

import java.util.Queue;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.UTILS;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class WorkerQueue<T> {

	final Queue<T> queue;

	final int limit;

	public WorkerQueue(Queue<T> queue, int limit) {
		this.queue = queue;
		this.limit = limit;
	}

	public boolean canGet() {
		return queue.peek() != null;
	}

	public boolean canPut() {
		return limit <= 0 || queue.size() < limit;
	}

	public T take() {
		T item;

		while ((item = queue.poll()) == null) {
			UTILS.sleep(100);
		}

		return item;
	}

	public void put(T item) {
		while (!queue.offer(item)) {
			UTILS.sleep(100);
		}
	}

}
