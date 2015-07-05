package custom.rapidoidx.db;

import java.util.concurrent.ConcurrentMap;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.U;
import org.rapidoidx.db.XDB;
import org.rapidoidx.db.impl.DbProxy;
import org.testng.annotations.Test;

import custom.rapidoidx.db.model.IPost;

/*
 * #%L
 * rapidoid-x-db-tests
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

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DbProxyTest extends DbTestCommons {

	@Test
	public void testDbProxy() {

		final ConcurrentMap<String, Object> map = U.concurrentMap();
		map.put("id", "1234567890123");
		map.put("version", "346578789843490123");
		map.put("content", "dsafasfasf");

		final IPost t = DbProxy.create(IPost.class, map);
		notNull(t);

		multiThreaded(100, 1000000, new Runnable() {
			@Override
			public void run() {
				check(map, t);
			}
		});

		XDB.shutdown();
	}

	private void check(final ConcurrentMap<String, Object> map, final IPost p) {
		notNullAll(p.content(), p.likes(), p.id(), p.version());

		isTrue(p.content() == p.content());
		isTrue(p.likes() == p.likes());

		eq(p.id(), map.get("id"));
		eq(p.version(), map.get("version"));
		eq(p.content().get(), map.get("content"));
	}

}
