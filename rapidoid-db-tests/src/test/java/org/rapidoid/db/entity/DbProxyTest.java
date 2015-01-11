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

		final Task task = DbProxy.create(Task.class, map);

		notNull(task);

		U.benchmarkMT(100, "ops", 1000000, new Runnable() {
			@Override
			public void run() {
				eq(task.id(), map.get("id"));
				eq(task.version(), map.get("version"));
				eq(task.title().get(), map.get("title"));
				eq(task.description().get(), "Abc");

				isTrue(task.title() == task.title());
				isTrue(task.description() == task.description());
				isTrue(task.priority() == task.priority());
				isTrue(task.comments() == task.comments());
				isTrue(task.owner() == task.owner());
				isTrue(task.likedBy() == task.likedBy());
			}
		});

		DB.shutdown();
	}

}
