package org.rapidoid.docs.httpfastest;

/*
 * #%L
 * rapidoid-http-fast
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

import org.rapidoid.buffer.Buf;
import org.rapidoid.http.AbstractHttpServer;
import org.rapidoid.http.HttpStatus;
import org.rapidoid.http.HttpUtils;
import org.rapidoid.http.MediaType;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.impl.RapidoidHelper;

public class CustomHttpServer extends AbstractHttpServer {

	private static final byte[] URI_PLAINTEXT = "/plaintext".getBytes();

	private static final byte[] URI_JSON = "/json".getBytes();

	private static final byte[] HELLO_WORLD = "Hello, World!".getBytes();

	@Override
	protected HttpStatus handle(Channel ctx, Buf buf, RapidoidHelper req) {

		if (req.isGet.value) {
			if (matches(buf, req.path, URI_PLAINTEXT)) {
				return ok(ctx, req.isKeepAlive.value, HELLO_WORLD, MediaType.TEXT_PLAIN);

			} else if (matches(buf, req.path, URI_JSON)) {
				return serializeToJson(HttpUtils.noReq(), ctx, req.isKeepAlive.value, new Message("Hello, World!"));
			}
		}

		return HttpStatus.NOT_FOUND;
	}

}
