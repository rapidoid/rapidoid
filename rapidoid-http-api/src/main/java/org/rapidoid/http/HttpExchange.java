package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http-api
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ctx.AppExchange;
import org.rapidoid.mime.MediaType;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public interface HttpExchange extends AppExchange {

	/* REQUEST METHODS: */

	String verb();

	String uri();

	String path();

	String subpath();

	String query();

	String protocol();

	String body();

	String host();

	Map<String, String> params();

	String param(String name);

	String param(String name, String defaultValue);

	Map<String, String> headers();

	String header(String name);

	String header(String name, String defaultValue);

	Map<String, String> cookies();

	String cookie(String name);

	String cookie(String name, String defaultValue);

	Map<String, String> data();

	String data(String name);

	String data(String name, String defaultValue);

	Map<String, byte[]> files();

	byte[] file(String name);

	byte[] file(String name, byte[] defaultValue);

	/**
	 * Vars include params + data.
	 */
	Map<String, String> vars();

	/**
	 * Vars include params + data.
	 */
	String var(String name);

	/**
	 * Vars include params + data.
	 */
	String var(String name, String defaultValue);

	Map<String, Object> session();

	Map<String, Object> locals();

	String sessionId();

	<T> T session(String name);

	<T> T session(String name, T defaultValue);

	void sessionSet(String name, Serializable value);

	<T extends Serializable> T sessionGetOrCreate(String name, Class<T> valueClass, Object... constructorArgs);

	void closeSession();

	boolean hasSession();

	String pathSegment(int segmentIndex);

	boolean isGetReq();

	boolean isPostReq();

	/* RESPONSE: */

	String constructUrl(String path);

	<T> T extra(Object key);

	void extra(Object key, Object value);

	String realAddress();

	HttpExchange startResponse(int httpResponseCode);

	HttpExchange response(int httpResponseCode);

	HttpExchange response(int httpResponseCode, String response);

	HttpExchange response(int httpResponseCode, String response, Throwable err);

	HttpExchange errorResponse(Throwable err);

	HttpNotFoundException notFound();

	HttpSuccessException error();

	HttpExchange plain();

	HttpExchange html();

	HttpExchange json();

	HttpExchange binary();

	HttpExchange download(String filename);

	HttpExchange addHeader(byte[] name, byte[] value);

	HttpExchange addHeader(HttpHeader name, String value);

	HttpExchange setCookie(String name, String value, String... extras);

	HttpExchange setContentType(MediaType contentType);

	HttpExchange accessDeniedIf(boolean accessDeniedCondition);

	HttpExchange authorize(Class<?> clazz);

	int responseCode();

	String redirectUrl();

	boolean serveStaticFile();

	HttpExchange sendFile(File file);

	HttpExchange sendFile(MediaType mediaType, byte[] bytes);

	HttpSuccessException redirect(String url);

	HttpSuccessException goBack(int steps);

	HttpExchange addToPageStack();

	OutputStream outputStream();

	HttpExchange write(String s);

	HttpExchange writeln(String s);

	HttpExchange write(byte[] bytes);

	HttpExchange write(byte[] bytes, int offset, int length);

	HttpExchange write(ByteBuffer buf);

	HttpExchange write(File file);

	HttpExchange writeJSON(Object value);

	// due to async() web handling option, it ain't over till the fat lady sings "done"
	HttpExchange async();

	HttpExchange done();

}
