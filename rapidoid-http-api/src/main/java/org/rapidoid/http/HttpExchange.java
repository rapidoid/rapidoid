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

import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.annotation.TransactionMode;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public interface HttpExchange extends HttpExchangeHeaders {

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

	Map<String, Object> getSessionById(String sessionId);

	<T> T session(String name);

	<T> T session(String name, T defaultValue);

	void sessionSet(String name, Object value);

	<T> T sessionGetOrCreate(String name, Class<T> valueClass, Object... constructorArgs);

	String sessionId();

	void closeSession();

	void clearSession(String sessionId);

	boolean hasSession();

	boolean hasSession(String sessionId);

	String pathSegment(int segmentIndex);

	boolean isGetReq();

	boolean isPostReq();

	TransactionMode getTransactionMode();

	void setTransactionMode(TransactionMode txMode);

	void setClassLoader(ClassLoader classLoader);

	ClassLoader getClassLoader();

	/* HELPERS: */

	String constructUrl(String path);

	byte[] sessionSerialize();

	void sessionDeserialize(byte[] bytes);

	<T> T extra(Object key);

	void extra(Object key, Object value);

	String realAddress();

}
