package org.rapidoid.commons;

/*
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

import java.util.Collections;
import java.util.NavigableMap;
import java.util.TreeMap;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class TimeSeries {

	private final NavigableMap<Long, Double> values = Collections.synchronizedNavigableMap(new TreeMap<Long, Double>());

	public void put(long timestamp, Number value) {
		values.put(timestamp, value.doubleValue());
	}

	public NavigableMap<Long, Double> values() {
		return values;
	}

	@Override
	public String toString() {
		return U.frmt("TimeSerie(%s values)", values.size());
	}

}
