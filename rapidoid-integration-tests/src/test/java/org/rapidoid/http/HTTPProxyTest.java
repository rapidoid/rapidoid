package org.rapidoid.http;

/*
 * #%L
 * rapidoid-integration-tests
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
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.setup.App;
import org.rapidoid.setup.Setup;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class HTTPProxyTest extends IsolatedIntegrationTest {

	@Test
	public void testProxy() {
		App.run(new String[0], "/->localhost:5555,localhost:6666");

		Setup x = Setup.create("x").port(5555);
		Setup y = Setup.create("y").port(6666);

		try {
			x.get("/who").html("X");
			y.get("/who").html("Y");

			eq(Self.get("/who").fetch(), "X");
			eq(Self.get("/who").fetch(), "Y");
			eq(Self.get("/who").fetch(), "X");
			eq(Self.get("/who").fetch(), "Y");

		} finally {
			x.shutdown();
			y.shutdown();
		}
	}

	@Test
	public void testProxySimpleConfig() {
		Conf.PROXY.set("/", "localhost:5555,localhost:6666");
		App.boot();

		Setup a = Setup.create("a").port(5555);
		Setup b = Setup.create("b").port(6666);

		try {
			a.get("/who").html("A");
			b.get("/who").html("B");

			eq(Self.get("/who").fetch(), "A");
			eq(Self.get("/who").fetch(), "B");
			eq(Self.get("/who").fetch(), "A");
			eq(Self.get("/who").fetch(), "B");

		} finally {
			a.shutdown();
			b.shutdown();
		}
	}

}
