package org.rapidoid.http;

/*
 * #%L
 * rapidoid-rest
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CancellationException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.entity.NByteArrayEntity;
import org.rapidoid.activity.RapidoidThreadFactory;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.concurrent.Callbacks;
import org.rapidoid.concurrent.Future;
import org.rapidoid.concurrent.Promise;
import org.rapidoid.concurrent.Promises;
import org.rapidoid.io.IO;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class HttpClient {

	private CloseableHttpAsyncClient client;

	public HttpClient() {
		this(asyncClient());
	}

	private static CloseableHttpAsyncClient asyncClient() {
		return HttpAsyncClients.custom().setThreadFactory(new RapidoidThreadFactory("http-client"))
				.disableCookieManagement().disableConnectionState().disableAuthCaching().build();
	}

	public HttpClient(CloseableHttpAsyncClient client) {
		this.client = client;
		client.start();
	}

	public Future<byte[]> post(String uri, Map<String, String> headers, byte[] postData, String contentType,
			Callback<byte[]> callback) {

		headers = U.safe(headers);

		HttpPost req = new HttpPost(uri);

		NByteArrayEntity entity = new NByteArrayEntity(postData);

		if (contentType != null) {
			entity.setContentType(contentType);
		}

		for (Entry<String, String> e : headers.entrySet()) {
			req.addHeader(e.getKey(), e.getValue());
		}

		req.setEntity(entity);

		Log.debug("Starting HTTP POST request", "request", req.getRequestLine());

		return execute(client, req, callback);
	}

	public Future<byte[]> post(String uri, Map<String, String> headers, Map<String, String> data,
			Map<String, String> files, Callback<byte[]> callback) {

		headers = U.safe(headers);
		data = U.safe(data);
		files = U.safe(files);

		HttpPost req = new HttpPost(uri);

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();

		for (Entry<String, String> entry : files.entrySet()) {
			String filename = entry.getValue();
			File file = IO.file(filename);
			builder = builder.addBinaryBody(entry.getKey(), file, ContentType.DEFAULT_BINARY, filename);
		}

		for (Entry<String, String> entry : data.entrySet()) {
			builder = builder.addTextBody(entry.getKey(), entry.getValue(), ContentType.DEFAULT_TEXT);
		}

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			builder.build().writeTo(stream);
		} catch (IOException e) {
			throw U.rte(e);
		}

		byte[] bytes = stream.toByteArray();
		NByteArrayEntity entity = new NByteArrayEntity(bytes, ContentType.MULTIPART_FORM_DATA);

		for (Entry<String, String> e : headers.entrySet()) {
			req.addHeader(e.getKey(), e.getValue());
		}

		req.setEntity(entity);

		Log.debug("Starting HTTP POST request", "request", req.getRequestLine());

		return execute(client, req, callback);
	}

	public Future<byte[]> get(String uri, Callback<byte[]> callback) {
		HttpGet req = new HttpGet(uri);

		Log.debug("Starting HTTP GET request", "request", req.getRequestLine());

		return execute(client, req, callback);
	}

	private Future<byte[]> execute(CloseableHttpAsyncClient client, HttpRequestBase req, Callback<byte[]> callback) {

		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000)
				.setConnectionRequestTimeout(10000).build();
		req.setConfig(requestConfig);

		Promise<byte[]> promise = Promises.create();

		FutureCallback<HttpResponse> cb = callback(callback, promise);
		client.execute(req, cb);

		return promise;
	}

	private <T> FutureCallback<HttpResponse> callback(final Callback<byte[]> callback, final Callback<byte[]> promise) {
		return new FutureCallback<HttpResponse>() {

			@Override
			public void completed(HttpResponse response) {
				int statusCode = response.getStatusLine().getStatusCode();

				if (statusCode != 200) {
					Callbacks.error(callback, new HttpException(statusCode));
					Callbacks.error(promise, new HttpException(statusCode));
					return;
				}

				byte[] bytes;
				try {
					InputStream resp = response.getEntity().getContent();
					bytes = IOUtils.toByteArray(resp);
				} catch (Exception e) {
					Callbacks.error(callback, e);
					Callbacks.error(promise, e);
					return;
				}

				Callbacks.success(callback, bytes);
				Callbacks.success(promise, bytes);
			}

			@Override
			public void failed(Exception e) {
				Callbacks.error(callback, e);
				Callbacks.error(promise, e);
			}

			@Override
			public void cancelled() {
				Callbacks.error(callback, new CancellationException());
				Callbacks.error(promise, new CancellationException());
			}
		};
	}

	public synchronized void close() {
		try {
			client.close();
		} catch (IOException e) {
			throw U.rte(e);
		}
	}

	public synchronized void reset() {
		close();
		client = asyncClient();
		client.start();
	}

}
