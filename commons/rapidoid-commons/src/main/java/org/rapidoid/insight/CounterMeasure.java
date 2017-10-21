package org.rapidoid.insight;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.util.concurrent.atomic.AtomicInteger;

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
public class CounterMeasure extends RapidoidThing implements Measure {

	private AtomicInteger counter = new AtomicInteger();

	@Override
	public void reset() {
		counter.set(0);
	}

	@Override
	public String get() {
		return counter.get() + "";
	}

	public void increment() {
		counter.incrementAndGet();
	}

	public void add(int delta) {
		counter.addAndGet(delta);
	}

}
