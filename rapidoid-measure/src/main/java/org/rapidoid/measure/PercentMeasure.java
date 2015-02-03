package org.rapidoid.measure;

/*
 * #%L
 * rapidoid-measure
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

import java.util.concurrent.atomic.AtomicInteger;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class PercentMeasure implements Measure {

	private final AtomicInteger total = new AtomicInteger();

	private final AtomicInteger hits = new AtomicInteger();

	public synchronized void reset() {
		total.set(0);
		hits.set(0);
	}

	@Override
	public synchronized String get() {
		int t = total.getAndSet(0);
		int h = hits.getAndSet(0) * 100;
		return t > 0 ? h / t + "%(" + t + ")" : null;
	}

	public void increment() {
		total.incrementAndGet();
	}

	public void hit() {
		hits.incrementAndGet();
	}

}
