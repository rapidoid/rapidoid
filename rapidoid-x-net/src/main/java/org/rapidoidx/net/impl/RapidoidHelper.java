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

import java.util.concurrent.Callable;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.pool.Pool;
import org.rapidoid.pool.Pools;
import org.rapidoid.wrap.BoolWrap;
import org.rapidoid.wrap.CharWrap;
import org.rapidoid.wrap.DoubleWrap;
import org.rapidoid.wrap.FloatWrap;
import org.rapidoid.wrap.IntWrap;
import org.rapidoid.wrap.LongWrap;
import org.rapidoid.wrap.ShortWrap;
import org.rapidoidx.data.KeyValueRanges;
import org.rapidoidx.data.Ranges;

/**
 * Helpers are instantiated per worker node (for thread-safe use), so they contain various data structures that can be
 * used as temporary data holders when implementing protocols, to avoid instantiating objects for each protocol
 * execution.
 */
@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class RapidoidHelper {

	public final byte[] bytes = new byte[100 * 1024];

	public final KeyValueRanges pairs = new KeyValueRanges(100);

	public final Ranges ranges1 = new Ranges(100);

	public final Ranges ranges2 = new Ranges(100);

	public final Ranges ranges3 = new Ranges(100);

	public final Ranges ranges4 = new Ranges(100);

	public final Ranges ranges5 = new Ranges(100);

	public final BoolWrap[] booleans = new BoolWrap[100];

	public final ShortWrap[] shorts = new ShortWrap[100];

	public final CharWrap[] chars = new CharWrap[100];

	public final IntWrap[] integers = new IntWrap[100];

	public final LongWrap[] longs = new LongWrap[100];

	public final FloatWrap[] floats = new FloatWrap[100];

	public final DoubleWrap[] doubles = new DoubleWrap[100];

	public final Pool<?> pool;

	public final Object exchange;

	public RapidoidHelper() {
		this(null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public RapidoidHelper(final Class<?> exchangeClass) {

		for (int i = 0; i < booleans.length; i++) {
			booleans[i] = new BoolWrap();
		}

		for (int i = 0; i < shorts.length; i++) {
			shorts[i] = new ShortWrap();
		}

		for (int i = 0; i < chars.length; i++) {
			chars[i] = new CharWrap();
		}

		for (int i = 0; i < integers.length; i++) {
			integers[i] = new IntWrap();
		}

		for (int i = 0; i < longs.length; i++) {
			longs[i] = new LongWrap();
		}

		for (int i = 0; i < floats.length; i++) {
			floats[i] = new FloatWrap();
		}

		for (int i = 0; i < doubles.length; i++) {
			doubles[i] = new DoubleWrap();
		}

		if (exchangeClass != null) {
			exchange = Cls.newInstance(exchangeClass);
			pool = Pools.create(new Callable() {
				@Override
				public Object call() throws Exception {
					return Cls.newInstance(exchangeClass);
				}
			}, 1000);
		} else {
			exchange = null;
			pool = null;
		}
	}

}
