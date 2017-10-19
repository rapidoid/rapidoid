package org.rapidoid.net.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.BufProvider;
import org.rapidoid.data.BinaryMultiData;
import org.rapidoid.data.KeyValueRanges;

import java.util.Map;

/*
 * #%L
 * rapidoid-net
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
public class DefaultBinaryMultiData extends RapidoidThing implements BinaryMultiData {

	private final BufProvider src;

	private final KeyValueRanges ranges;

	private Map<String, byte[]> values;

	public DefaultBinaryMultiData(BufProvider src, KeyValueRanges ranges) {
		this.src = src;
		this.ranges = ranges;
	}

	@Override
	public synchronized Map<String, byte[]> get() {
		if (values == null) {
			values = ranges.toBinaryMap(src.buffer(), true);
		}

		return values;
	}

	@Override
	public KeyValueRanges ranges() {
		return ranges;
	}

	@Override
	public String toString() {
		return "BinaryMultiData [ranges=" + ranges + "]";
	}

	@Override
	public byte[] get(String name) {
		return get().get(name);
	}

	@Override
	public synchronized void reset() {
		values = null;
	}

}
