package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http
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

import java.io.File;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.ctx.UserInfo;
import org.rapidoid.data.BinaryMultiData;
import org.rapidoid.data.Data;
import org.rapidoid.data.KeyValueRanges;
import org.rapidoid.data.MultiData;
import org.rapidoid.data.Range;
import org.rapidoid.data.Ranges;
import org.rapidoid.http.session.SessionStore;
import org.rapidoid.io.Res;
import org.rapidoid.jackson.JSON;
import org.rapidoid.log.Log;
import org.rapidoid.mime.MediaType;
import org.rapidoid.net.impl.ConnState;
import org.rapidoid.net.impl.DefaultExchange;
import org.rapidoid.plugins.templates.ITemplate;
import org.rapidoid.plugins.templates.Templates;
import org.rapidoid.security.Secure;
import org.rapidoid.util.Constants;
import org.rapidoid.util.RapidoidConf;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;
import org.rapidoid.webapp.AppCtx;
import org.rapidoid.wire.Wire;
import org.rapidoid.wrap.BoolWrap;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class HttpExchangeImpl extends DefaultExchange<HttpExchangeImpl> implements LowLevelHttpExchange,
		HttpExchangeInternals, HttpInterception, Constants {

	public static final String SESSION_COOKIE = "JSESSIONID";
	public static final String COOKIEPACK_COOKIE = "COOKIEPACK";
	private static final String COOKIPACK_SESSION = "_session_";

	private final static HttpParser PARSER = Wire.singleton(HttpParser.class);

	private static volatile ITemplate PAGE_TEMPLATE;

	private static final byte[] HEADER_SEP = ": ".getBytes();

	final Range rUri = new Range();
	final Range rVerb = new Range();
	final Range rPath = new Range();
	final Range rQuery = new Range();
	final Range rProtocol = new Range();

	final Ranges headers = new Ranges(50);

	private final KeyValueRanges params = new KeyValueRanges(50);
	private final KeyValueRanges headersKV = new KeyValueRanges(50);
	private final KeyValueRanges cookies = new KeyValueRanges(50);
	private final KeyValueRanges posted = new KeyValueRanges(50);
	private final KeyValueRanges files = new KeyValueRanges(50);

	final Range rBody = new Range();
	final Range multipartBoundary = new Range();
	private final Range subpathRange = new Range();

	final BoolWrap isGet = new BoolWrap();
	final BoolWrap isKeepAlive = new BoolWrap();

	private boolean parsedParams;
	private boolean parsedHeaders;
	private boolean parsedBody;

	private int responseBodyPos;
	private int responseContentLengthPos;
	private boolean writesResponseBody;
	private MediaType responseContentType;
	private int responseStartingPos;

	private String path = null;
	private String home = "/";
	private String[] pathSegments;

	private HttpResponses responses;

	private Map<String, Object> data;
	private Map<String, String> errors;

	private Map<String, Object> model;

	private String resourceName;
	private boolean resourceNameHasExtension;

	/* STATE */

	private Map<String, Serializable> session;
	private Map<String, Serializable> cookiepack;
	private Map<String, Serializable> locals;
	private Map<String, Object> tmps;

	/**********/

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
	private final MultiData _posted;
	private final BinaryMultiData _files;

	private int responseCode;
	private String redirectUrl;
	private String sessionId;
	private Throwable error;
	private boolean complete;
	private boolean lowLevelProcessing;

	private ClassLoader classLoader;
	private SessionStore sessionStore;
	private Handler handler;

	private final Callable<Map<String, Object>> lazyData = new Callable<Map<String, Object>>() {
		@Override
		public Map<String, Object> call() throws Exception {
			return data();
		}
	};

	private final Callable<Map<String, byte[]>> lazyFiles = new Callable<Map<String, byte[]>>() {
		@Override
		public Map<String, byte[]> call() throws Exception {
			return files();
		}
	};

	private final Callable<Map<String, String>> lazyCookies = new Callable<Map<String, String>>() {
		@Override
		public Map<String, String> call() throws Exception {
			return cookies();
		}
	};

	private final Callable<Map<String, String>> lazyHeaders = new Callable<Map<String, String>>() {
		@Override
		public Map<String, String> call() throws Exception {
			return headers();
		}
	};

	public HttpExchangeImpl() {
		this._body = data(rBody);
		this._uri = data(rUri);
		this._verb = data(rVerb);
		this._path = decodedData(rPath);
		this._subpath = decodedData(subpathRange);
		this._query = decodedData(rQuery);
		this._protocol = data(rProtocol);
		this._params = multiData(params);
		this._headers = multiData(headersKV);
		this._cookies = multiData(cookies);
		this._posted = multiData(posted);
		this._files = binaryMultiData(files);

		reset();
	}

	@Override
	public synchronized void reset() {
		super.reset();

		isGet.value = false;
		isKeepAlive.value = false;

		tmps = null;

		rVerb.reset();
		rUri.reset();
		rPath.reset();
		rQuery.reset();
		rProtocol.reset();
		rBody.reset();
		multipartBoundary.reset();

		params.reset();
		headersKV.reset();
		headers.reset();
		cookies.reset();
		posted.reset();
		files.reset();
		data = null;
		model = null;

		path = null;
		home = "/";

		parsedParams = false;
		parsedHeaders = false;
		parsedBody = false;

		sessionId = null;

		classLoader = null;
		sessionStore = null;
		handler = null;

		session = null;
		cookiepack = null;
		locals = null;
		tmps = null;
		errors = null;

		_body.reset();
		_uri.reset();
		_verb.reset();
		_path.reset();
		_subpath.reset();
		_query.reset();
		_protocol.reset();
		_params.reset();
		_headers.reset();
		_cookies.reset();
		_posted.reset();
		_files.reset();

		resetResponse();
	}

	private void resetResponse() {
		writesResponseBody = false;
		responseBodyPos = -1;
		responseContentLengthPos = -1;
		responseContentType = null;
		responses = null;
		responseCode = -1;
		redirectUrl = null;
		error = null;
		complete = false;
		lowLevelProcessing = false;
	}

	@Override
	public void log(String msg) {
		conn.log(msg);
	}

	@Override
	public synchronized MultiData params_() {
		if (!parsedParams) {
			if (!rQuery.isEmpty()) {
				PARSER.parseParams(input(), params, query_().range());
			}

			parsedParams = true;
		}

		return _params;
	}

	@Override
	public synchronized MultiData headers_() {
		if (!parsedHeaders) {
			if (!headers.isEmpty()) {
				PARSER.parseHeadersIntoKV(input(), headers, headersKV, cookies, helper());
			}

			parsedHeaders = true;
		}

		return _headers;
	}

	@Override
	public synchronized MultiData cookies_() {
		if (!parsedHeaders) {
			if (!headers.isEmpty()) {
				PARSER.parseHeadersIntoKV(input(), headers, headersKV, cookies, helper());
			}

			parsedHeaders = true;
		}

		return _cookies;
	}

	@Override
	@SuppressWarnings("unchecked")
	public synchronized MultiData posted_() {
		if (!parsedBody) {
			boolean completed = PARSER.parseBody(input(), headersKV, rBody, posted, files, helper());

			if (!completed) {
				Map<String, String> map = JSON.parse(body(), Map.class);
				_posted.putExtras(map);
			}

			parsedBody = true;
		}

		return _posted;
	}

	@Override
	public synchronized BinaryMultiData files_() {
		if (!parsedBody) {
			PARSER.parseBody(input(), headersKV, rBody, posted, files, helper());
			parsedBody = true;
		}

		return _files;
	}

	public synchronized Data subpath_() {
		return _subpath;
	}

	@Override
	public synchronized Data body_() {
		return _body;
	}

	@Override
	public synchronized Data uri_() {
		return _uri;
	}

	@Override
	public synchronized Data verb_() {
		return _verb;
	}

	@Override
	public synchronized Data path_() {
		return _path;
	}

	@Override
	public synchronized Data protocol_() {
		return _protocol;
	}

	@Override
	public synchronized Data query_() {
		return _query;
	}

	public synchronized void setSubpath(int start, int end) {
		subpathRange.setInterval(start, end);
	}

	@Override
	public synchronized HttpExchangeImpl done() {
		if (isAsync()) {
			completeResponse();
			conn.done();
		}
		return this;
	}

	@Override
	public synchronized HttpExchangeImpl send() {
		conn.send();
		return this;
	}

	@Override
	public synchronized String toString() {
		return "HttpExchange [uri=" + uri() + "]";
	}

	@Override
	public synchronized String verb() {
		return isGet.value ? "GET" : verb_().get();
	}

	@Override
	public synchronized String uri() {
		return uri_().get();
	}

	@Override
	public synchronized String path() {
		if (path == null) {
			path = path_().get();
		}

		return path;
	}

	@Override
	public synchronized String subpath() {
		String subp = subpath_().get();
		return !U.isEmpty(subp) ? subp : "/";
	}

	@Override
	public synchronized String query() {
		return query_().get();
	}

	@Override
	public synchronized String protocol() {
		return protocol_().get();
	}

	@Override
	public synchronized String body() {
		return body_().get();
	}

	@Override
	public synchronized Map<String, String> params() {
		Map<String, String> reqParams = params_().get();
		reqParams.remove("_embedded");
		return reqParams;
	}

	@Override
	public synchronized String param(String name) {
		return U.notNull(params_().get(name), "PARAM[%s]", name);
	}

	@Override
	public synchronized String param(String name, String defaultValue) {
		return U.or(params_().get(name), defaultValue);
	}

	@Override
	public synchronized Map<String, String> headers() {
		return headers_().get();
	}

	@Override
	public synchronized String header(String name) {
		return U.notNull(headers_().get(name), "HEADERS[%s]", name);
	}

	@Override
	public synchronized String header(String name, String defaultValue) {
		return U.or(headers_().get(name), defaultValue);
	}

	@Override
	public synchronized Map<String, String> cookies() {
		return cookies_().get();
	}

	@Override
	public synchronized String cookie(String name) {
		return U.notNull(cookies_().get(name), "COOKIES[%s]", name);
	}

	@Override
	public synchronized String cookie(String name, String defaultValue) {
		return U.or(cookies_().get(name), defaultValue);
	}

	@Override
	public synchronized Map<String, String> posted() {
		return posted_().get();
	}

	@Override
	public synchronized String posted(String name) {
		return U.notNull(posted_().get(name), "POSTED[%s]", name);
	}

	@Override
	public synchronized String posted(String name, String defaultValue) {
		return U.or(posted_().get(name), defaultValue);
	}

	@Override
	public synchronized Map<String, byte[]> files() {
		return files_().get();
	}

	@Override
	public synchronized byte[] file(String name) {
		return U.notNull(files_().get(name), "FILE[%s]", name);
	}

	@Override
	public synchronized byte[] file(String name, byte[] defaultValue) {
		return U.or(files_().get(name), defaultValue);
	}

	@Override
	public synchronized Map<String, Object> data() {
		if (data == null) {
			data = U.synchronizedMap();
			data.putAll(params());
			data.putAll(posted());
		}

		return data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized <T> T data(String name) {
		return (T) U.notNull(data().get(name), "DATA[%s]", name);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized <T> T data(String name, T defaultValue) {
		return (T) U.or(data().get(name), defaultValue);
	}

	@Override
	public synchronized Data host_() {
		return headers_().get_("host");
	}

	@Override
	public synchronized String host() {
		return headers_().get("host");
	}

	@Override
	public synchronized HttpExchange addHeader(byte[] name, byte[] value) {
		U.must(!writesResponseBody, "Cannot add header because the body is being rendered already!");

		if (responseCode <= 0) {
			responseCode(200);
		}

		super.write(name);
		super.write(HEADER_SEP);
		super.write(value);
		super.write(CR_LF);

		return this;
	}

	private HttpExchange responseCode(int responseCode) {
		if (this.responseCode > 0) {
			assert responseStartingPos >= 0;
			output().deleteAfter(responseStartingPos);
		}

		this.responseCode = responseCode;

		responseStartingPos = output().size();

		HttpResponse resp = responses.get(responseCode, isKeepAlive.value);
		assert resp != null;

		output().append(resp.bytes());
		responseContentLengthPos = responseStartingPos + resp.contentLengthPos + 10;

		responseContentType = null;
		writesResponseBody = false;
		responseBodyPos = -1;

		return this;
	}

	public synchronized void completeResponse() {

		// TODO find better solution
		if (complete) {
			// after async req is done, it might be called to complete again, so exit
			return;
		}

		finish();

		if (!lowLevelProcessing) {
			U.must(responseCode >= 100, "Invalid response code: %s, URI=%s", responseCode, uri());

			write(new byte[0]);

			U.must(responseBodyPos >= 0);

			long responseSize = output().size() - responseBodyPos;
			U.must(responseSize <= Integer.MAX_VALUE, "Response too big!");

			output().putNumAsText(responseContentLengthPos, responseSize, false);

			closeIf(!isKeepAlive.value);
		}

		complete = true;
	}

	@Override
	public synchronized HttpExchange addHeader(HttpHeader name, String value) {
		addHeader(name.getBytes(), value.getBytes());
		return this;
	}

	@Override
	public synchronized HttpExchange setCookie(String name, String value, String... extras) {
		String cookie = name + "=" + value;

		if (extras.length > 0) {
			cookie += "; " + U.join("; ", extras);
		}

		addHeader(HttpHeader.SET_COOKIE, cookie);
		return this;
	}

	@Override
	public synchronized HttpExchange setContentType(MediaType mediaType) {
		U.must(responseContentType == null, "Content type was already set!");

		if (mediaType == null) {
			mediaType = MediaType.BINARY;
		}

		addHeader(HttpHeader.CONTENT_TYPE.getBytes(), mediaType.getBytes());

		// this must be at the end of this method, because state might get restarted
		responseContentType = mediaType;

		return this;
	}

	@Override
	public synchronized HttpExchange plain() {
		return setContentType(MediaType.PLAIN_TEXT_UTF_8);
	}

	@Override
	public synchronized HttpExchange html() {
		return setContentType(MediaType.HTML_UTF_8);
	}

	@Override
	public synchronized HttpExchange json() {
		return setContentType(MediaType.JSON_UTF_8);
	}

	@Override
	public synchronized HttpExchange binary() {
		return setContentType(MediaType.BINARY);
	}

	@Override
	public synchronized HttpExchange download(String filename) {
		addHeader(HttpHeader.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
		addHeader(HttpHeader.CACHE_CONTROL, "private");
		return binary();
	}

	public synchronized void ensureHeadersComplete() {
		if (!writesResponseBody) {
			beforeClosingHeaders();
			if (responseContentType == null) {
				html();
			}
			writesResponseBody = true;
			write(CR_LF);
			responseBodyPos = output().size();
		}
	}

	@Override
	public synchronized HttpExchangeImpl write(String s) {
		ensureHeadersComplete();
		return super.write(s);
	}

	@Override
	public synchronized HttpExchangeImpl writeln(String s) {
		ensureHeadersComplete();
		return super.writeln(s);
	}

	@Override
	public synchronized HttpExchangeImpl write(byte[] bytes) {
		ensureHeadersComplete();
		return super.write(bytes);
	}

	@Override
	public synchronized HttpExchangeImpl write(byte[] bytes, int offset, int length) {
		ensureHeadersComplete();
		return super.write(bytes, offset, length);
	}

	@Override
	public synchronized HttpExchangeImpl write(ByteBuffer buf) {
		ensureHeadersComplete();
		return super.write(buf);
	}

	@Override
	public synchronized HttpExchangeImpl write(File file) {
		if (!hasContentType()) {
			download(file.getName());
		}

		ensureHeadersComplete();
		return super.write(file);
	}

	@Override
	public synchronized HttpExchangeImpl writeJSON(Object value) {
		if (!hasContentType()) {
			json();
		}

		ensureHeadersComplete();
		return super.writeJSON(value);
	}

	@Override
	public synchronized boolean isInitial() {
		return conn.isInitial();
	}

	@Override
	public synchronized ConnState state() {
		return conn.state();
	}

	public synchronized boolean hasContentType() {
		return responseContentType != null;
	}

	public MediaType getResponseContentType() {
		return responseContentType;
	}

	@Override
	public synchronized HttpExchange sendFile(File file) {
		U.must(file.exists());
		setContentType(MediaType.getByFileName(file.getAbsolutePath()));
		write(file);
		done();
		return this;
	}

	@Override
	public synchronized HttpExchange sendFile(Res resource) {
		U.must(resource.exists());
		setContentType(MediaType.getByFileName(resource.getShortName()));
		write(resource.getBytes());
		done();
		return this;
	}

	@Override
	public synchronized HttpExchange sendFile(MediaType mediaType, byte[] bytes) {
		setContentType(mediaType);
		write(bytes);
		done();
		return this;
	}

	@Override
	public synchronized HttpSuccessException redirect(String url) {
		responseCode(303);
		addHeader(HttpHeader.LOCATION, url);
		this.redirectUrl = url;
		ensureHeadersComplete();
		throw error();
	}

	@Override
	public synchronized String redirectUrl() {
		return redirectUrl;
	}

	@Override
	public synchronized HttpExchange response(int httpResponseCode) {
		return response(httpResponseCode, null, null);
	}

	@Override
	public synchronized HttpExchange response(int httpResponseCode, String response) {
		return response(httpResponseCode, response, null);
	}

	@Override
	public synchronized HttpExchange response(int httpResponseCode, String response, Throwable err) {

		responseCode(httpResponseCode);
		ensureHeadersComplete();

		if (Conf.production()) {
			if (response != null) {
				write(response);
			}
		} else {
			String title = U.or(response, "Internal server error!");

			if (err != null) {
				String details = err.getMessage();

				details = U.trimr(details, "(<Unknown Source>#1)");

				renderPage(U.map("title", title, "error", true, "code", httpResponseCode, "navbar", !U.isEmpty(title),
						"details", details));
			} else {
				renderPage(U.map("title", title, "code", httpResponseCode, "error", httpResponseCode >= 400));
			}
		}

		return this;
	}

	@Override
	public synchronized HttpExchange startResponse(int httpResponseCode) {
		return responseCode(httpResponseCode);
	}

	@Override
	public synchronized String constructUrl(String path) {
		return (Conf.is("https") ? "https://" : "http://") + host() + path;
	}

	@Override
	public synchronized HttpNotFoundException notFound() {
		response(404, "Page not found!");
		throw HttpNotFoundException.get();
	}

	public synchronized UserInfo user() {
		return cookiepack != null ? UserInfo.from(cookiepack()) : null;
	}

	@Override
	public synchronized boolean isGetReq() {
		return isGet.value;
	}

	@Override
	public synchronized boolean isPostReq() {
		return !isGet.value && verb().equals("POST");
	}

	@Override
	public synchronized byte[] serializeLocals() {
		return locals != null ? UTILS.serialize(locals) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized void deserializeLocals(byte[] bytes) {
		locals = (Map<String, Serializable>) UTILS.deserialize(bytes);
	}

	@Override
	public synchronized byte[] serializeCookiepack() {
		return cookiepack != null ? UTILS.serialize(cookiepack) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized void deserializeCookiepack(byte[] bytes) {
		cookiepack = (Map<String, Serializable>) UTILS.deserialize(bytes);
	}

	@Override
	public synchronized OutputStream outputStream() {
		return new HttpOutputStream(this);
	}

	@Override
	public synchronized boolean isDevMode() {
		return AppCtx.app().dev();
	}

	@Override
	public synchronized int responseCode() {
		return this.responseCode;
	}

	@Override
	public synchronized HttpExchange exchange() {
		return this;
	}

	@Override
	public synchronized boolean hasError() {
		return error != null;
	}

	@Override
	public synchronized Throwable getError() {
		return error;
	}

	@Override
	public synchronized String pathSegment(int segmentIndex) {
		return pathSegments()[segmentIndex];
	}

	@Override
	public synchronized String[] pathSegments() {
		if (pathSegments == null) {
			pathSegments = U.triml(path(), "/").split("/");
		}

		return pathSegments;
	}

	@Override
	public synchronized HttpExchange accessDeniedIf(boolean accessDeniedCondition) {
		if (accessDeniedCondition) {
			throw new SecurityException("Access denied!");
		}
		return this;
	}

	@Override
	public synchronized HttpExchange error(Throwable err) {
		Throwable cause = UTILS.rootCause(err);
		if (cause instanceof HttpSuccessException) {
			return this;
		} else if (cause instanceof HttpNotFoundException) {
			throw notFound();
		} else if (cause instanceof SecurityException) {
			return response(500, "Access Denied!", cause);
		} else {
			return response(500, "Internal server error!", cause);
		}
	}

	@Override
	public synchronized HttpExchange authorize(Class<?> clazz) {
		return accessDeniedIf(!Secure.canAccessClass(AppCtx.username(), clazz));
	}

	@Override
	public synchronized boolean serveStaticFile() {
		if (serveStaticFile(resourceName())) {
			return true;
		}

		return !resourceNameHasExtension() && serveStaticFile(resourceName() + ".html");
	}

	@Override
	public synchronized boolean serveStaticFile(String filename) {
		String firstFile = Conf.staticPath() + "/" + filename;
		String defaultFile = Conf.staticPathDefault() + "/" + filename;
		Res res = Res.from(filename, true, firstFile, defaultFile);

		if (res.exists()) {
			sendFile(res);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public synchronized HttpSuccessException goBack(int steps) {
		throw PageStack.goBack(this, steps);
	}

	public synchronized HttpExchange addToPageStack() {
		PageStack.addToPageStack(this);
		return this;
	}

	public synchronized void init(HttpResponses responses, SessionStore sessionStore) {
		this.responses = responses;
		this.sessionStore = sessionStore;

		String cookiepack = cookie(HttpExchangeImpl.COOKIEPACK_COOKIE, null);
		if (!U.isEmpty(cookiepack) && !cookiepack.equals("null")) {
			String cpackStr = '"' + cookiepack + '"';
			byte[] cpbytes = JSON.parseBytes(cpackStr);
			deserializeCookiepack(cpbytes);
		}
	}

	@Override
	public HttpSuccessException error() {
		return HttpSuccessException.get();
	}

	@Override
	public synchronized void lowLevelProcessing() {
		this.lowLevelProcessing = true;
	}

	public synchronized boolean isLowLevelProcessing() {
		return lowLevelProcessing;
	}

	@Override
	public boolean isClosing() {
		return conn.isClosing();
	}

	@Override
	public boolean isClosed() {
		return conn.isClosed();
	}

	@Override
	public void waitUntilClosing() {
		conn.waitUntilClosing();
	}

	@Override
	public String realIpAddress() {
		return header("X-Forwarded-For", address());
	}

	@Override
	public synchronized void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public synchronized ClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	public synchronized void setSessionStore(SessionStore sessionStore) {
		this.sessionStore = sessionStore;
	}

	@Override
	public synchronized SessionStore getSessionStore() {
		return sessionStore;
	}

	@Override
	public synchronized boolean hasErrors() {
		return !U.isEmpty(errors);
	}

	@Override
	public synchronized Map<String, String> errors() {
		if (errors == null) {
			errors = U.synchronizedMap();
		}
		return errors;
	}

	@Override
	public synchronized Map<String, Object> tmps() {
		if (tmps == null) {
			tmps = U.synchronizedMap();
		}
		return tmps;
	}

	@Override
	public synchronized Map<String, Serializable> cookiepack() {
		if (cookiepack == null) {
			cookiepack = U.synchronizedMap();
		}
		return cookiepack;
	}

	@Override
	public synchronized Map<String, Serializable> locals() {
		if (locals == null) {
			locals = U.synchronizedMap();
		}
		return locals;
	}

	@Override
	public synchronized Map<String, Serializable> session() {
		if (session == null) {
			if (RapidoidConf.stateless()) {
				session = U.synchronizedMap();
				cookiepack().put(COOKIPACK_SESSION, UTILS.serializable(session));
			} else {
				session = sessionStore.get(sessionId());
			}
		}
		return session;
	}

	/* SESSION SCOPE GETTERS */

	@Override
	public synchronized <T extends Serializable> T session(String name, T defaultValue) {
		return Scopes.get("session", session, name, defaultValue);
	}

	@Override
	public synchronized <T extends Serializable> T session(String name) {
		return Scopes.get("session", session, name);
	}

	@Override
	public synchronized <T extends Serializable> T sessionGetOrCreate(String name, Class<T> valueClass,
			Object... constructorArgs) {
		return Scopes.getOrCreate("session", session(), name, valueClass, constructorArgs);
	}

	/* COOKIEPACK SCOPE GETTERS */

	@Override
	public synchronized <T extends Serializable> T cookiepack(String name, T defaultValue) {
		return Scopes.get("cookiepack", cookiepack, name, defaultValue);
	}

	@Override
	public synchronized <T extends Serializable> T cookiepack(String name) {
		return Scopes.get("cookiepack", cookiepack, name);
	}

	@Override
	public synchronized <T extends Serializable> T cookiepackGetOrCreate(String name, Class<T> valueClass,
			Object... constructorArgs) {
		return Scopes.getOrCreate("cookiepack", cookiepack(), name, valueClass, constructorArgs);
	}

	/* LOCALS SCOPE GETTERS */

	@Override
	public synchronized <T extends Serializable> T local(String name, T defaultValue) {
		return Scopes.get("locals", locals, name, defaultValue);
	}

	@Override
	public synchronized <T extends Serializable> T local(String name) {
		return Scopes.get("locals", locals, name);
	}

	@Override
	public synchronized <T extends Serializable> T localGetOrCreate(String name, Class<T> valueClass,
			Object... constructorArgs) {
		return Scopes.getOrCreate("locals", locals(), name, valueClass, constructorArgs);
	}

	/* TMPS SCOPE GETTERS */

	@Override
	public synchronized <T> T tmp(String name, T defaultValue) {
		return Scopes.get("tmps", tmps, name, defaultValue);
	}

	@Override
	public synchronized <T> T tmp(String name) {
		return Scopes.get("tmps", tmps, name);
	}

	@Override
	public synchronized <T> T tmpGetOrCreate(String name, Class<T> valueClass, Object... constructorArgs) {
		return Scopes.getOrCreate("tmps", tmps(), name, valueClass, constructorArgs);
	}

	/* SESSION */

	public synchronized void finish() {
		storeSession();
	}

	@Override
	public synchronized void storeSession() {
		if (sessionId != null) {
			sessionStore.set(sessionId, session);
		}
	}

	public synchronized boolean hasSession() {
		if (sessionId == null) {
			sessionId = cookie(SESSION_COOKIE, null);
		}

		return sessionId != null;
	}

	@Override
	public synchronized String sessionId() {
		if (sessionId == null) {
			sessionId = cookie(SESSION_COOKIE, null);

			if (sessionId == null) {
				sessionId = UUID.randomUUID().toString();
				setCookie(SESSION_COOKIE, sessionId, "path=/");
			}
		}

		return sessionId;
	}

	private void beforeClosingHeaders() {
		byte[] cpack = serializeCookiepack();
		if (cpack != null) {
			String json = U.mid(JSON.stringify(cpack), 1, -1);
			setCookie(COOKIEPACK_COOKIE, json, "path=/");
		}
	}

	@Override
	public synchronized void preload() {
		uri();
		verb();
		path();
		query();
		protocol();
		headers();
		params();
		data();
		files();
	}

	@Override
	public void loadState() {
		if (isPostReq()) {
			String state = posted("__state", null);
			if (!U.isEmpty(state) && !state.equals("null")) {
				byte[] bytes = JSON.parseBytes('"' + state + '"');
				deserializeLocals(bytes);
			}
		}
	}

	@Override
	public HttpExchange result(Object res) {
		if (res instanceof byte[]) {
			if (!hasContentType()) {
				binary();
			}
			write((byte[]) res);

		} else if (res instanceof String) {
			if (!hasContentType()) {
				json();
			}

			if (U.eq(getResponseContentType(), MediaType.JSON_UTF_8)) {
				writeJSON((String) res);
			} else {
				write((String) res);
			}

		} else if (res instanceof ByteBuffer) {
			if (!hasContentType()) {
				binary();
			}
			write((ByteBuffer) res);

		} else if (res instanceof File) {
			File file = (File) res;
			sendFile(file);

		} else if (UTILS.isRapidoidType(res.getClass())) {
			html().write(res.toString());

		} else {
			if (!hasContentType()) {
				json();
			}
			if (U.eq(getResponseContentType(), MediaType.JSON_UTF_8)) {
				writeJSON(res);
			} else {
				write("" + res);
			}
		}

		done();
		return this;
	}

	@Override
	public HttpExchangeImpl async() {
		super.async();
		preload();
		return this;
	}

	@Override
	public synchronized String resourceName() {
		if (resourceName == null) {
			resourceName = path().substring(1);

			if (resourceName.isEmpty()) {
				resourceName = "index";
				resourceNameHasExtension = false;
			} else {
				if (resourceName.endsWith(".html")) {
					resourceName = U.mid(resourceName, 0, -5);
				}
				resourceNameHasExtension = resourceName.contains(".");
			}
		}

		return resourceName;
	}

	@Override
	public synchronized String verbAndResourceName() {
		return verb().toUpperCase() + "/" + resourceName();
	}

	public synchronized boolean resourceNameHasExtension() {
		resourceName(); // make sure it is calculated
		return resourceNameHasExtension;
	}

	@Override
	public HttpExchange render(ITemplate template, Object model) {
		template.render(this.outputStream(), model, model());
		return this;
	}

	@Override
	public HttpExchange renderPage(Object model) {
		if (!hasContentType()) {
			html();
		}

		pageTemplate().render(this.outputStream(), model, model());

		return done();
	}

	@Override
	public String renderPageToHTML(Object model) {
		return pageTemplate().render(model, model());
	}

	private static ITemplate pageTemplate() {
		if (PAGE_TEMPLATE == null) {
			PAGE_TEMPLATE = Templates.fromFile("page.html");
		}
		return PAGE_TEMPLATE;
	}

	@Override
	public synchronized Map<String, Object> model() {
		if (model == null) {
			model = U.map("req", this, "data", lazyData, "files", lazyFiles, "cookies", lazyCookies, "headers",
					lazyHeaders);
			model.put("verb", verb());
			model.put("uri", uri());
			model.put("path", path());
			model.put("home", home());
			model.put("dev", isDevMode());
			model.put("app", AppCtx.app());

			List<String> providers = U.list("google", "facebook", "linkedin", "github");
			Map<String, Object> oauth = U.map("popup", true, "providers", providers);
			model.put("oauth", oauth);

			boolean loggedIn = AppCtx.isLoggedIn();
			model.put("loggedIn", loggedIn);
			model.put("user", loggedIn ? AppCtx.user() : null);
		}

		return model;
	}

	@Override
	public <T> T persister() {
		return Ctxs.ctx().persister();
	}

	@Override
	public synchronized String home() {
		return home;
	}

	public synchronized HttpExchangeImpl setHome(String home) {
		String uriPath = path();
		U.must(uriPath.startsWith(home));

		this.path = uriPath.substring(home.length());
		if (U.isEmpty(this.path)) {
			this.path = "/";
			this.rPath.length = 1;
		} else {
			this.rPath.strip(home.length(), 0);
		}

		pathSegments = null; // re-calculate path segments
		pathSegments();

		this.home = home;

		return this;
	}

	public synchronized String renderState() {
		try {
			return JSON.stringify(serializeLocals());
		} catch (Exception e) {
			Log.error("Cannot render state tag!", e);
			return "{}";
		}
	}

	@Override
	public synchronized Runnable asAsyncJob(Handler handler) {
		this.handler = handler;
		return this.async();
	}

	@Override
	public synchronized void run() {
		runInAsyncContext();
	}

	private void runInAsyncContext() {
		Object result;

		try {
			U.notNull(handler, "async handler");
			result = handler.handle(this);
		} catch (Throwable e) {
			HttpProtocol.handleError(this, e);
			return;
		}

		if (result != null && !(result instanceof HttpExchange)) {
			HttpProtocol.processResponse(this, result);
		}
	}

}
