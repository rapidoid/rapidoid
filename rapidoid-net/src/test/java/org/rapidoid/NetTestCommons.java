package org.rapidoid;

/*
 * #%L
 * rapidoid-net
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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
import org.rapidoid.log.Log;
import org.rapidoid.net.Protocol;
import org.rapidoid.net.Server;
import org.rapidoid.net.TCP;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public abstract class NetTestCommons extends TestCommons {

	protected void server(Protocol protocol, Runnable client) {
		Server server = TCP.server().protocol(protocol).build().start();

		U.sleep(300);
		System.out.println("----------------------------------------");

		try {
			client.run();
		} finally {
			server.shutdown();
			U.sleep(300);
			System.out.println("--- SERVER STOPPED ---");
		}

		Log.info("server finished");
	}

}
