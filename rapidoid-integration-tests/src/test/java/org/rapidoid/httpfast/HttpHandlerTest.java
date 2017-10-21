package org.rapidoid.httpfast;

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
import org.rapidoid.config.ConfigImpl;
import org.rapidoid.http.FastHttp;
import org.rapidoid.http.IsolatedIntegrationTest;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.impl.HttpRoutesImpl;
import org.rapidoid.net.Server;
import org.rapidoid.setup.My;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class HttpHandlerTest extends IsolatedIntegrationTest {

	@Test
	public void testFastHttpHandler() {
		Customization customization = new Customization("example", My.custom(), new ConfigImpl());
		HttpRoutesImpl routes = new HttpRoutesImpl("example", customization);
		FastHttp http = new FastHttp(routes);

		routes.on("get", "/abc", (req, resp) -> resp.json(req.data()));

		routes.on("get,post", "/xyz", (req, resp) -> resp.html(U.list(req.uri(), req.data())));

		Server server = http.listen(7779);

		onlyGet(7779, "/abc?x=1&y=foo");

		getAndPost(7779, "/xyz?aa=foo&bb=bar&c=true");

		server.shutdown();
	}

}
