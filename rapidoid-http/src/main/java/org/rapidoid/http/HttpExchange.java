package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http
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
import org.rapidoid.data.BinaryMultiData;
import org.rapidoid.data.Data;
import org.rapidoid.data.MultiData;
import org.rapidoid.net.abstracts.CtxFull;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public interface HttpExchange extends CtxFull<HttpExchange, HttpExchangeBody>, HttpExchangeHeaders {

	/* REQUEST METHODS: */

	String verb();

	Data verb_();

	String uri();

	Data uri_();

	String path();

	Data path_();

	String subpath();

	Data subpath_();

	String query();

	Data query_();

	String protocol();

	Data protocol_();

	String body();

	Data body_();

	String host();

	Data host_();

	Map<String, String> params();

	MultiData params_();

	String param(String name);

	String param(String name, String defaultValue);

	Map<String, String> headers();

	MultiData headers_();

	String header(String name);

	String header(String name, String defaultValue);

	Map<String, String> cookies();

	MultiData cookies_();

	String cookie(String name);

	String cookie(String name, String defaultValue);

	Map<String, String> data();

	MultiData data_();

	String data(String name);

	String data(String name, String defaultValue);

	Map<String, byte[]> files();

	BinaryMultiData files_();

	byte[] file(String name);

	byte[] file(String name, byte[] defaultValue);

	Map<String, Object> session();

	Map<String, Object> getSessionById(String sessionId);

	<T> T session(String name);

	<T> T session(String name, T defaultValue);

	<T> T sessionGetOrCreate(String name, Class<T> valueClass, Object... constructorArgs);

	String pathSegment(int segmentIndex);

	boolean isGetReq();

	boolean isPostReq();

	/* HELPERS: */

	String constructUrl(String path);

	byte[] sessionSerialize();

	void sessionDeserialize(byte[] bytes);

	<T> T extra(Object key);

	void extra(Object key, Object value);

}
