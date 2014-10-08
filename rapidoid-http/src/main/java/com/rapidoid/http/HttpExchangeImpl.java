package com.rapidoid.http;

/*
 * #%L
 * rapidoid-http
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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
import java.nio.ByteBuffer;
import java.util.Map;

import org.rapidoid.data.BinaryMultiData;
import org.rapidoid.data.Data;
import org.rapidoid.data.KeyValueRanges;
import org.rapidoid.data.MultiData;
import org.rapidoid.data.Range;
import org.rapidoid.data.Ranges;
import org.rapidoid.net.impl.ConnState;
import org.rapidoid.net.impl.DefaultExchange;
import org.rapidoid.net.mime.MediaType;
import org.rapidoid.util.Constants;
import org.rapidoid.util.U;
import org.rapidoid.wrap.Bool;

public class HttpExchangeImpl extends DefaultExchange<HttpExchange, HttpExchangeBody> implements HttpExchange, Constants {

	private final static HttpParser PARSER = U.singleton(HttpParser.class);

	private static final byte[] HEADER_SEP = ": ".getBytes();

	final Range uri = new Range();
	final Range verb = new Range();
	final Range path = new Range();
	final Range query = new Range();
	final Range protocol = new Range();

	final Ranges headers = new Ranges(50);

	private final KeyValueRanges params = new KeyValueRanges(50);
	private final KeyValueRanges headersKV = new KeyValueRanges(50);
	private final KeyValueRanges cookies = new KeyValueRanges(50);
	private final KeyValueRanges data = new KeyValueRanges(50);
	private final KeyValueRanges files = new KeyValueRanges(50);

	final Range body = new Range();
	final Bool isGet = new Bool();
	final Bool isKeepAlive = new Bool();

	private boolean parsedParams;
	private boolean parsedHeaders;
	private boolean parsedBody;

	int total;
	public int bodyPos;
	private boolean writesBody;
	private boolean hasContentType;

	final Range multipartBoundary = new Range();

	/**********/

	private final Range subpathRange = new Range();

	private final Data _body;
	private final Data _uri;
	private final Data _verb;
	private final Data _path;
	private final Data _subpath;
	private final Data _query;
	private final Data _protocol;
	private final MultiData _params;
	private final MultiData _headers;
	private final MultiData _cookies;
	private final MultiData _data;
	private final BinaryMultiData _files;

	public HttpExchangeImpl() {
		reset();

		this._body = data(body);
		this._uri = data(uri);
		this._verb = data(verb);
		this._path = decodedData(path);
		this._subpath = decodedData(subpathRange);
		this._query = decodedData(query);
		this._protocol = data(protocol);
		this._params = multiData(params);
		this._headers = multiData(headersKV);
		this._cookies = multiData(cookies);
		this._data = multiData(data);
		this._files = binaryMultiData(files);
	}

	@Override
	public void reset() {
		super.reset();

		isGet.value = false;
		isKeepAlive.value = false;

		verb.reset();
		uri.reset();
		path.reset();
		query.reset();
		protocol.reset();
		body.reset();
		multipartBoundary.reset();

		params.reset();
		headersKV.reset();
		headers.reset();
		cookies.reset();
		data.reset();
		files.reset();

		parsedParams = false;
		parsedHeaders = false;
		parsedBody = false;

		total = -1;
		writesBody = false;
		bodyPos = -1;
		hasContentType = false;
	}

	@Override
	public MultiData params_() {
		if (!parsedParams) {
			if (!query.isEmpty()) {
				PARSER.parseParams(input(), params, query_().range());
			}

			parsedParams = true;
		}

		return _params;
	}

	@Override
	public MultiData headers_() {
		if (!parsedHeaders) {
			if (!headers.isEmpty()) {
				PARSER.parseHeadersIntoKV(input(), headers, headersKV, cookies, helper());
			}

			parsedHeaders = true;
		}

		return _headers;
	}

	@Override
	public MultiData cookies_() {
		if (!parsedHeaders) {
			if (!headers.isEmpty()) {
				PARSER.parseHeadersIntoKV(input(), headers, headersKV, cookies, helper());
			}

			parsedHeaders = true;
		}

		return _cookies;
	}

	@Override
	public MultiData data_() {
		if (!parsedBody) {
			PARSER.parseBody(input(), headersKV, body, data, files, helper());
			parsedBody = true;
		}

		return _data;
	}

	@Override
	public BinaryMultiData files_() {
		if (!parsedBody) {
			PARSER.parseBody(input(), headersKV, body, data, files, helper());
			parsedBody = true;
		}

		return _files;
	}

	public Data subpath_() {
		return _subpath;
	}

	@Override
	public Data body_() {
		return _body;
	}

	@Override
	public Data uri_() {
		return _uri;
	}

	@Override
	public Data verb_() {
		return _verb;
	}

	@Override
	public Data path_() {
		return _path;
	}

	@Override
	public Data protocol_() {
		return _protocol;
	}

	@Override
	public Data query_() {
		return _query;
	}

	public void setSubpath(int start, int end) {
		subpathRange.setInterval(start, end);
	}

	@Override
	public HttpExchangeImpl done() {
		conn.done();
		return this;
	}

	@Override
	public String toString() {
		return "WebExchange [uri=" + uri() + ", verb=" + verb() + ", path=" + path() + ", subpath=" + subpath()
				+ ", query=" + query() + ", protocol=" + protocol() + ", body=" + body() + ", headers=" + headers()
				+ ", params=" + params() + ", cookies=" + cookies() + ", data=" + data() + ", files=" + files() + "]";
	}

	@Override
	public String verb() {
		return verb_().get();
	}

	@Override
	public String uri() {
		return uri_().get();
	}

	@Override
	public String path() {
		return path_().get();
	}

	@Override
	public String subpath() {
		return subpath_().get();
	}

	@Override
	public String query() {
		return query_().get();
	}

	@Override
	public String protocol() {
		return protocol_().get();
	}

	@Override
	public String body() {
		return body_().get();
	}

	@Override
	public Map<String, String> params() {
		return params_().get();
	}

	@Override
	public String param(String name) {
		return params_().get(name);
	}

	@Override
	public Map<String, String> headers() {
		return headers_().get();
	}

	@Override
	public String header(String name) {
		return headers_().get(name);
	}

	@Override
	public Map<String, String> cookies() {
		return cookies_().get();
	}

	@Override
	public String cookie(String name) {
		return cookies_().get(name);
	}

	@Override
	public Map<String, String> data() {
		return data_().get();
	}

	@Override
	public String data(String name) {
		return data_().get(name);
	}

	@Override
	public Map<String, byte[]> files() {
		return files_().get();
	}

	@Override
	public byte[] file(String name) {
		return files_().get(name);
	}

	@Override
	public HttpExchange addHeader(byte[] name, byte[] value) {
		super.write(name);
		super.write(HEADER_SEP);
		super.write(value);
		super.write(CR_LF);
		return this;
	}

	@Override
	public HttpExchange addHeader(HttpHeader name, String value) {
		addHeader(name.getBytes(), value.getBytes());
		return this;
	}

	@Override
	public HttpExchange setCookie(String name, String value) {
		addHeader(HttpHeader.SET_COOKIE, name + "=" + value);
		return this;
	}

	@Override
	public synchronized HttpExchange setContentType(MediaType MediaType) {
		U.must(!hasContentType);
		hasContentType = true;

		addHeader(HttpHeader.CONTENT_TYPE.getBytes(), MediaType.getBytes());
		return this;
	}

	@Override
	public HttpExchange plain() {
		return setContentType(MediaType.PLAIN_TEXT_UTF_8);
	}

	@Override
	public HttpExchange html() {
		return setContentType(MediaType.HTML_UTF_8);
	}

	@Override
	public HttpExchange json() {
		return setContentType(MediaType.JSON_UTF_8);
	}

	@Override
	public HttpExchange binary() {
		return setContentType(MediaType.BINARY);
	}

	@Override
	public HttpExchange download(String filename) {
		addHeader(HttpHeader.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
		addHeader(HttpHeader.CACHE_CONTROL, "private");
		return binary();
	}

	private synchronized void ensureBodyWrite() {
		if (!writesBody) {
			writesBody = true;
			write(CR_LF);
			bodyPos = output().size();
		}
	}

	@Override
	public HttpExchangeBody write(String s) {
		ensureBodyWrite();
		return super.write(s);
	}

	@Override
	public HttpExchangeBody write(byte[] bytes) {
		ensureBodyWrite();
		return super.write(bytes);
	}

	@Override
	public HttpExchangeBody write(byte[] bytes, int offset, int length) {
		ensureBodyWrite();
		return super.write(bytes, offset, length);
	}

	@Override
	public HttpExchangeBody write(ByteBuffer buf) {
		ensureBodyWrite();
		return super.write(buf);
	}

	@Override
	public HttpExchangeBody write(File file) {
		if (!hasContentType()) {
			download(file.getName());
		}

		ensureBodyWrite();
		return super.write(file);
	}

	@Override
	public HttpExchangeBody writeJSON(Object value) {
		if (!hasContentType()) {
			json();
		}

		ensureBodyWrite();
		return super.writeJSON(value);
	}

	@Override
	public boolean isInitial() {
		return conn.isInitial();
	}

	@Override
	public ConnState state() {
		return conn.state();
	}

	public boolean hasContentType() {
		return hasContentType;
	}

	@Override
	public HttpExchangeBody sendFile(File file) {
		U.must(file.exists());
		setContentType(MediaType.getByFileName(file.getAbsolutePath()));
		write(file);
		return this;
	}
}
