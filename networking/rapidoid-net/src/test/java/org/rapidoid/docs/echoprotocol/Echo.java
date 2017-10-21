package org.rapidoid.docs.echoprotocol;

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

import org.junit.Test;
import org.rapidoid.net.Server;
import org.rapidoid.net.TCP;
import org.rapidoid.test.Doc;
import org.rapidoid.test.TestCommons;

public class Echo extends TestCommons {

	@Test
	@Doc(title = "Implementing Echo protocol")
	public void docs() {
		Server server = TCP.server().protocol(new EchoProtocol()).port(5555).build().start();

		/* Not doing any work now, so shutdown */

		server.shutdown();
	}

}
