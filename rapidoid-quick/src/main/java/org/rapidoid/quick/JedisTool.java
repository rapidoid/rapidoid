package org.rapidoid.quick;

/*
 * #%L
 * rapidoid-quick
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.config.Conf;
import org.rapidoid.u.U;
import redis.clients.jedis.Jedis;

@Authors("Nikolche Mihajlovski")
@Since("4.2.0")
public class JedisTool {

	private static Jedis jedis;

	public static Jedis get() {
		if (jedis == null) {
			String host = Conf.nested("redis", "host");
			host = U.or(host, "localhost");

			Object rport = Conf.nested("redis", "port");
			int port = rport != null ? Cls.convert(rport, int.class) : 6379;

			jedis = new Jedis(host, port);
		}

		return jedis;
	}

}
