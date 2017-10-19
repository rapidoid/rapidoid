package org.rapidoid.commons;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

/*
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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
@Since("5.1.0")
public class Stats extends RapidoidThing {

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
