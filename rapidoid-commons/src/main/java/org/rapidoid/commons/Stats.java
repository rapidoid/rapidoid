package org.rapidoid.commons;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class Stats {

	private volatile int count;
	private volatile double min;
	private volatile double max;
	private volatile double sum;

	public synchronized void add(double value) {
		min = count > 0 ? Math.min(min, value) : value;
		max = count > 0 ? Math.max(max, value) : value;
		sum += value;
		count++;
	}

	public double min() {
		return min;
	}

	public double max() {
		return max;
	}

	public double sum() {
		return sum;
	}

	public int count() {
		return count;
	}

	public double avg() {
		return count > 0 ? sum / count : 0;
	}

	@Override
	public synchronized String toString() {
		return "Stats{" +
				"count=" + count +
				", min=" + min +
				", max=" + max +
				", sum=" + sum +
				", avg=" + avg() +
				'}';
	}
}
