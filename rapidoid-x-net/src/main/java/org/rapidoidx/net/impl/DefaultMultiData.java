package org.rapidoidx.net.impl;

/*
 * #%L
 * rapidoid-x-net
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.UTILS;
import org.rapidoidx.buffer.Buf;
import org.rapidoidx.buffer.BufProvider;
import org.rapidoidx.data.Data;
import org.rapidoidx.data.KeyValueRanges;
import org.rapidoidx.data.MultiData;
import org.rapidoidx.data.Range;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
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
