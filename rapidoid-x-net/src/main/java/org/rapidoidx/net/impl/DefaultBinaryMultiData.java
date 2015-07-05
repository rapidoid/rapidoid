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
import org.rapidoidx.buffer.Buf;
import org.rapidoidx.buffer.BufProvider;
import org.rapidoidx.data.BinaryMultiData;
import org.rapidoidx.data.KeyValueRanges;
import org.rapidoidx.data.Range;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DefaultBinaryMultiData implements BinaryMultiData {

	private final BufProvider src;

	private final KeyValueRanges ranges;

	public DefaultBinaryMultiData(BufProvider src, KeyValueRanges ranges) {
		this.src = src;
		this.ranges = ranges;
	}

	@Override
	public Map<String, byte[]> get() {
		return ranges.toBinaryMap(src.buffer(), true);
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
		Buf buf = src.buffer();
		Range range = ranges.get(buf, name.getBytes(), false);
		return range != null ? range.bytes(buf) : null;
	}

}
