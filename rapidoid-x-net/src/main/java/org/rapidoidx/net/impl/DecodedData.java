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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.UTILS;
import org.rapidoidx.buffer.BufProvider;
import org.rapidoidx.data.Data;
import org.rapidoidx.data.Range;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DecodedData implements Data {

	private final BufProvider src;

	private final Range range;

	public DecodedData(BufProvider src, Range range) {
		this.src = src;
		this.range = range;
	}

	@Override
	public String get() {
		return !range.isEmpty() ? UTILS.urlDecode(src.buffer().get(range)) : "";
	}

	@Override
	public Range range() {
		return range;
	}

}
