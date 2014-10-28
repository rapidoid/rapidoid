package org.rapidoid.worker;

import java.util.Queue;

import org.rapidoid.util.U;

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
			U.sleep(100);
		}

		return item;
	}

	public void put(T item) {
		while (!queue.offer(item)) {
			U.sleep(100);
		}
	}

}
