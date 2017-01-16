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
import org.rapidoid.setup.On;
import org.rapidoid.util.Bufs;

@Authors("Nikolche Mihajlovski")
@Since("5.0.11")
public class HttpFullRawResponseTest extends IsolatedIntegrationTest {

	public static final String BYTES_RESPONSE = "HTTP/1.1 200 OK\nContent-Length: 5\n\nbytes";

	public static final String BUF_RESPONSE = "HTTP/1.1 200 OK\nContent-Length: 11\n\nbyte-buffer";

	@Test
	public void testBytesRawResponse() {
		On.get("/bytes").html(req -> req.response().raw(BYTES_RESPONSE.getBytes()));

		onlyGet("/bytes");
	}

	@Test
	public void testByteBufferRawResponse() {
		On.get("/buf").html((Req req, Resp resp) -> resp.raw(Bufs.buf(BUF_RESPONSE)));

		onlyGet("/buf");
	}

}
