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
@Since("2.0.0")
public interface HttpExchange extends Req, Resp, Runnable {

	/* REQUEST: */

	String name();

	String verbAndResourceName();

	/* RESPONSE: */

	Map<String, String> errors();

	boolean hasErrors();

	// e.g. redirect
	HttpSuccessException error();

	// due to async() web handling option, it ain't over till the fat lady sings "done"
	HttpExchange async();

	HttpExchange addToPageStack();

	HttpSuccessException goBack(@P("steps") int steps);

	HttpExchange authorize(@P("clazz") Class<?> clazz);

	/* SESSION SCOPE: */

	Map<String, Serializable> session();

	<T extends Serializable> T session(String name);

	<T extends Serializable> T session(String name, T defaultValue);

	<T extends Serializable> T sessionGetOrCreate(String name, Class<T> valueClass, Object... constructorArgs);

	/* LOCAL SCOPE: */

	Map<String, Serializable> locals();

	<T extends Serializable> T local(String key);

	<T extends Serializable> T local(String key, T defaultValue);

	<T extends Serializable> T localGetOrCreate(String name, Class<T> valueClass, Object... constructorArgs);

	/* COOKIEPACK SCOPE: */

	Map<String, Serializable> cookiepack();

	<T extends Serializable> T cookiepack(String name);

	<T extends Serializable> T cookiepack(String name, T defaultValue);

	<T extends Serializable> T cookiepackGetOrCreate(String name, Class<T> valueClass, Object... constructorArgs);

	/* EXTRAS: */

	<T> T persister();

	Map<String, Object> model();

	Runnable asAsyncJob(Handler handler);

	String dbQuery();

	boolean isGetReq();

	boolean isPostReq();

	boolean isDevMode();

	String pathSegment(int segmentIndex);

	String[] pathSegments();

	String realIpAddress();

	String subpath();

	String home();

	<T> T attrGetOrCreate(String name, Class<T> valueClass, Object... constructorArgs);

	String sessionId();

	String query();

	String protocol();

}
