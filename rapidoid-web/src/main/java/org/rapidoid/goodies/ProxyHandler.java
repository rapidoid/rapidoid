package org.rapidoid.goodies;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.http.*;
import org.rapidoid.http.impl.HttpIO;

/*
 * #%L
 * rapidoid-web
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class ProxyHandler extends RapidoidThing implements ReqRespHandler {

	private final String host;

	private final HttpClient client = HTTP.client().reuseConnections(true).maxConnTotal(100).maxConnPerRoute(100);

	public ProxyHandler(String host) {
		this.host = host;
	}

	@Override
	public Object execute(final Req req, final Resp resp) throws Exception {
		req.async();

		client.req()
				.verb(req.verb())
				.url(host + req.uri())
				.headers(req.headers())
				.body(req.body())
				.raw(true)
				.execute(new Callback<byte[]>() {

					@Override
					public void onDone(byte[] result, Throwable error) {
						if (error == null) {
							resp.raw(result).done();
						} else {
							HttpIO.errorAndDone(req, error, req.custom().errorHandler());
						}
					}

				});

		return req;
	}

}
