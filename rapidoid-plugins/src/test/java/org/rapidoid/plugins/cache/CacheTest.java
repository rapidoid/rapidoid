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

import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.junit.Test;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.concurrent.Future;
import org.rapidoid.plugins.cache.Cache;
import org.rapidoid.util.U;

public class CacheTest {

	/*
	 * Just a demo of the API.
	 */
	@SuppressWarnings("unused")
	@Test(expected = AbstractMethodError.class)
	public void remoteCacheAPIDemo() {

		Cache.set("key1", U.set(1, 2, 3), 1000);

		Cache.set("key2", "abc", 1500, new Callback<Void>() {
			@Override
			public void onDone(Void result, Throwable error) throws Exception {}
		});

		Cache.get("key1", new Callback<Set<Integer>>() {
			@Override
			public void onDone(Set<Integer> result, Throwable error) throws Exception {}
		});

		Future<String> s = Cache.get("key2");

		String ss = s.get();

		try {
			s.get(100);
		} catch (TimeoutException e) {}

		Future<Void> set = Cache.set(new Integer(123), U.map("aa", true), 5000);
		set.get();
	}

}
