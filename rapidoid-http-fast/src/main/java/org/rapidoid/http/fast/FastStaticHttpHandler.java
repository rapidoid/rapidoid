package org.rapidoid.http.fast;

/*
 * #%L
 * rapidoid-http-fast
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.net.abstracts.Channel;

@Authors("Nikolche Mihajlovski")
@Since("4.3.0")
public class FastStaticHttpHandler extends AbstractFastHttpHandler {

	private final FastHttp http;

	private final byte[] contentType;

	private final byte[] response;

	public FastStaticHttpHandler(FastHttp http, byte[] contentType, byte[] response) {
		this.http = http;
		this.contentType = contentType;
		this.response = response;
	}

	@Override
	public HttpStatus handle(Channel ctx, boolean isKeepAlive, Map<String, Object> params) {
		http.getListener().state(this, params);

		http.getListener().result(this, contentType, response);
		http.write200(ctx, isKeepAlive, contentType, response);

		return HttpStatus.DONE;
	}

}
