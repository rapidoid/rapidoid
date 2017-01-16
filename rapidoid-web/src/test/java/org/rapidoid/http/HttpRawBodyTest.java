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
import org.rapidoid.util.Bufs;

@Authors("Nikolche Mihajlovski")
@Since("5.0.10")
public class HttpRawBodyTest extends HttpTestCommons {

	@Test
	public void testBytesResponse() {
		On.get("/bytes").html(new ReqHandler() {
			@Override
			public Object execute(Req req) throws Exception {
				return req.response().body("ABC".getBytes());
			}
		});

		onlyGet("/bytes");
	}

	@Test
	public void testByteBufferResponse() {
		On.get("/buf").json(new ReqHandler() {
			@Override
			public Object execute(Req req) throws Exception {
				return req.response().body(Bufs.buf("{\"byte-buffer\": true}"));
			}
		});

		onlyGet("/buf");
	}

}
