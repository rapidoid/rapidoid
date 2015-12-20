package org.rapidoid.http;

/*
 * #%L
 * rapidoid-commons
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

import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("5.0.2")
public interface Req {

	/* REQUEST METHODS: */

	String verb();

	Req verb(String verb);

	String uri();

	Req uri(String uri);

	String path();

	Req path(String path);

	String query();

	Req query(String query);

	byte[] body();

	Req body(byte[] body);

	/* IP ADDRESS : */

	String clientIpAddress();

	/* HEADERS: */

	String host();

	String forwardedForAddress();

	/* UNIQUE CONNECTION ID: */

	long connectionId();

	/* UNIQUE REQUEST ID: */

	long requestId();

	/* URL PARAMETERS: */

	Map<String, String> params();

	String param(String name);

	String param(String name, String defaultValue);

	/* REQUEST HEADERS: */

	Map<String, String> headers();

	String header(String name);

	String header(String name, String defaultValue);

	/* REQUEST COOKIES: */

	Map<String, String> cookies();

	String cookie(String name);

	String cookie(String name, String defaultValue);

	/* POSTED PARAMS IN REQUEST BODY: */

	Map<String, Object> posted();

	<T extends Serializable> T posted(String name);

	<T extends Serializable> T posted(String name, T defaultValue);

	/* POSTED FILES IN REQUEST BODY: */

	Map<String, byte[]> files();

	byte[] file(String name);

	byte[] file(String name, byte[] defaultValue);

	/* REQUEST DATA (URL PARAMS + POSTED DATA): */

	/**
	 * Data includes params + posted.
	 */
	Map<String, Object> data();

	/**
	 * Data includes params + posted.
	 */
	<T> T data(String name);

	/**
	 * Data includes params + posted.
	 */
	<T> T data(String name, T defaultValue);

	/* CUSTOM REQUEST ATTRIBUTES: */

	Map<String, Object> attrs();

	<T> T attr(String name);

	<T> T attr(String name, T defaultValue);

	/* SESSION: */

	boolean hasSession();

	String sessionId();

	Map<String, Serializable> session();

	<T extends Serializable> T session(String name);

	<T extends Serializable> T session(String name, T defaultValue);

	/* COOKIEPACK: */

	boolean hasCookiepack();

	Map<String, Serializable> cookiepack();

	<T extends Serializable> T cookiepack(String name);

	<T extends Serializable> T cookiepack(String name, T defaultValue);

	/* RESPONSE: */

	Resp response();

	OutputStream out();

	Req done();

	boolean isDone();

	Req async();

	boolean isAsync();

}
