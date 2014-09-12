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

import org.rapidoid.data.Data;
import org.rapidoid.data.KeyValueRanges;
import org.rapidoid.data.MultiData;
import org.rapidoid.data.Range;
import org.rapidoid.data.Ranges;
import org.rapidoid.net.Exchange;
import org.rapidoid.util.U;
import org.rapidoid.wrap.Bool;

public class WebExchangeImpl extends Exchange implements WebExchange {

	private final static HttpParser PARSER = U.inject(HttpParser.class);

	static final int WHOLE = 1;
	static final int HEADER = 2;
	static final int BODY_PART = 3;

	final Range uri = new Range();
	final Range verb = new Range();
	final Range path = new Range();
	final Range query = new Range();
	final Range protocol = new Range();

	final Ranges headers = new Ranges(50);

	private final KeyValueRanges params = new KeyValueRanges(50);
	private final KeyValueRanges headersKV = new KeyValueRanges(50);

	final Range body = new Range();

	final Bool isGet = new Bool();
	final Bool isKeepAlive = new Bool();

	int total;

	private boolean parsedParams;
	private boolean parsedHeaders;

	byte respType;

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

	public WebExchangeImpl() {
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

		parsedParams = false;
		parsedHeaders = false;

		total = -1;
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
				PARSER.parseHeadersIntoKV(input(), headers, headersKV);
			}

			parsedHeaders = true;
		}

		return _headers;
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
	public void done() {
		conn.complete(this, !isKeepAlive.value);
	}

	@Override
	public String toString() {
		return "WebExchange [uri=" + uri_() + ", verb=" + verb_() + ", path=" + path_() + ", subpath=" + subpath_()
				+ ", query=" + query_() + ", protocol=" + protocol_() + ", body=" + body_() + ", headers=" + headers_()
				+ ", params=" + params_();
	}

}
