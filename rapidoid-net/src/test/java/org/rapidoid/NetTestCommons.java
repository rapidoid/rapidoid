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

import org.rapidoid.net.TCP;
import org.rapidoid.net.TCPServer;
import org.rapidoid.net.impl.Protocol;
import org.rapidoid.test.TestCommons;
import org.rapidoid.util.Log;
import org.rapidoid.util.U;

public abstract class NetTestCommons extends TestCommons {

	protected void server(Protocol protocol, Runnable client) {
		TCPServer server = TCP.listen(protocol);

		U.sleep(300);
		U.print("----------------------------------------");

		try {
			client.run();
		} finally {
			server.shutdown();
			U.sleep(300);
			U.print("--- SERVER STOPPED ---");
		}

		Log.info("server finished");
	}

}
