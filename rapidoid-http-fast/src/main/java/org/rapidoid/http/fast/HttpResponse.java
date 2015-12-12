package org.rapidoid.http.fast;

/*
 * #%L
 * rapidoid-http-fast
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.MediaType;
import org.rapidoid.http.Req;
import org.rapidoid.http.Response;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.0.2")
public class HttpResponse implements Response {

	private final ReqImpl req;

	private Object content = null;

	private int code = 200;

	private MediaType contentType = MediaType.HTML_UTF_8;

	private final Map<String, String> headers = Collections.synchronizedMap(new HashMap<String, String>());

	private final Map<String, String> cookies = Collections.synchronizedMap(new HashMap<String, String>());

	private String redirect = null;

	private String view = null;

	public HttpResponse(ReqImpl req) {
		this.req = req;
	}

	@Override
	public synchronized Response content(Object content) {
		ensureCanChange();
		this.content = content;
		return this;
	}

	@Override
	public synchronized Object content() {
		return this.content;
	}

	@Override
	public synchronized Response code(int code) {
		ensureCanChange();
		this.code = code;
		return this;
	}

	@Override
	public synchronized int code() {
		return this.code;
	}

	@Override
	public Map<String, String> headers() {
		return isReadOnly() ? Collections.unmodifiableMap(this.headers) : this.headers;
	}

	@Override
	public Map<String, String> cookies() {
		return isReadOnly() ? Collections.unmodifiableMap(this.cookies) : this.cookies;
	}

	@Override
	public synchronized Response contentType(MediaType contentType) {
		ensureCanChange();
		this.contentType = contentType;
		return this;
	}

	@Override
	public synchronized MediaType contentType() {
		return this.contentType;
	}

	@Override
	public synchronized Response redirect(String redirect) {
		ensureCanChange();
		this.redirect = redirect;
		return this;
	}

	@Override
	public synchronized String redirect() {
		return this.redirect;
	}

	private void ensureCanChange() {
		U.must(!req.isDone(), "The request was already processed, so the response can't be changed now!");
		U.must(!req.isRendering(), "The response rendering has already started, so the response can't be changed now!");
	}

	private boolean isReadOnly() {
		return req.isRendering() || req.isDone();
	}

	@Override
	public Req done() {
		return req.done();
	}

	@Override
	public Response html(Object content) {
		return contentType(MediaType.HTML_UTF_8).content(content);
	}

	@Override
	public Response plain(Object content) {
		return contentType(MediaType.PLAIN_TEXT_UTF_8).content(content);
	}

	@Override
	public Response json(Object content) {
		return contentType(MediaType.JSON_UTF_8).content(content);
	}

	@Override
	public Response binary(Object content) {
		return contentType(MediaType.BINARY).content(content);
	}

	@Override
	public synchronized String view() {
		return view;
	}

	@Override
	public synchronized Response view(String view) {
		this.view = view;
		return this;
	}

	@Override
	public Req request() {
		return req;
	}

}
