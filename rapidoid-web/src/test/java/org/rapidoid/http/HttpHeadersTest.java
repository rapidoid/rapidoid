package org.rapidoid.http;

/*
 * #%L
 * rapidoid-web
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
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class HttpHeadersTest extends HttpTestCommons {

	@Test
	public void headerNamesCaseInsensitive() {

		On.get("/hdrs").json(new ReqRespHandler() {
			@Override
			public Object execute(Req req, Resp resp) {
				return U.list(req.headers().get("foo"), req.headers().get("abc"));
			}
		});

		On.get("/host").json(new ReqRespHandler() {
			@Override
			public Object execute(Req req, Resp resp) {
				return req.host();
			}
		});

		On.get("/xnum").json(new ReqRespHandler() {
			@Override
			public Object execute(Req req, Resp resp) {
				return U.list(req.header("X-NuM"), req.header("X-NuM", "?"), req.header("foo"), req.header("FOO", "?"));
			}
		});

		verify("hdrs", HTTP.get(localhost("/hdrs")).header("Abc", "XYZ").header("Foo", "Bar").fetch());

		verify("host", HTTP.get(localhost("/host")).fetch());

		verify("xnum", HTTP.get(localhost("/xnum")).header("X-num", "123").header("Foo", "Bar").fetch());
	}

}
