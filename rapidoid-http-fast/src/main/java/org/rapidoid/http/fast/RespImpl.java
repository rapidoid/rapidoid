package org.rapidoid.http.fast;

/*
 * #%L
 * rapidoid-http-fast
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.MediaType;
import org.rapidoid.http.Req;
import org.rapidoid.http.Resp;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.0.x")
public class RespImpl implements Resp {

	private final ReqImpl req;

	private volatile Object content = null;

	private volatile Object body = null;

	private volatile int code = 200;

	private volatile MediaType contentType = MediaType.HTML_UTF_8;

	private final Map<String, String> headers = Collections.synchronizedMap(new HashMap<String, String>());

	private final Map<String, String> cookies = Collections.synchronizedMap(new HashMap<String, String>());

	private volatile String redirect = null;

	private volatile String filename = null;

	private volatile File file = null;

	private volatile String view = null;

	public RespImpl(ReqImpl req) {
		this.req = req;
	}

	@Override
	public synchronized Resp content(Object content) {
		ensureCanChange();
		this.content = content;
		return this;
	}

	@Override
	public synchronized Object content() {
		return this.content;
	}

	@Override
	public synchronized Resp body(byte[] body) {
		ensureCanChange();
		this.body = body;
		return this;
	}

	@Override
	public synchronized Resp body(ByteBuffer body) {
		ensureCanChange();
		this.body = body;
		return this;
	}

	@Override
	public synchronized Object body() {
		return this.body;
	}

	@Override
	public synchronized Resp code(int code) {
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
	public synchronized Resp contentType(MediaType contentType) {
		ensureCanChange();
		this.contentType = contentType;
		return this;
	}

	@Override
	public synchronized MediaType contentType() {
		return this.contentType;
	}

	@Override
	public synchronized Resp redirect(String redirect) {
		ensureCanChange();
		this.redirect = redirect;
		return this;
	}

	@Override
	public synchronized String redirect() {
		return this.redirect;
	}

	@Override
	public synchronized Resp filename(String filename) {
		ensureCanChange();
		this.filename = filename;
		return this;
	}

	@Override
	public synchronized String filename() {
		return this.filename;
	}

	@Override
	public synchronized Resp file(File file) {
		ensureCanChange();
		this.file = file;
		return this;
	}

	@Override
	public synchronized File file() {
		return this.file;
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
	public Resp html(Object content) {
		return contentType(MediaType.HTML_UTF_8).content(content);
	}

	@Override
	public Resp plain(Object content) {
		return contentType(MediaType.PLAIN_TEXT_UTF_8).content(content);
	}

	@Override
	public Resp json(Object content) {
		return contentType(MediaType.JSON_UTF_8).content(content);
	}

	@Override
	public Resp binary(Object content) {
		return contentType(MediaType.BINARY).content(content);
	}

	@Override
	public synchronized String view() {
		return view;
	}

	@Override
	public synchronized Resp view(String view) {
		this.view = view;
		return this;
	}

	@Override
	public Req request() {
		return req;
	}

}
