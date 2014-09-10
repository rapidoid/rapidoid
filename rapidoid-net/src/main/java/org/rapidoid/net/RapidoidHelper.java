package org.rapidoid.net;

/*
 * #%L
 * rapidoid-net
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import java.util.concurrent.Callable;

import org.rapidoid.data.KeyValueRanges;
import org.rapidoid.data.Ranges;
import org.rapidoid.pool.ArrayPool;
import org.rapidoid.pool.Pool;
import org.rapidoid.util.U;
import org.rapidoid.wrap.Bool;
import org.rapidoid.wrap.Chr;
import org.rapidoid.wrap.Dbl;
import org.rapidoid.wrap.Flt;
import org.rapidoid.wrap.Int;
import org.rapidoid.wrap.Lng;
import org.rapidoid.wrap.Shrt;

/**
 * Helpers are instantiated per worker node (for thread-safe use), so they
 * contain various data structures that can be used as temporary data holders
 * when implementing protocols, to avoid instantiating objects for each protocol
 * execution.
 */
public class RapidoidHelper {

	public final byte[] bytes = new byte[100 * 1024];

	public final KeyValueRanges pairs = new KeyValueRanges(100);

	public final Ranges ranges1 = new Ranges(100);

	public final Ranges ranges2 = new Ranges(100);

	public final Ranges ranges3 = new Ranges(100);

	public final Ranges ranges4 = new Ranges(100);

	public final Ranges ranges5 = new Ranges(100);

	public final Bool[] booleans = new Bool[100];

	public final Shrt[] shorts = new Shrt[100];

	public final Chr[] chars = new Chr[100];

	public final Int[] integers = new Int[100];

	public final Lng[] longs = new Lng[100];

	public final Flt[] floats = new Flt[100];

	public final Dbl[] doubles = new Dbl[100];

	public final Pool<?> pool;

	public final Object exchange;

	public RapidoidHelper() {
		this(null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public RapidoidHelper(final Class<?> exchangeClass) {

		for (int i = 0; i < booleans.length; i++) {
			booleans[i] = new Bool();
		}

		for (int i = 0; i < shorts.length; i++) {
			shorts[i] = new Shrt();
		}

		for (int i = 0; i < chars.length; i++) {
			chars[i] = new Chr();
		}

		for (int i = 0; i < integers.length; i++) {
			integers[i] = new Int();
		}

		for (int i = 0; i < longs.length; i++) {
			longs[i] = new Lng();
		}

		for (int i = 0; i < floats.length; i++) {
			floats[i] = new Flt();
		}

		for (int i = 0; i < doubles.length; i++) {
			doubles[i] = new Dbl();
		}

		if (exchangeClass != null) {
			exchange = U.newInstance(exchangeClass);
			pool = new ArrayPool(new Callable() {
				@Override
				public Object call() throws Exception {
					return U.newInstance(exchangeClass);
				}
			}, 1000);
		} else {
			exchange = null;
			pool = null;
		}
	}

}
