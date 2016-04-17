package org.rapidoid.http;

import org.apache.http.*;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.entity.NByteArrayEntity;
import org.apache.http.protocol.HttpContext;
import org.rapidoid.RapidoidThing;
import org.rapidoid.activity.RapidoidThreadFactory;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Err;
import org.rapidoid.commons.Str;
import org.rapidoid.concurrent.*;
import org.rapidoid.io.IO;
import org.rapidoid.io.Upload;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;

/*
 * #%L
 * rapidoid-http-client
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
public class HttpClientUtil extends RapidoidThing {

	private static final RedirectStrategy NO_REDIRECTS = new RedirectStrategy() {
		@Override
		public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context)
				throws ProtocolException {
			return false;
		}

		@Override
		public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context)
				throws ProtocolException {
			return null;
		}
	};

	static CloseableHttpAsyncClient client(HttpClient config) {
		ConnectionReuseStrategy reuseStrategy = config.reuseConnections() ? new DefaultConnectionReuseStrategy() : new NoConnectionReuseStrategy();

		HttpAsyncClientBuilder builder = HttpAsyncClients.custom()
				.setThreadFactory(new RapidoidThreadFactory("http-client"))
				.disableConnectionState()
				.disableAuthCaching()
				.setMaxConnPerRoute(config.maxConnPerRoute())
				.setMaxConnTotal(config.maxConnTotal())
				.setConnectionReuseStrategy(reuseStrategy)
				.setRedirectStrategy(config.followRedirects() ? new DefaultRedirectStrategy() : NO_REDIRECTS);


		if (!U.isEmpty(config.cookies())) {
			BasicCookieStore cookieStore = new BasicCookieStore();

			for (Map.Entry<String, String> e : config.cookies().entrySet()) {
				BasicClientCookie cookie = new BasicClientCookie(e.getKey(), e.getValue());
				cookie.setDomain(getDomain(config));
				cookie.setPath("/");
				cookieStore.addCookie(cookie);
			}

			builder = builder.setDefaultCookieStore(cookieStore);
		}

		if (config.userAgent() != null) {
			builder = builder.setUserAgent(config.userAgent());
		}

		if (!config.keepCookies() && U.isEmpty(config.cookies())) {
			builder = builder.disableCookieManagement();
		}

		return builder.build();
	}

	private static String getDomain(HttpClient config) {
		String url = config.url();

		url = Str.triml(url, "http://");
		url = Str.triml(url, "https://");

		String domain = url.split("(/|:)")[0];

		return domain;
	}

	static HttpRequestBase createRequest(HttpClient config) {

		Map<String, String> headers = U.safe(config.headers());
		Map<String, Object> data = U.safe(config.data());
		Map<String, List<Upload>> files = U.safe(config.files());

		String url = config.url();

		HttpRequestBase req;
		boolean canHaveBody = false;

		switch (config.verb()) {
			case GET:
				req = new HttpGet(url);
				break;

			case POST:
				req = new HttpPost(url);
				canHaveBody = true;
				break;

			case PUT:
				req = new HttpPut(url);
				canHaveBody = true;
				break;

			case DELETE:
				req = new HttpDelete(url);
				break;

			case PATCH:
				req = new HttpPatch(url);
				canHaveBody = true;
				break;

			case OPTIONS:
				req = new HttpOptions(url);
				break;

			case HEAD:
				req = new HttpHead(url);
				break;

			case TRACE:
				req = new HttpTrace(url);
				break;

			default:
				throw Err.notExpected();
		}

		for (Map.Entry<String, String> e : headers.entrySet()) {
			req.addHeader(e.getKey(), e.getValue());
		}

		if (canHaveBody) {
			HttpEntityEnclosingRequestBase entityEnclosingReq = (HttpEntityEnclosingRequestBase) req;

			if (config.body() != null) {

				NByteArrayEntity entity = new NByteArrayEntity(config.body());

				if (config.contentType() != null) {
					entity.setContentType(config.contentType());
				}

				entityEnclosingReq.setEntity(entity);
			} else {

				MultipartEntityBuilder builder = MultipartEntityBuilder.create();

				for (Map.Entry<String, List<Upload>> entry : files.entrySet()) {
					for (Upload file : entry.getValue()) {
						builder = builder.addBinaryBody(entry.getKey(), file.content(), ContentType.DEFAULT_BINARY, file.filename());
					}
				}

				for (Map.Entry<String, Object> entry : data.entrySet()) {
					String name = entry.getKey();
					String value = String.valueOf(entry.getValue());
					builder = builder.addTextBody(name, value, ContentType.DEFAULT_TEXT);
				}

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				try {
					builder.build().writeTo(stream);
				} catch (IOException e) {
					throw U.rte(e);
				}

				byte[] bytes = stream.toByteArray();
				NByteArrayEntity entity = new NByteArrayEntity(bytes, ContentType.MULTIPART_FORM_DATA);

				entityEnclosingReq.setEntity(entity);
			}
		}

		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(config.socketTimeout())
				.setConnectTimeout(config.connectTimeout())
				.setConnectionRequestTimeout(config.connectionRequestTimeout())
				.build();

		req.setConfig(requestConfig);

		return req;
	}

	static Future<byte[]> request(HttpClient config, CloseableHttpAsyncClient client,
	                              Callback<byte[]> callback, boolean close) {

		HttpRequestBase req = createRequest(config);
		Log.debug("Starting HTTP request", "request", req.getRequestLine());

		Promise<byte[]> promise = Promises.create();
		FutureCallback<HttpResponse> cb = callback(client, callback, promise, config.raw(), close);
		client.execute(req, cb);

		return promise;
	}

	private static <T> FutureCallback<HttpResponse> callback(final CloseableHttpAsyncClient client,
	                                                         final Callback<byte[]> callback,
	                                                         final Callback<byte[]> promise,
	                                                         final boolean fullResponse,
	                                                         final boolean close) {

		return new FutureCallback<HttpResponse>() {

			@Override
			public void completed(HttpResponse response) {
				int statusCode = response.getStatusLine().getStatusCode();

				if (!fullResponse && statusCode != 200) {
					Callbacks.error(callback, new HttpException(statusCode));
					Callbacks.error(promise, new HttpException(statusCode));
					if (close) {
						close(client);
					}
					return;
				}

				byte[] bytes;

				if (response.getEntity() != null) {
					try {
						if (fullResponse) {
							bytes = responseToBytes(response);
						} else {
							InputStream resp = response.getEntity().getContent();
							bytes = IO.loadBytes(resp);
							U.must(bytes != null, "Couldn't read the HTTP response!");
						}

					} catch (Exception e) {
						Callbacks.error(callback, e);
						Callbacks.error(promise, e);
						if (close) {
							close(client);
						}
						return;
					}
				} else {
					bytes = new byte[0];
				}

				Callbacks.success(callback, bytes);
				Callbacks.success(promise, bytes);
				if (close) {
					close(client);
				}
			}

			@Override
			public void failed(Exception e) {
				Callbacks.error(callback, e);
				Callbacks.error(promise, e);
				if (close) {
					close(client);
				}
			}

			@Override
			public void cancelled() {
				Callbacks.error(callback, new CancellationException());
				Callbacks.error(promise, new CancellationException());
				if (close) {
					close(client);
				}
			}
		};
	}

	static byte[] responseToBytes(HttpResponse response) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter printer = new PrintWriter(baos);

		printer.print(response.getStatusLine() + "");
		printer.print("\n");

		for (Header hdr : response.getAllHeaders()) {
			printer.print(hdr.getName());
			printer.print(": ");
			printer.print(hdr.getValue());
			printer.print("\n");
		}

		printer.print("\n");

		printer.flush();

		try {
			response.getEntity().writeTo(baos);
		} catch (Exception e) {
			throw U.rte(e);
		}

		return baos.toByteArray();
	}

	static void close(CloseableHttpAsyncClient client) {
		try {
			Log.debug("Closing HTTP client", "client", client);
			client.close();
		} catch (IOException e) {
			throw U.rte(e);
		}
	}

}
