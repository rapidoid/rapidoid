package org.rapidoid.db.entity;

import java.util.concurrent.ConcurrentMap;

import org.rapidoid.db.DB;
import org.rapidoid.db.DbTestCommons;
import org.rapidoid.db.impl.DbProxy;
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
		map.put("title", "dsafasfasf");
		map.put("description", "Abc");

		final Task t = DbProxy.create(Task.class, map);

		notNull(t);

		U.benchmarkMT(100, "ops", 1000000, new Runnable() {
			@Override
			public void run() {
				eq(t.id(), map.get("id"));
				eq(t.version(), map.get("version"));

				notNullAll(t.title(), t.description(), t.priority(), t.comments(), t.owner(), t.likedBy());

				eq(t.title().get(), map.get("title"));
				eq(t.description().get(), "Abc");

				isTrue(t.title() == t.title());
				isTrue(t.description() == t.description());
				isTrue(t.priority() == t.priority());
				isTrue(t.comments() == t.comments());
				isTrue(t.owner() == t.owner());
				isTrue(t.likedBy() == t.likedBy());
			}
		});

		DB.shutdown();
	}

}
