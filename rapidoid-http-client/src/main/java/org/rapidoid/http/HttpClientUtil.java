package org.rapidoid.http;

import org.apache.http.*;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
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
import org.rapidoid.net.tls.TLSUtil;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;

/*
 * #%L
 * rapidoid-http-client
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

	static CloseableHttpAsyncClient client(HttpClient client) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

		ConnectionReuseStrategy reuseStrategy = client.reuseConnections() ? new DefaultConnectionReuseStrategy() : new NoConnectionReuseStrategy();

		HttpAsyncClientBuilder builder = HttpAsyncClients.custom()
			.setThreadFactory(new RapidoidThreadFactory("http-client", true))
			.disableConnectionState()
			.disableAuthCaching()
			.setMaxConnPerRoute(client.maxConnPerRoute())
			.setMaxConnTotal(client.maxConnTotal())
			.setConnectionReuseStrategy(reuseStrategy)
			.setRedirectStrategy(client.followRedirects() ? new DefaultRedirectStrategy() : NO_REDIRECTS);

		if (!client.validateSSL()) {
			builder.setSSLContext(TLSUtil.createTrustingContext());
			builder.setSSLHostnameVerifier(new AllowAllHostnameVerifier());
		}

		if (!U.isEmpty(client.cookies())) {
			BasicCookieStore cookieStore = new BasicCookieStore();

			for (Map.Entry<String, String> e : client.cookies().entrySet()) {
				BasicClientCookie cookie = new BasicClientCookie(e.getKey(), e.getValue());

				String host = client.host();
				U.notNull(host, "HTTP client host");

				cookie.setDomain(getDomain(host));
				cookie.setPath("/");
				cookieStore.addCookie(cookie);
			}

			builder = builder.setDefaultCookieStore(cookieStore);
		}

		if (client.userAgent() != null) {
			builder = builder.setUserAgent(client.userAgent());
		}

		if (!client.keepCookies() && U.isEmpty(client.cookies())) {
			builder = builder.disableCookieManagement();
		}

		return builder.build();
	}

	private static String getDomain(String host) {
		String url = host;

		url = Str.triml(url, "http://");
		url = Str.triml(url, "https://");

		return url.split("(/|:)")[0];
	}

	static HttpRequestBase createRequest(HttpReq config) {

		Map<String, String> headers = U.safe(config.headers());
		Map<String, String> cookies = U.safe(config.cookies());

		String url = config.url();

		url = Msc.urlWithProtocol(url);

		HttpRequestBase req = createReq(config, url);

		for (Map.Entry<String, String> e : headers.entrySet()) {
			req.addHeader(e.getKey(), e.getValue());
		}

		if (U.notEmpty(cookies)) {
			req.addHeader("Cookie", joinCookiesAsHeader(cookies));
		}

		switch (config.verb()) {
			case POST:
			case PUT:
			case PATCH:
				HttpEntityEnclosingRequestBase entityEnclosingReq = (HttpEntityEnclosingRequestBase) req;

				if (config.body() != null) {
					entityEnclosingReq.setEntity(byteBody(config));

				} else if (U.notEmpty(config.data()) || U.notEmpty(config.files())) {
					entityEnclosingReq.setEntity(paramsBody(config.data(), config.files()));
				}
				break;
		}

		req.setConfig(reqConfig(config));

		return req;
	}

	private static String joinCookiesAsHeader(Map<String, String> cookies) {
		StringBuilder allCookies = new StringBuilder();

		for (Iterator<Map.Entry<String, String>> it = cookies.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, String> e = it.next();

			allCookies.append(e.getKey());
			allCookies.append("=");
			allCookies.append(e.getValue());

			if (it.hasNext()) {
				allCookies.append("; ");
			}
		}

		return allCookies.toString();
	}

	private static RequestConfig reqConfig(HttpReq config) {
		return RequestConfig.custom()
			.setSocketTimeout(config.socketTimeout())
			.setConnectTimeout(config.connectTimeout())
			.setConnectionRequestTimeout(config.connectionRequestTimeout())
			.build();
	}

	private static NByteArrayEntity paramsBody(Map<String, Object> data, Map<String, List<Upload>> files) {
		data = U.safe(data);
		files = U.safe(files);

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
		return new NByteArrayEntity(bytes, ContentType.MULTIPART_FORM_DATA);
	}

	private static NByteArrayEntity byteBody(HttpReq config) {
		NByteArrayEntity entity = new NByteArrayEntity(config.body());

		if (config.contentType() != null) {
			entity.setContentType(config.contentType());
		}
		return entity;
	}

	private static HttpRequestBase createReq(HttpReq config, String url) {
		HttpRequestBase req;
		switch (config.verb()) {
			case GET:
				req = new HttpGet(url);
				break;

			case POST:
				req = new HttpPost(url);
				break;

			case PUT:
				req = new HttpPut(url);
				break;

			case DELETE:
				req = new HttpDelete(url);
				break;

			case PATCH:
				req = new HttpPatch(url);
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
		return req;
	}

	static Future<HttpResp> request(HttpReq config, CloseableHttpAsyncClient client,
	                                Callback<HttpResp> callback, boolean close) {

		HttpRequestBase req = createRequest(config);

		if (Log.isDebugEnabled()) Log.debug("Starting HTTP request", "request", req.getRequestLine());

		Promise<HttpResp> promise = Promises.create();
		FutureCallback<HttpResponse> cb = callback(client, callback, promise, close);
		client.execute(req, cb);

		return promise;
	}

	private static <T> FutureCallback<HttpResponse> callback(final CloseableHttpAsyncClient client,
	                                                         final Callback<HttpResp> callback,
	                                                         final Callback<HttpResp> promise,
	                                                         final boolean close) {

		return new FutureCallback<HttpResponse>() {

			@Override
			public void completed(HttpResponse response) {
				HttpResp resp;

				try {
					resp = response(response);

				} catch (Exception e) {
					Callbacks.error(callback, e);
					Callbacks.error(promise, e);
					if (close) {
						close(client);
					}
					return;
				}

				Callbacks.success(callback, resp);
				Callbacks.success(promise, resp);

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

	private static HttpResp response(HttpResponse response) throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter printer = new PrintWriter(baos);

		printer.print(response.getStatusLine() + "");
		printer.print("\n");

		Map<String, String> headers = U.map();

		for (Header hdr : response.getAllHeaders()) {
			printer.print(hdr.getName());
			printer.print(": ");
			printer.print(hdr.getValue());
			printer.print("\n");

			headers.put(hdr.getName(), hdr.getValue());
		}

		printer.print("\n");
		printer.flush();

		HttpEntity entity = response.getEntity();
		byte[] body = entity != null ? IO.loadBytes(response.getEntity().getContent()) : new byte[0];

		baos.write(body);
		byte[] raw = baos.toByteArray();

		return new HttpResp(raw, response.getStatusLine().getStatusCode(), headers, body);
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
