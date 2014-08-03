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
import org.rapidoid.net.Exchange;
import org.rapidoid.util.U;

public class WebExchangeImpl extends Exchange implements WebExchange {

	private final static HttpParser PARSER = U.singleton(HttpParser.class);

	static final int WHOLE = 1;
	static final int HEADER = 2;
	static final int BODY_PART = 3;

	final Range verb = new Range();
	final Range path = new Range();
	final Range query = new Range();
	final Range protocol = new Range();

	final KeyValueRanges params = new KeyValueRanges(50);
	final KeyValueRanges headers = new KeyValueRanges(50);

	final Range body = new Range();

	int total;

	private boolean parsedParams;

	boolean isKeepAlive = false;

	byte respType;

	final Range multipartBoundary = new Range();

	/**********/

	private final Range subpathRange = new Range();

	private final Data _body;
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
		this._verb = data(verb);
		this._path = decodedData(path);
		this._subpath = decodedData(subpathRange);
		this._query = decodedData(query);
		this._protocol = data(protocol);
		this._params = multiData(params);
		this._headers = multiData(headers);
	}

	@Override
	public void reset() {
		super.reset();

		isKeepAlive = false;

		verb.reset();
		path.reset();
		query.reset();
		protocol.reset();
		body.reset();
		multipartBoundary.reset();

		params.reset();
		headers.reset();

		parsedParams = false;
		total = -1;
	}

	@Override
	public MultiData params() {
		if (!parsedParams) {
			if (!query.isEmpty()) {
				PARSER.parseParams(input(), params, query);
			}

			parsedParams = true;
		}

		return _params;
	}

	@Override
	public MultiData headers() {
		return _headers;
	}

	public Data subpath() {
		return _subpath;
	}

	@Override
	public Data body() {
		return _body;
	}

	@Override
	public Data verb() {
		return _verb;
	}

	@Override
	public Data path() {
		return _path;
	}

	@Override
	public Data protocol() {
		return _protocol;
	}

	@Override
	public Data query() {
		return _query;
	}

	public void setSubpath(int start, int end) {
		subpathRange.setStartEnd(start, end);
	}

	@Override
	public void done() {
		conn.complete(this, !isKeepAlive);
	}

	public void setKeepAlive(boolean isKeepAlive) {
		this.isKeepAlive = isKeepAlive;
	}

}
