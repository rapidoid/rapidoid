package org.rapidoid.http.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.cls.Cls;
import org.rapidoid.collection.Coll;
import org.rapidoid.config.BasicConfig;
import org.rapidoid.config.Conf;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.ctx.UserInfo;
import org.rapidoid.http.HttpUtils;
import org.rapidoid.http.MediaType;
import org.rapidoid.http.Req;
import org.rapidoid.http.Resp;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.customize.LoginProvider;
import org.rapidoid.http.customize.RolesProvider;
import org.rapidoid.io.IO;
import org.rapidoid.net.AsyncLogic;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.util.MscOpts;
import org.rapidoid.util.Tokens;
import org.rapidoid.web.Screen;
import org.rapidoid.web.ScreenBean;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/*
 * #%L
 * rapidoid-http-fast
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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
public class RespImpl extends RapidoidThing implements Resp {

	private final ReqImpl req;

	private volatile Object result = null;

	private volatile Object body = null;

	private volatile Object raw = null;

	private volatile int code = 200;

	private volatile MediaType contentType = HttpUtils.getDefaultContentType();

	private final Map<String, String> headers = Coll.synchronizedMap();

	private final Map<String, String> cookies = Coll.synchronizedMap();

	private final Map<String, Object> model = Coll.synchronizedMap();

	private volatile String redirect = null;

	private volatile String filename = null;

	private volatile File file = null;

	private volatile String view = null;

	private volatile boolean mvc = false;

	private volatile Screen screen;

	private volatile ChunkedResponse chunked;

	public RespImpl(ReqImpl req) {
		this.req = req;
	}

	@Override
	public synchronized Resp result(Object content) {
		ensureCanChange();
		this.result = content;
		return this;
	}

	@Override
	public synchronized Object result() {
		return this.result;
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
	public Resp header(String name, String value) {
		headers().put(name, value);
		return this;
	}

	@Override
	public Map<String, String> cookies() {
		return isReadOnly() ? Collections.unmodifiableMap(this.cookies) : this.cookies;
	}

	@Override
	public Resp cookie(String name, String value, String... extras) {

		if (U.notEmpty(extras)) {
			value += "; " + U.join("; ", extras);
		}

		if (!cookieContainsPath(extras)) {
			value += "; path=" + HttpUtils.cookiePath();
		}

		cookies().put(name, value);

		return this;
	}

	private static boolean cookieContainsPath(String[] extras) {
		for (String extra : extras) {
			if (extra.toLowerCase().startsWith("path=")) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Map<String, Serializable> session() {
		return request().session();
	}

	@Override
	public Resp session(String name, Serializable value) {
		session().put(name, value);
		return this;
	}

	@Override
	public Map<String, Serializable> token() {
		return request().token();
	}

	@Override
	public Resp token(String name, Serializable value) {
		token().put(name, value);
		return this;
	}

	@Override
	public Map<String, Object> model() {
		return isReadOnly() ? Collections.unmodifiableMap(this.model) : this.model;
	}

	@Override
	public Resp model(String name, Object value) {
		model().put(name, value);
		return this;
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
	public Resp done() {
		req.done();
		return this;
	}

	@Override
	public Resp html(Object content) {
		return contentType(MediaType.HTML_UTF_8).result(content);
	}

	@Override
	public Resp plain(Object content) {
		return contentType(MediaType.PLAIN_TEXT_UTF_8).result(content);
	}

	@Override
	public Resp json(Object content) {
		return contentType(MediaType.JSON).result(content);
	}

	@Override
	public Resp binary(Object content) {
		return contentType(MediaType.BINARY).result(content);
	}

	@Override
	public synchronized String view() {
		return view != null ? view : HttpUtils.viewName(req);
	}

	@Override
	public Resp noView() {
		return view("");
	}

	boolean hasCustomView() {
		return view != null;
	}

	@Override
	public synchronized Resp view(String view) {
		if (view != null) {
			HttpUtils.validateViewName(view);
			this.mvc(true);
		}

		this.view = view;
		return this;
	}

	@Override
	public synchronized boolean mvc() {
		return mvc;
	}

	@Override
	public synchronized Resp mvc(boolean mvc) {

		if (mvc) {
			U.must(MscOpts.hasRapidoidHTML(), "The rapidoid-html module must be included for the MVC feature!");
		}

		this.mvc = mvc;
		return this;
	}

	@Override
	public Req request() {
		return req;
	}

	@Override
	public boolean login(String username, String password) {

		LoginProvider loginProvider = Customization.of(req).loginProvider();
		U.must(loginProvider != null, "A login provider wasn't set!");

		RolesProvider rolesProvider = Customization.of(req).rolesProvider();
		U.must(rolesProvider != null, "A roles provider wasn't set!");

		req.tokenChanged.set(true);

		boolean success;

		try {
			success = loginProvider.login(req, username, password);

			if (success) {
				Set<String> roles = rolesProvider.getRolesForUser(req, username);

				long ttl = Conf.TOKEN.entry("ttl").or(0);
				long expiresOn = ttl > 0 ? U.time() + ttl : Long.MAX_VALUE;

				UserInfo user = new UserInfo(username, roles, null);
				Ctxs.required().setUser(user);

				request().token().put(Tokens._USER, username);
				request().token().put(Tokens._EXPIRES, expiresOn);
			}

		} catch (Throwable e) {
			throw U.rte("Login error!", e);
		}

		return success;
	}

	@Override
	public void logout() {
		HttpUtils.clearUserData(request());
		HttpUtils.setResponseTokenCookie(this, "");
		req.tokenChanged.set(true);
	}

	@Override
	public Screen screen() {
		if (screen == null) {
			synchronized (this) {
				if (screen == null) {
					screen = createScreen();
				}
			}
		}

		return screen;
	}

	@Override
	public void resume(AsyncLogic asyncLogic) {
		req.channel().resume(req.connectionId(), req.handle(), asyncLogic);
	}

	private Screen createScreen() {
		Screen screen = MscOpts.hasRapidoidGUI() ? GUIUtil.newPage() : new ScreenBean();
		initScreen(screen);
		return screen;
	}

	private void initScreen(Screen screen) {
		BasicConfig zone = HttpUtils.zone(req);

		String brand = zone.entry("brand").str().getOrNull();
		String title = zone.entry("title").str().getOrNull();

		String siteName = req.host();
		if (U.isEmpty(siteName)
			|| siteName.equals("localhost") || siteName.startsWith("localhost:")
			|| siteName.equals("127.0.0.1") || siteName.startsWith("127.0.0.1:")) {
			siteName = "Rapidoid";
		}

		screen.brand(U.or(brand, siteName));
		screen.title(U.or(title, siteName));

		screen.home(zone.entry("home").str().or("/"));

		screen.search(zone.entry("search").bool().or(false));
		screen.navbar(zone.entry("navbar").bool().or(brand != null));
		screen.fluid(zone.entry("fluid").bool().or(false));

		String cdn = zone.entry("cdn").or("auto");
		if (!"auto".equalsIgnoreCase(cdn)) {
			screen.cdn(Cls.bool(cdn));
		}

		if (zone.has("menu")) {
			screen.menu(zone.sub("menu").toMap());
		}
	}

	@Override
	public OutputStream out() {
		if (chunked == null) {
			synchronized (this) {
				if (chunked == null) {
					checkStreamingPreconditions();

					// the chunked response object, which buffers the response data
					chunked = new ChunkedResponse(this);

					// set the header early, so it can be overwritten by the app, if necessary
					header("Transfer-Encoding", "chunked");
				}
			}
		}

		return chunked;
	}

	private void checkStreamingPreconditions() {
		U.must(result() == null, "The response result has already been set, so cannot write the response through OutputStream, too!");
		U.must(body() == null, "The response body has already been set, so cannot write the response through OutputStream, too!");
		U.must(raw() == null, "The raw response has already been set, so cannot write the response through OutputStream, too!");
	}

	void startChunkedOutputStream() {
		req.doRendering(code(), null);
	}

	@Override
	public String toString() {
		return "RespImpl{" +
			(result != null ? "result=" + result : "") +
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

	byte[] renderToBytes() {
		if (mvc()) {
			byte[] bytes = ResponseRenderer.renderMvc(req, this);
			HttpUtils.postProcessResponse(this); // the response might have been changed, so post-process again
			return bytes;

		} else if (result() != null) {
			return serializeResponseContent();

		} else if (body() != null) {
			return Msc.toBytes(body());

		} else {
			throw U.rte("There's nothing to render!");
		}
	}

	private byte[] serializeResponseContent() {
		return HttpUtils.responseToBytes(req, result(), contentType(), Customization.of(req).jsonResponseRenderer());
	}

	@Override
	public Resp chunk(byte[] data) {
		OutputStream chnk = out();

		synchronized (chnk) {
			try {
				chnk.flush();
			} catch (IOException e) {
				throw U.rte(e);
			}

			chunk(data, 0, data.length);
		}

		return this;
	}

	public void chunk(final byte[] data, final int offset, final int length) {
		U.notNull(data, "data");

		resume(new AsyncLogic() {
			@Override
			public boolean resumeAsync() {
				Buf out = req.channel().output();

				out.append(Integer.toHexString(length));
				out.append("\r\n");
				out.append(data, offset, length);
				out.append("\r\n");

				req.channel().send();

				return false;
			}
		});
	}

	void terminatingChunk() {
		resume(new AsyncLogic() {
			@Override
			public boolean resumeAsync() {
				Buf out = req.channel().output();
				out.append("0\r\n\r\n");
				return true;
			}
		});
	}

	void finish() {
		if (chunked != null && !chunked.isClosed()) {
			IO.close(chunked, false);
		}
	}
}
