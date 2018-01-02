/*-
 * #%L
 * rapidoid-integration-tests
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
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

package tcpdemo;

import org.rapidoid.insight.Insights;
import org.rapidoid.net.Server;
import org.rapidoid.net.TCP;
import org.rapidoid.net.abstracts.Channel;

public class TCPServerDemo {

	public static void main(String[] args) {
		new TCPServerDemo().run();
	}

	public void run() {
		Server server = TCP.server()
			.port(5555)
			.protocol(this::server)
			.build();

		server.start();

		Insights.show();
	}

	private void server(Channel ch) {
		Object s = Serializer.deserialize(ch.input());
		Serializer.serialize(ch.output(), s);
		ch.send();
	}

}
