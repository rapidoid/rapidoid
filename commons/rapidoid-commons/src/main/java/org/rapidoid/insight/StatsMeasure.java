package org.rapidoid.insight;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.util.concurrent.atomic.AtomicLong;

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
@Since("2.0.0")
public class StatsMeasure extends RapidoidThing implements Measure {

	private long min;
	private long max;
	private long sum = 0;
	private long count = 0;

	private final AtomicLong ticks = new AtomicLong();

	@Override
	public synchronized String get() {
		return count > 0 ? String.format("%s:[%s..%s..%s]#%s", sum, min, sum / count, max, count) : "" + ticks;
	}

	@Override
	public synchronized void reset() {
		min = 0;
		max = 0;
		sum = 0;
		count = 0;
		ticks.set(0);
	}

	public void tick() {
		ticks.incrementAndGet();
	}

	public synchronized void value(long value) {
		if (count == 0 || min > value) {
			min = value;
		}

		if (count == 0 || max < value) {
			max = value;
		}

		count++;
		sum += value;
	}

	@Override
	public String toString() {
		return get();
	}

}
