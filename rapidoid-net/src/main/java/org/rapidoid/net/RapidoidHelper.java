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
import org.rapidoid.data.Range;
import org.rapidoid.pool.ArrayPool;
import org.rapidoid.pool.Pool;
import org.rapidoid.util.U;

public class RapidoidHelper {

	public final byte[] bytes = new byte[100 * 1024];

	public final KeyValueRanges pairs = new KeyValueRanges(1000);

	public final Range[] ranges = new Range[1000];

	private final Pool<?> pool;

	private final Object exchange;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public RapidoidHelper(final Class<?> exchangeClass) {
		for (int i = 0; i < ranges.length; i++) {
			ranges[i] = new Range();
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

	public Pool<?> pool() {
		return pool;
	}

	public Object exchange() {
		return exchange;
	}

}
