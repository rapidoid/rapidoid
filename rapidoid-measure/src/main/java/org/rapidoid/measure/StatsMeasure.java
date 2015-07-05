package org.rapidoid.measure;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

/*
 * #%L
 * rapidoid-measure
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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
public class StatsMeasure implements Measure {

	private long min = Long.MAX_VALUE;
	private long max = Long.MIN_VALUE;
	private long sum = 0;
	private long count = 0;

	@Override
	public String get() {
		return count > 0 ? String.format("[%s..%s..%s]/%s", min, sum / count, max, count) : null;
	}

	@Override
	public void reset() {
		min = Long.MAX_VALUE;
		max = Long.MIN_VALUE;
		sum = 0;
		count = 0;
	}

	public synchronized void add(long value) {
		if (min > value) {
			min = value;
		}

		if (max < value) {
			max = value;
		}

		count++;
		sum += value;
	}

}
