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
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.ctx.UserInfo;
import org.rapidoid.data.BinaryMultiData;
import org.rapidoid.data.Data;
import org.rapidoid.data.KeyValueRanges;
import org.rapidoid.data.MultiData;
import org.rapidoid.data.Range;
import org.rapidoid.data.Ranges;
import org.rapidoid.http.session.SessionStore;
import org.rapidoid.io.CachedResource;
import org.rapidoid.json.JSON;
import org.rapidoid.log.Log;
import org.rapidoid.mime.MediaType;
import org.rapidoid.net.impl.ConnState;
import org.rapidoid.net.impl.DefaultExchange;
import org.rapidoid.security.Secure;
import org.rapidoid.util.Constants;
import org.rapidoid.util.RapidoidConf;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;
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
	private final KeyValueRanges posted = new KeyValueRanges(50);
	private final KeyValueRanges files = new KeyValueRanges(50);

	final Range body = new Range();
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
	private boolean responseHasContentType;
	private int responseStartingPos;

	private HttpResponses responses;
	private Router router;

	private Map<String, String> data;
	private Map<String, String> errors;

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

	public HttpExchangeImpl() {
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
		posted.reset();
		files.reset();
		data = null;

		parsedParams = false;
		parsedHeaders = false;
		parsedBody = false;

		sessionId = null;

		classLoader = null;
		sessionStore = null;
		router = null;

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
		responseHasContentType = false;
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
			if (!query.isEmpty()) {
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
	public synchronized MultiData posted_() {
		if (!parsedBody) {
			PARSER.parseBody(input(), headersKV, body, posted, files, helper());
			parsedBody = true;
		}

		return _posted;
	}

	@Override
	public synchronized BinaryMultiData files_() {
		if (!parsedBody) {
			PARSER.parseBody(input(), headersKV, body, posted, files, helper());
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
		return path_().get();
	}

	@Override
	public synchronized String subpath() {
		return subpath_().get();
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
		return params_().get();
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
	public synchronized Map<String, String> data() {
		if (data == null) {
			data = U.synchronizedMap();
			data.putAll(params());
			data.putAll(posted());
		}

		return data;
	}

	@Override
	public synchronized String data(String name) {
		return U.notNull(data().get(name), "DATA[%s]", name);
	}

	@Override
	public synchronized String data(String name, String defaultValue) {
		return U.or(data().get(name), defaultValue);
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

		responseHasContentType = false;
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
			U.must(responseCode >= 100);

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
		U.must(!responseHasContentType, "Content type was already set!");

		if (mediaType != null) {
			addHeader(HttpHeader.CONTENT_TYPE.getBytes(), mediaType.getBytes());
		}

		// this must be at the end of this method, because state might get restarted
		responseHasContentType = true;

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
			if (!responseHasContentType) {
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
		return responseHasContentType;
	}

	@Override
	public synchronized HttpExchange sendFile(File file) {
		U.must(file.exists());
		setContentType(MediaType.getByFileName(file.getAbsolutePath()));
		write(file);
		return this;
	}

	@Override
	public synchronized HttpExchange sendFile(CachedResource resource) {
		U.must(resource.exists());
		setContentType(MediaType.getByFileName(resource.getName()));
		write(resource.getBytes());
		return this;
	}

	@Override
	public synchronized HttpExchange sendFile(MediaType mediaType, byte[] bytes) {
		setContentType(mediaType);
		write(bytes);
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
				if (Conf.dev()) {
					HTMLSnippets.writeErrorPage(this, title, err);
				} else {
					HTMLSnippets.writeFullPage(this, title, "");
				}
			} else {
				HTMLSnippets.writeFullPage(this, title, "");
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
		if (Conf.production()) {
			return false;
		}

		String host = host();
		return host == null || host.equals("localhost") || host.equals("127.0.0.1") || host.startsWith("localhost:")
				|| host.startsWith("127.0.0.1:");
	}

	@Override
	public synchronized int responseCode() {
		return this.responseCode;
	}

	@Override
	public synchronized void run() {
		router.dispatch(this);
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
		return path().substring(1).split("/")[segmentIndex];
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
		return accessDeniedIf(!Secure.canAccessClass(Secure.username(), clazz));
	}

	@Override
	public synchronized boolean serveStaticFile() {
		if (serveStaticFile("public/" + resourceName())) {
			return true;
		}

		return !resourceNameHasExtension() && serveStaticFile("public/" + resourceName() + ".html");
	}

	@Override
	public synchronized boolean serveStaticFile(String filename) {
		CachedResource resource = CachedResource.from(filename);

		if (resource.exists()) {
			sendFile(resource);
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

	public synchronized void init(HttpResponses responses, SessionStore sessionStore, Router router) {
		this.responses = responses;
		this.sessionStore = sessionStore;
		this.router = router;

		synchronized (Conf.class) {
			if (Conf.option("mode", null) == null) {
				Conf.set("mode", isDevMode() ? "dev" : "production");
				Log.info("Auto-detected dev/production mode", "mode", Conf.option("mode"));
			}
		}

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
				sessionId = helper().randomSHA512();
				setCookie(SESSION_COOKIE, sessionId, "path=/");
			}
		}

		return sessionId;
	}

	private void beforeClosingHeaders() {
		byte[] cpack = serializeCookiepack();
		if (cpack != null) {
			String json = U.mid(JSON.jacksonStringify(cpack), 1, -1);
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
			write((String) res);

		} else if (res instanceof ByteBuffer) {
			if (!hasContentType()) {
				binary();
			}
			write((ByteBuffer) res);

		} else if (res instanceof File) {
			File file = (File) res;
			sendFile(file);

		} else if (res.getClass().getSimpleName().endsWith("Page")) {
			html().write(res.toString());

		} else {
			if (!hasContentType()) {
				json();
			}
			writeJSON(res);
		}

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
				resourceNameHasExtension = resourceName.contains(".");
			}
		}

		return resourceName;
	}

	public boolean resourceNameHasExtension() {
		resourceName(); // make sure it is calculated
		return resourceNameHasExtension;
	}

	@Override
	public HttpExchange render(CachedResource template, Object... namesAndValues) {
		String text = UTILS.fillIn(template.toString(), namesAndValues);
		return write(text.getBytes());
	}

}
