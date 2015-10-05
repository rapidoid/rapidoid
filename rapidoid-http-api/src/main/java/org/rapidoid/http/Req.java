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

import java.io.Serializable;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.P;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("4.2.0")
public interface Req {

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

	Map<String, Object> posted();

	<T extends Serializable> T posted(@P("name") String name);

	<T extends Serializable> T posted(@P("name") String name, @P("defaultValue") T defaultValue);

	Map<String, byte[]> files();

	byte[] file(@P("name") String name);

	byte[] file(@P("name") String name, @P("defaultValue") byte[] defaultValue);

	/**
	 * Data includes params + posted.
	 */
	Map<String, Object> data();

	/**
	 * Data includes params + posted.
	 */
	<T> T data(@P("name") String name);

	/**
	 * Data includes params + posted.
	 */
	<T> T data(@P("name") String name, @P("defaultValue") T defaultValue);

	String home();

	String[] pathSegments();

	String pathSegment(@P("segmentIndex") int segmentIndex);

	String realIpAddress();

	boolean isGetReq();

	boolean isPostReq();

	boolean isDevMode();

	long requestId();

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

}
