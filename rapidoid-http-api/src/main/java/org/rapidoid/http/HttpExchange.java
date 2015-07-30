package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http-api
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
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.io.Res;
import org.rapidoid.mime.MediaType;
import org.rapidoid.plugins.templates.ITemplate;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public interface HttpExchange {

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

	Map<String, String> posted();

	String posted(String name);

	String posted(String name, String defaultValue);

	Map<String, byte[]> files();

	byte[] file(String name);

	byte[] file(String name, byte[] defaultValue);

	/**
	 * Data includes params + posted.
	 */
	Map<String, String> data();

	/**
	 * Data includes params + posted.
	 */
	String data(String name);

	/**
	 * Data includes params + posted.
	 */
	String data(String name, String defaultValue);

	String home();

	String[] pathSegments();

	String pathSegment(int segmentIndex);

	String realIpAddress();

	boolean isGetReq();

	boolean isPostReq();

	boolean isDevMode();

	long requestId();

	String resourceName();

	/* STATE: */

	String sessionId();

	/* SESSION SCOPE: */

	Map<String, Serializable> session();

	<T extends Serializable> T session(String name);

	<T extends Serializable> T session(String name, T defaultValue);

	<T extends Serializable> T sessionGetOrCreate(String name, Class<T> valueClass, Object... constructorArgs);

	/* COOKIEPACK SCOPE: */

	Map<String, Serializable> cookiepack();

	<T extends Serializable> T cookiepack(String name);

	<T extends Serializable> T cookiepack(String name, T defaultValue);

	<T extends Serializable> T cookiepackGetOrCreate(String name, Class<T> valueClass, Object... constructorArgs);

	/* LOCAL SCOPE: */

	Map<String, Serializable> locals();

	<T extends Serializable> T local(String key);

	<T extends Serializable> T local(String key, T defaultValue);

	<T extends Serializable> T localGetOrCreate(String name, Class<T> valueClass, Object... constructorArgs);

	/* TMP SCOPE: */

	Map<String, Object> tmps();

	<T> T tmp(String key);

	<T> T tmp(String key, T defaultValue);

	<T> T tmpGetOrCreate(String name, Class<T> valueClass, Object... constructorArgs);

	/* RESPONSE: */

	HttpExchange result(Object result);

	Map<String, String> errors();

	boolean hasErrors();

	String constructUrl(String path);

	HttpExchange startResponse(int httpResponseCode);

	HttpExchange response(int httpResponseCode);

	HttpExchange response(int httpResponseCode, String response);

	HttpExchange response(int httpResponseCode, String response, Throwable err);

	HttpExchange error(Throwable err);

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

	boolean serveStaticFile(String filename);

	HttpExchange sendFile(File file);

	HttpExchange sendFile(Res resource);

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

	HttpExchange render(ITemplate template, Object model);

	/* EXTRAS: */

	<P> P persister();

	Object model();

}
