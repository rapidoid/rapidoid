package org.rapidoid.http.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.MediaType;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.ctx.UserInfo;
import org.rapidoid.http.HttpUtils;
import org.rapidoid.http.Req;
import org.rapidoid.http.Resp;
import org.rapidoid.http.Screen;
import org.rapidoid.http.customize.LoginProvider;
import org.rapidoid.http.customize.RolesProvider;
import org.rapidoid.http.impl.ReqImpl;
import org.rapidoid.http.impl.ResponseRenderer;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.io.File;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

@Authors("Nikolche Mihajlovski")
@Since("5.0.x")
public class RespImpl extends RapidoidThing implements Resp, Screen {

	private final ReqImpl req;

	private volatile Object content = null;

	private volatile Object body = null;

	private volatile Object raw = null;

	private volatile int code = 200;

	private volatile MediaType contentType = MediaType.HTML_UTF_8;

	private final Map<String, String> headers = Collections.synchronizedMap(new HashMap<String, String>());

	private final Map<String, String> cookies = Collections.synchronizedMap(new HashMap<String, String>());

	private final Map<String, Object> model = Collections.synchronizedMap(new HashMap<String, Object>());

	private volatile String redirect = null;

	private volatile String filename = null;

	private volatile File file = null;

	private volatile String view = null;

	private volatile boolean mvc = false;

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
	public synchronized Resp raw(byte[] raw) {
		ensureCanChange();
		this.raw = raw;
		return this;
	}

	@Override
	public synchronized Resp raw(ByteBuffer raw) {
		ensureCanChange();
		this.raw = raw;
		return this;
	}

	@Override
	public synchronized Object raw() {
		return this.raw;
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
	public Map<String, Object> model() {
		return isReadOnly() ? Collections.unmodifiableMap(this.model) : this.model;
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
		return view != null ? view : HttpUtils.defaultView(req.path());
	}

	@Override
	public synchronized Resp view(String view) {
		this.view = view;
		return this;
	}

	@Override
	public synchronized boolean mvc() {
		return mvc;
	}

	@Override
	public synchronized Resp mvc(boolean mvc) {
		this.mvc = mvc;
		return this;
	}

	@Override
	public Req request() {
		return req;
	}

	@Override
	public boolean login(String username, String password) {
		LoginProvider loginProvider = req.http().custom().loginProvider();
		U.must(loginProvider != null, "A login provider wasn't set!");

		RolesProvider rolesProvider = req.http().custom().rolesProvider();
		U.must(rolesProvider != null, "A roles provider wasn't set!");

		boolean success;
		Set<String> roles;

		try {
			success = loginProvider.login(username, password);
			if (success) {
				roles = rolesProvider.getRolesForUser(username);
				Ctxs.ctx().setUser(new UserInfo(username, roles));
				request().cookiepack().put(HttpUtils._USER, username);
			}
		} catch (Throwable e) {
			throw U.rte("Login error!", e);
		}

		return success;
	}

	@Override
	public void logout() {
		Ctxs.ctx().setUser(UserInfo.ANONYMOUS);
		request().cookiepack().remove(HttpUtils._USER);
	}

	@Override
	public Screen screen() {
		return this;
	}

	@Override
	public OutputStream out() {
		U.must(content() == null, "The response content has already been set, so cannot write the response through OutputStream, too!");
		U.must(body() == null, "The response body has already been set, so cannot write the response through OutputStream, too!");
		U.must(raw() == null, "The raw response has already been set, so cannot write the response through OutputStream, too!");

		req.startRendering(code(), true);

		return req.channel().output().asOutputStream();
	}

	@Override
	public String toString() {
		return "RespImpl{" +
				(content != null ? "content=" + content : "") +
				(body != null ? ", body=" + body : "") +
				(raw != null ? ", raw=" + raw : "") +
				", code=" + code +
				(contentType != null ? ", contentType=" + contentType : "") +
				", headers=" + headers +
				", cookies=" + cookies +
				", model=" + model +
				(redirect != null ? ", redirect='" + redirect + '\'' : "") +
				(filename != null ? ", filename='" + filename + '\'' : "") +
				(file != null ? ", file=" + file : "") +
				(view != null ? ", view='" + view + '\'' : "") +
				", mvc=" + mvc +
				'}';
	}

	public byte[] renderToBytes() {
		if (mvc()) {
			return ResponseRenderer.render(req, this);

		} else if (content() != null) {
			return serializeResponseContent();

		} else if (body() != null) {
			return Msc.toBytes(body());

		} else {
			throw U.rte("There's nothing to render!");
		}
	}

	private byte[] serializeResponseContent() {
		return HttpUtils.responseToBytes(content(), contentType(), req.http().custom().jsonResponseRenderer());
	}

	@Override
	public Screen title(String title) {
		model().put("title", title);
		return this;
	}

	@Override
	public String title() {
		return (String) model().get("title");
	}

	@Override
	public Screen brand(Object brand) {
		model().put("brand", brand);
		return this;
	}

	@Override
	public Object brand() {
		return model().get("brand");
	}

	@Override
	public Screen menu(Object menu) {
		model().put("menu", menu);
		return this;
	}

	@Override
	public Object menu() {
		return model().get("menu");
	}

	@Override
	public Screen search(boolean search) {
		model().put("search", search);
		return this;
	}

	@Override
	public Boolean search() {
		return (Boolean) model().get("search");
	}

	@Override
	public Screen cdn(boolean cdn) {
		model().put("cdn", cdn);
		return this;
	}

	@Override
	public Boolean cdn() {
		return (Boolean) model().get("cdn");
	}

	@Override
	public Screen navbar(boolean navbar) {
		model().put("navbar", navbar);
		return this;
	}

	@Override
	public Boolean navbar() {
		return (Boolean) model().get("navbar");
	}

	@Override
	public Screen fluid(boolean fluid) {
		model().put("fluid", fluid);
		return this;
	}

	@Override
	public Boolean fluid() {
		return (Boolean) model().get("fluid");
	}

}
