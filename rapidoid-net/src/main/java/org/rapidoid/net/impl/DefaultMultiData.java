package org.rapidoid.net.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.buffer.BufProvider;
import org.rapidoid.data.BufRange;
import org.rapidoid.data.Data;
import org.rapidoid.data.KeyValueRanges;
import org.rapidoid.data.MultiData;

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
public class DefaultMultiData extends RapidoidThing implements MultiData {

	private final BufProvider src;

	private final KeyValueRanges ranges;

	private Map<String, String> values;

	public DefaultMultiData(BufProvider src, KeyValueRanges ranges) {
		this.src = src;
		this.ranges = ranges;
	}

	@Override
	public synchronized Map<String, String> get() {
		if (values == null) {
			values = ranges.toMap(src.buffer(), true, true, false);
		}

		return values;
	}

	@Override
	public KeyValueRanges ranges() {
		return ranges;
	}

	@Override
	public String toString() {
		return "MultiData [ranges=" + ranges + "]";
	}

	@Override
	public String get(String name) {
		Data data = get_(name);
		return data != null ? data.get() : null;
	}

	@Override
	public Data get_(String name) {
		Buf buf = src.buffer();
		BufRange range = ranges.get(buf, name.getBytes(), false);
		return range != null ? new DecodedData(src, range) : null;
	}

	@Override
	public synchronized void reset() {
		values = null;
	}

	@Override
	public void putExtras(Map<String, String> extras) {
		get().putAll(extras);
	}

}
