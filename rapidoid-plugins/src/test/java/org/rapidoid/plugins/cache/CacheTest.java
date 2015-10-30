package org.rapidoid.plugins.cache;

/*
 * #%L
 * rapidoid-plugins
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import java.util.concurrent.TimeoutException;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.concurrent.Future;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class CacheTest extends TestCommons {

	/**
	 * The default cache implementation is {@link NoCache}.
	 */
	@Test
	public void showCacheAPI() {

		ICache<String, Object> cache = Cache.create("testcache", 1000, true);

		cache.set("key1", U.set(1, 2, 3), 1000);

		cache.set("key2", "abc", 1500, new Callback<Void>() {
			@Override
			public void onDone(Void result, Throwable error) throws Exception {}
		});

		cache.get("key1", new Callback<Object>() {
			@Override
			public void onDone(Object result, Throwable error) throws Exception {}
		});

		Future<Object> s = cache.get("key2");
		isNull(s.get());

		try {
			s.get(100);
		} catch (TimeoutException e) {}

		Future<Void> set = cache.set("abcd", U.map("aa", true), 5000);
		set.get();
	}

}
