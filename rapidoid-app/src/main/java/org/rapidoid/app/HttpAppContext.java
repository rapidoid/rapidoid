package org.rapidoid.app;

/*
 * #%L
 * rapidoid-app
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
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Map;

import org.rapidoid.buffer.Buf;
import org.rapidoid.data.BinaryMultiData;
import org.rapidoid.data.Data;
import org.rapidoid.data.MultiData;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.http.HttpExchangeBody;
import org.rapidoid.http.HttpExchangeException;
import org.rapidoid.http.HttpExchangeHeaders;
import org.rapidoid.http.HttpHeader;
import org.rapidoid.net.impl.ConnState;
import org.rapidoid.net.impl.RapidoidHelper;
import org.rapidoid.net.mime.MediaType;

public class HttpAppContext implements AppContext {

	private final HttpExchange x;

	public HttpAppContext(HttpExchange x) {
		this.x = x;
	}

	public String readln() {
		return x.readln();
	}

	public boolean isInitial() {
		return x.isInitial();
	}

	public String readN(int count) {
		return x.readN(count);
	}

	public HttpExchange restart() {
		return x.restart();
	}

	public String address() {
		return x.address();
	}

	public ConnState state() {
		return x.state();
	}

	public HttpExchangeBody write(String s) {
		return x.write(s);
	}

	public InetSocketAddress getAddress() {
		return x.getAddress();
	}

	public HttpExchangeHeaders startResponse(int httpResponseCode) {
		return x.startResponse(httpResponseCode);
	}

	public HttpExchangeBody writeln(String s) {
		return x.writeln(s);
	}

	public Buf input() {
		return x.input();
	}

	public Buf output() {
		return x.output();
	}

	public HttpExchangeBody write(byte[] bytes) {
		return x.write(bytes);
	}

	public long connId() {
		return x.connId();
	}

	public RapidoidHelper helper() {
		return x.helper();
	}

	public boolean isAsync() {
		return x.isAsync();
	}

	public HttpExchangeBody write(byte[] bytes, int offset, int length) {
		return x.write(bytes, offset, length);
	}

	public HttpExchangeHeaders response(int httpResponseCode) {
		return x.response(httpResponseCode);
	}

	public HttpExchangeBody sendFile(File file) {
		return x.sendFile(file);
	}

	@Override
	public HttpExchangeBody sendFile(MediaType mediaType, byte[] bytes) {
		return x.sendFile(mediaType, bytes);
	}

	public HttpExchange close() {
		return x.close();
	}

	public HttpExchange closeIf(boolean condition) {
		return x.closeIf(condition);
	}

	public HttpExchangeException redirect(String url) {
		return x.redirect(url);
	}

	public HttpExchangeBody write(ByteBuffer buf) {
		return x.write(buf);
	}

	public HttpExchangeHeaders response(int httpResponseCode, String response) {
		return x.response(httpResponseCode, response);
	}

	public HttpExchangeBody write(File file) {
		return x.write(file);
	}

	public HttpExchangeException goBack(int steps) {
		return x.goBack(steps);
	}

	public HttpExchangeBody writeJSON(Object value) {
		return x.writeJSON(value);
	}

	public HttpExchangeBody send() {
		return x.send();
	}

	public HttpExchangeBody addToPageStack() {
		return x.addToPageStack();
	}

	public HttpExchangeHeaders response(int httpResponseCode, String response, Throwable err) {
		return x.response(httpResponseCode, response, err);
	}

	public HttpExchangeBody async() {
		return x.async();
	}

	public String verb() {
		return x.verb();
	}

	public OutputStream outputStream() {
		return x.outputStream();
	}

	public Data verb_() {
		return x.verb_();
	}

	public String uri() {
		return x.uri();
	}

	public HttpExchangeHeaders errorResponse(Throwable err) {
		return x.errorResponse(err);
	}

	public Data uri_() {
		return x.uri_();
	}

	public HttpExchangeBody done() {
		return x.done();
	}

	public String path() {
		return x.path();
	}

	public Data path_() {
		return x.path_();
	}

	public HttpExchangeException notFound() {
		return x.notFound();
	}

	public String subpath() {
		return x.subpath();
	}

	public Data subpath_() {
		return x.subpath_();
	}

	public HttpExchangeException error() {
		return x.error();
	}

	public String query() {
		return x.query();
	}

	public Data query_() {
		return x.query_();
	}

	public HttpExchangeHeaders plain() {
		return x.plain();
	}

	public String protocol() {
		return x.protocol();
	}

	public Data protocol_() {
		return x.protocol_();
	}

	public HttpExchangeHeaders html() {
		return x.html();
	}

	public String body() {
		return x.body();
	}

	public HttpExchangeHeaders json() {
		return x.json();
	}

	public Data body_() {
		return x.body_();
	}

	public String host() {
		return x.host();
	}

	public HttpExchangeHeaders binary() {
		return x.binary();
	}

	public Data host_() {
		return x.host_();
	}

	public Map<String, String> params() {
		return x.params();
	}

	public HttpExchangeHeaders download(String filename) {
		return x.download(filename);
	}

	public MultiData params_() {
		return x.params_();
	}

	public String param(String name) {
		return x.param(name);
	}

	public HttpExchangeHeaders addHeader(byte[] name, byte[] value) {
		return x.addHeader(name, value);
	}

	public String param(String name, String defaultValue) {
		return x.param(name, defaultValue);
	}

	public HttpExchangeHeaders addHeader(HttpHeader name, String value) {
		return x.addHeader(name, value);
	}

	public Map<String, String> headers() {
		return x.headers();
	}

	public MultiData headers_() {
		return x.headers_();
	}

	public HttpExchangeHeaders setCookie(String name, String value, String... extras) {
		return x.setCookie(name, value, extras);
	}

	public String header(String name) {
		return x.header(name);
	}

	public String header(String name, String defaultValue) {
		return x.header(name, defaultValue);
	}

	public HttpExchangeHeaders setContentType(MediaType contentType) {
		return x.setContentType(contentType);
	}

	public Map<String, String> cookies() {
		return x.cookies();
	}

	public MultiData cookies_() {
		return x.cookies_();
	}

	public HttpExchangeHeaders sessionSet(String name, Object value) {
		return x.sessionSet(name, value);
	}

	public String cookie(String name) {
		return x.cookie(name);
	}

	public String cookie(String name, String defaultValue) {
		return x.cookie(name, defaultValue);
	}

	public HttpExchangeHeaders closeSession() {
		return x.closeSession();
	}

	public Map<String, String> data() {
		return x.data();
	}

	public HttpExchangeHeaders accessDeniedIf(boolean accessDeniedCondition) {
		return x.accessDeniedIf(accessDeniedCondition);
	}

	public MultiData data_() {
		return x.data_();
	}

	public String data(String name) {
		return x.data(name);
	}

	public HttpExchangeHeaders authorize(Class<?> clazz) {
		return x.authorize(clazz);
	}

	public String data(String name, String defaultValue) {
		return x.data(name, defaultValue);
	}

	public String sessionId() {
		return x.sessionId();
	}

	public Map<String, byte[]> files() {
		return x.files();
	}

	public boolean hasSession() {
		return x.hasSession();
	}

	public BinaryMultiData files_() {
		return x.files_();
	}

	public int responseCode() {
		return x.responseCode();
	}

	public byte[] file(String name) {
		return x.file(name);
	}

	public String redirectUrl() {
		return x.redirectUrl();
	}

	public byte[] file(String name, byte[] defaultValue) {
		return x.file(name, defaultValue);
	}

	public boolean serveStatic() {
		return x.serveStatic();
	}

	public Map<String, Object> session() {
		return x.session();
	}

	public <T> T session(String name) {
		return x.session(name);
	}

	public <T> T session(String name, T defaultValue) {
		return x.session(name, defaultValue);
	}

	public <T> T sessionGetOrCreate(String name, Class<T> valueClass, Object... constructorArgs) {
		return x.sessionGetOrCreate(name, valueClass, constructorArgs);
	}

	public String pathSegment(int segmentIndex) {
		return x.pathSegment(segmentIndex);
	}

	public boolean isGetReq() {
		return x.isGetReq();
	}

	public boolean isPostReq() {
		return x.isPostReq();
	}

	public String constructUrl(String path) {
		return x.constructUrl(path);
	}

	public byte[] sessionSerialize() {
		return x.sessionSerialize();
	}

	public void sessionDeserialize(byte[] bytes) {
		x.sessionDeserialize(bytes);
	}

	@Override
	public HttpExchangeHeaders clearSession() {
		return x.clearSession();
	}

}
