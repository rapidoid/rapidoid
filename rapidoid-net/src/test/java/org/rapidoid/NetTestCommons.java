package org.rapidoid;

/*
 * #%L
 * rapidoid-net
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

import org.rapidoid.log.Log;
import org.rapidoid.net.TCP;
import org.rapidoid.net.TCPServer;
import org.rapidoid.net.impl.Protocol;
import org.rapidoid.test.TestCommons;
import org.rapidoid.util.UTILS;

public abstract class NetTestCommons extends TestCommons {

	protected void server(Protocol protocol, Runnable client) {
		TCPServer server = TCP.listen(protocol);

		UTILS.sleep(300);
		System.out.println("----------------------------------------");

		try {
			client.run();
		} finally {
			server.shutdown();
			UTILS.sleep(300);
			System.out.println("--- SERVER STOPPED ---");
		}

		Log.info("server finished");
	}

}
