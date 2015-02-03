package org.rapidoid.net.impl;

/*
 * #%L
 * rapidoid-net
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

import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.buffer.BufProvider;
import org.rapidoid.data.Data;
import org.rapidoid.data.KeyValueRanges;
import org.rapidoid.data.MultiData;
import org.rapidoid.data.Range;
import org.rapidoid.util.UTILS;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class DefaultMultiData implements MultiData {

	private final BufProvider src;

	private final KeyValueRanges ranges;

	public DefaultMultiData(BufProvider src, KeyValueRanges ranges) {
		this.src = src;
		this.ranges = ranges;
	}

	@Override
	public Map<String, String> get() {
		return ranges.toMap(src.buffer(), true, true);
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
		Buf buf = src.buffer();
		Range range = ranges.get(buf, name.getBytes(), false);
		return range != null ? UTILS.urlDecode(range.str(buf)) : null;
	}

	@Override
	public Data get_(String name) {
		Buf buf = src.buffer();
		Range range = ranges.get(buf, name.getBytes(), false);
		return range != null ? new DecodedData(src, range) : null;
	}

}
