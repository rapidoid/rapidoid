package org.rapidoid.db;

import java.util.concurrent.ConcurrentMap;

import org.rapidoid.db.impl.DbProxy;
import org.rapidoid.db.model.IPost;
import org.rapidoid.util.U;
import org.testng.annotations.Test;

/*
 * #%L
 * rapidoid-db-tests
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

public class DbProxyTest extends DbTestCommons {

	@Test
	public void testDbProxy() {

		final ConcurrentMap<String, Object> map = U.concurrentMap();
		map.put("id", 1234567890123L);
		map.put("version", 346578789843490123L);
		map.put("content", "dsafasfasf");

		final IPost t = DbProxy.create(IPost.class, map);
		notNull(t);

		U.benchmarkMT(100, "ops", 1000000, new Runnable() {
			@Override
			public void run() {
				check(map, t);
			}
		});

		DB.shutdown();
	}

	private void check(final ConcurrentMap<String, Object> map, final IPost p) {
		notNullAll(p.content(), p.likes(), p.id(), p.version());

		isTrue(p.id() == p.id());
		isTrue(p.version() == p.version());
		isTrue(p.content() == p.content());
		isTrue(p.likes() == p.likes());

		eq(p.id().get(), map.get("id"));
		eq(p.version().get(), map.get("version"));
		eq(p.content().get(), map.get("content"));
	}

}
