package org.rapidoid.reverseproxy;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.http.*;
import org.rapidoid.http.impl.HttpIO;
import org.rapidoid.log.LogLevel;
import org.rapidoid.u.U;

import java.net.ConnectException;
import java.util.List;
import java.util.Map;

/*
 * #%L
 * rapidoid-http-server
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
@Since("5.2.0")
public class ReverseProxy extends AbstractReverseProxyBean<ReverseProxy> implements ReqRespHandler {

	private final List<ProxyMapping> mappings = U.list();

	@Override
	public Object execute(final Req req, final Resp resp) throws Exception {

		ProxyMapping mapping = findMapping(req);
		if (mapping == null) return null; // not found!

		req.async();

		final String targetUrl = mapping.getTargetUrl(req);

		Map<String, String> headers = req.headers();
		headers.remove("transfer-encoding");
		headers.remove("content-length");

		HttpClient client = getOrCreateClient();

		client.req()
			.verb(req.verb())
			.url(targetUrl)
			.headers(headers)
			.cookies(req.cookies())
			.body(req.body())
			.raw(true)
			.execute(new Callback<HttpResp>() {

				@Override
				public void onDone(HttpResp result, Throwable error) {
					if (error == null) {

						processResponseHeaders(result.headers(), resp);

						resp.code(result.code());
						resp.body(result.bodyBytes());
						resp.done();

					} else {

						if (error instanceof ConnectException) {
							HttpIO.errorAndDone(req, U.rte("Couldn't connect to the upstream!", error), LogLevel.DEBUG);
						} else {
							HttpIO.errorAndDone(req, error, LogLevel.ERROR);
						}
					}
				}

			});

		return req;
	}

	protected ProxyMapping findMapping(Req req) {
		for (ProxyMapping mapping : mappings) {
			if (mapping.matches(req)) {
				return mapping;
			}
		}

		return null;
	}

	protected void processResponseHeaders(Map<String, String> headers, Resp resp) {
		for (Map.Entry<String, String> hdr : headers.entrySet()) {
			String name = hdr.getKey();
			String value = hdr.getValue();
			String lowerName = name.toLowerCase();

			if (lowerName.equals("content-type")) {
				resp.contentType(MediaType.of(value));

			} else if (!ignoreResponseHeader(lowerName)) {
				resp.headers().put(name, value);
			}
		}
	}

	protected boolean ignoreResponseHeader(String name) {
		return name.equals("transfer-encoding")
			|| name.equals("content-length")
			|| name.equals("connection")
			|| name.equals("date")
			|| name.equals("server");
	}

	@Override
	protected HttpClient createClient() {
		return HTTP.client()
			.reuseConnections(reuseConnections())
			.keepCookies(false)
			.maxConnTotal(maxConnTotal())
			.maxConnPerRoute(maxConnPerRoute());
	}

	public ReverseProxyMapDSL map(String uriPrefix) {
		return new ReverseProxyMapDSL(this, uriPrefix);
	}

	public List<ProxyMapping> mappings() {
		return mappings;
	}

	public void reset() {
		mappings.clear();
	}

}
