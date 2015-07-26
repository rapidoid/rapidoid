package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.io.IO;
import org.rapidoid.log.Log;
import org.rapidoid.util.U;
import org.rapidoid.wire.Wire;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class HTTP {

	public static HTTPServerBuilder server() {
		return Wire.builder(HTTPServerBuilder.class, HTTPServer.class, HTTPServerImpl.class);
	}

	public static CloseableHttpClient client(String uri) {
		return HttpClientBuilder.create().build();
	}

	public static byte[] post(String uri, Map<String, String> headers, Map<String, String> data,
			Map<String, String> files) throws IOException, ClientProtocolException {

		headers = U.safe(headers);
		data = U.safe(data);
		files = U.safe(files);

		CloseableHttpClient client = client(uri);

		try {
			HttpPost httppost = new HttpPost(uri);

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();

			for (Entry<String, String> entry : files.entrySet()) {
				ContentType contentType = ContentType.create("application/octet-stream");
				String filename = entry.getValue();
				File file = IO.file(filename);
				builder = builder.addBinaryBody(entry.getKey(), file, contentType, filename);
			}

			for (Entry<String, String> entry : data.entrySet()) {
				ContentType contentType = ContentType.create("text/plain", "UTF-8");
				builder = builder.addTextBody(entry.getKey(), entry.getValue(), contentType);
			}

			httppost.setEntity(builder.build());

			for (Entry<String, String> e : headers.entrySet()) {
				httppost.addHeader(e.getKey(), e.getValue());
			}

			Log.info("Starting HTTP POST request", "request", httppost.getRequestLine());

			CloseableHttpResponse response = client.execute(httppost);

			try {
				int statusCode = response.getStatusLine().getStatusCode();
				U.must(statusCode == 200, "Expected HTTP status code 200, but found: %s", statusCode);

				InputStream resp = response.getEntity().getContent();
				return IOUtils.toByteArray(resp);

			} finally {
				response.close();
			}
		} finally {
			client.close();
		}
	}

	public static byte[] get(String uri) {
		try {
			CloseableHttpClient client = client(uri);

			HttpGet get = new HttpGet(uri);

			Log.info("Starting HTTP GET request", "request", get.getRequestLine());

			CloseableHttpResponse response = client.execute(get);

			int statusCode = response.getStatusLine().getStatusCode();
			U.must(statusCode == 200, "Expected HTTP status code 200, but found: %s", statusCode);

			InputStream resp = response.getEntity().getContent();

			return IOUtils.toByteArray(resp);
		} catch (Throwable e) {
			throw U.rte(e);
		}
	}

}
