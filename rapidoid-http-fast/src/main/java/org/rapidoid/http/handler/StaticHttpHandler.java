package org.rapidoid.http.handler;

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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.HttpStatus;
import org.rapidoid.http.HttpUtils;
import org.rapidoid.http.Req;
import org.rapidoid.http.impl.lowlevel.HttpIO;
import org.rapidoid.http.impl.RouteOptions;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("4.3.0")
public class StaticHttpHandler extends AbstractHttpHandler {

	private final byte[] response;

	public StaticHttpHandler(RouteOptions options, byte[] response) {
		super(options);
		this.response = response;
	}

	@Override
	public HttpStatus handle(Channel ctx, boolean isKeepAlive, Req req, Object extra) {
		HttpIO.INSTANCE.write200(HttpUtils.maybe(req), ctx, isKeepAlive, contentType, response);
		return HttpStatus.DONE;
	}

	@Override
	public String toString() {
		return contentTypeInfo(U.frmt("() -> (static response of %s bytes)", response.length));
	}

}
