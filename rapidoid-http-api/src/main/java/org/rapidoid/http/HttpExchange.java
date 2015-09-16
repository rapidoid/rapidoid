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
import org.rapidoid.annotation.P;
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

	String param(@P("name") String name);

	String param(@P("name") String name, @P("defaultValue") String defaultValue);

	Map<String, String> headers();

	String header(@P("name") String name);

	String header(@P("name") String name, @P("defaultValue") String defaultValue);

	Map<String, String> cookies();

	String cookie(@P("name") String name);

	String cookie(@P("name") String name, @P("defaultValue") String defaultValue);

	Map<String, String> posted();

	String posted(@P("name") String name);

	String posted(@P("name") String name, @P("defaultValue") String defaultValue);

	Map<String, byte[]> files();

	byte[] file(@P("name") String name);

	byte[] file(@P("name") String name, @P("defaultValue") byte[] defaultValue);

	/**
	 * Data includes params + posted.
	 */
	Map<String, String> data();

	/**
	 * Data includes params + posted.
	 */
	String data(@P("name") String name);

	/**
	 * Data includes params + posted.
	 */
	String data(@P("name") String name, @P("defaultValue") String defaultValue);

	String home();

	String[] pathSegments();

	String pathSegment(@P("segmentIndex") int segmentIndex);

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

	<T extends Serializable> T session(@P("name") String name);

	<T extends Serializable> T session(@P("name") String name, @P("defaultValue") T defaultValue);

	<T extends Serializable> T sessionGetOrCreate(@P("name") String name, @P("valueClass") Class<T> valueClass,
			@P("constructorArgs") Object... constructorArgs);

	/* COOKIEPACK SCOPE: */

	Map<String, Serializable> cookiepack();

	<T extends Serializable> T cookiepack(@P("name") String name);

	<T extends Serializable> T cookiepack(@P("name") String name, @P("defaultValue") T defaultValue);

	<T extends Serializable> T cookiepackGetOrCreate(@P("name") String name, @P("valueClass") Class<T> valueClass,
			@P("constructorArgs") Object... constructorArgs);

	/* LOCAL SCOPE: */

	Map<String, Serializable> locals();

	<T extends Serializable> T local(@P("key") String key);

	<T extends Serializable> T local(@P("key") String key, @P("defaultValue") T defaultValue);

	<T extends Serializable> T localGetOrCreate(@P("name") String name, @P("valueClass") Class<T> valueClass,
			@P("constructorArgs") Object... constructorArgs);

	/* TMP SCOPE: */

	Map<String, Object> tmps();

	<T> T tmp(@P("key") String key);

	<T> T tmp(@P("key") String key, @P("defaultValue") T defaultValue);

	<T> T tmpGetOrCreate(@P("name") String name, @P("valueClass") Class<T> valueClass,
			@P("constructorArgs") Object... constructorArgs);

	/* RESPONSE: */

	HttpExchange result(@P("result") Object result);

	Map<String, String> errors();

	boolean hasErrors();

	String constructUrl(@P("path") String path);

	HttpExchange startResponse(@P("httpResponseCode") int httpResponseCode);

	HttpExchange response(@P("httpResponseCode") int httpResponseCode);

	HttpExchange response(@P("httpResponseCode") int httpResponseCode, @P("response") String response);

	HttpExchange response(@P("httpResponseCode") int httpResponseCode, @P("response") String response,
			@P("err") Throwable err);

	HttpExchange error(@P("err") Throwable err);

	HttpNotFoundException notFound();

	HttpSuccessException error();

	HttpExchange plain();

	HttpExchange html();

	HttpExchange json();

	HttpExchange binary();

	HttpExchange download(@P("filename") String filename);

	HttpExchange addHeader(@P("name") byte[] name, @P("value") byte[] value);

	HttpExchange addHeader(@P("name") HttpHeader name, @P("value") String value);

	HttpExchange setCookie(@P("name") String name, @P("value") String value, @P("extras") String... extras);

	HttpExchange setContentType(@P("contentType") MediaType contentType);

	HttpExchange accessDeniedIf(@P("accessDeniedCondition") boolean accessDeniedCondition);

	HttpExchange authorize(@P("clazz") Class<?> clazz);

	int responseCode();

	String redirectUrl();

	boolean serveStaticFile();

	boolean serveStaticFile(@P("filename") String filename);

	HttpExchange sendFile(@P("file") File file);

	HttpExchange sendFile(@P("resource") Res resource);

	HttpExchange sendFile(@P("mediaType") MediaType mediaType, @P("bytes") byte[] bytes);

	HttpSuccessException redirect(@P("url") String url);

	HttpSuccessException goBack(@P("steps") int steps);

	HttpExchange addToPageStack();

	OutputStream outputStream();

	HttpExchange write(String s);

	HttpExchange writeln(String s);

	HttpExchange write(@P("bytes") byte[] bytes);

	HttpExchange write(@P("bytes") byte[] bytes, @P("offset") int offset, @P("length") int length);

	HttpExchange write(@P("buf") ByteBuffer buf);

	HttpExchange write(@P("file") File file);

	HttpExchange writeJSON(@P("value") Object value);

	// due to async() web handling option, @P("ain") it ain't over till the fat lady sings "done"
	HttpExchange async();

	HttpExchange done();

	HttpExchange render(@P("template") ITemplate template, @P("model") Object model);

	HttpExchange renderPage(@P("model") Object model);

	String renderPageToHTML(@P("model") Object model);

	/* EXTRAS: */

	<T> T persister();

	Object model();

}
