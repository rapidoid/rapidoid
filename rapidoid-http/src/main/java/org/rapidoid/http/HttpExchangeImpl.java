package org.rapidoid.http;

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
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.rapidoid.data.BinaryMultiData;
import org.rapidoid.data.Data;
import org.rapidoid.data.KeyValueRanges;
import org.rapidoid.data.MultiData;
import org.rapidoid.data.Range;
import org.rapidoid.data.Ranges;
import org.rapidoid.inject.IoC;
import org.rapidoid.net.impl.ConnState;
import org.rapidoid.net.impl.DefaultExchange;
import org.rapidoid.net.mime.MediaType;
import org.rapidoid.security.Secure;
import org.rapidoid.util.Cls;
import org.rapidoid.util.Constants;
import org.rapidoid.util.IUser;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;
import org.rapidoid.wrap.Bool;

public class HttpExchangeImpl extends DefaultExchange<HttpExchange, HttpExchangeBody> implements HttpExchange,
		HttpInterception, Constants {

	protected static final String SESSION_USER = "_user";

	private static final String SESSION_COOKIE = "JSESSIONID";

	public static final String SESSION_PAGE_STACK = "_page_stack_";

	private final static HttpParser PARSER = IoC.singleton(HttpParser.class);

	private static final byte[] HEADER_SEP = ": ".getBytes();

	private static final Pattern STATIC_RESOURCE_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\.\\-/]+$");

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

	private int bodyPos;

	private boolean writesBody;
	private boolean hasContentType;
	private int startingPos;
	private HttpResponses responses;
	private HttpSession session;
	private Router router;

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

	private int responseCode;
	private String redirectUrl;
	private String sessionId;
	private Throwable error;
	private boolean complete;

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
	public synchronized void reset() {
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

		sessionId = null;

		session = null;
		router = null;

		resetResponse();
	}

	private void resetResponse() {
		writesBody = false;
		bodyPos = -1;
		hasContentType = false;
		responses = null;
		responseCode = -1;
		redirectUrl = null;
		error = null;
		complete = false;
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
	public synchronized MultiData data_() {
		if (!parsedBody) {
			PARSER.parseBody(input(), headersKV, body, data, files, helper());
			parsedBody = true;
		}

		return _data;
	}

	@Override
	public synchronized BinaryMultiData files_() {
		if (!parsedBody) {
			PARSER.parseBody(input(), headersKV, body, data, files, helper());
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
	public synchronized HttpExchangeBody send() {
		conn.send();
		return this;
	}

	@Override
	public synchronized String toString() {
		return "HttpExchange [uri=" + uri() + ", verb=" + verb() + ", path=" + path() + ", subpath=" + subpath()
				+ ", query=" + query() + ", protocol=" + protocol() + ", body=" + body() + ", headers=" + headers()
				+ ", params=" + params() + ", cookies=" + cookies() + ", data=" + data() + ", files=" + files() + "]";
	}

	@Override
	public synchronized String verb() {
		return verb_().get();
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
	public String param(String name, String defaultValue) {
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
	public String header(String name, String defaultValue) {
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
	public synchronized Map<String, String> data() {
		return data_().get();
	}

	@Override
	public synchronized String data(String name) {
		return U.notNull(data_().get(name), "DATA[%s]", name);
	}

	@Override
	public String data(String name, String defaultValue) {
		return U.or(data_().get(name), defaultValue);
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
	public synchronized Data host_() {
		return headers_().get_("host");
	}

	@Override
	public synchronized String host() {
		return headers_().get("host");
	}

	public synchronized void setResponses(HttpResponses responses) {
		this.responses = responses;
	}

	public synchronized void setSession(HttpSession session) {
		this.session = session;
	}

	@Override
	public synchronized HttpExchange addHeader(byte[] name, byte[] value) {
		if (responseCode <= 0) {
			responseCode(200);
		}

		super.write(name);
		super.write(HEADER_SEP);
		super.write(value);
		super.write(CR_LF);

		return this;
	}

	private HttpExchangeHeaders responseCode(int responseCode) {
		if (this.responseCode > 0) {
			assert startingPos >= 0;
			output().deleteAfter(startingPos);
		}

		this.responseCode = responseCode;

		startingPos = output().size();
		output().append(getResp(responseCode).bytes());
		hasContentType = false;
		writesBody = false;
		bodyPos = -1;

		return this;
	}

	public synchronized void completeResponse() {

		// TODO find better solution
		if (complete) {
			// after async req is done, it might be called to complete again, so exit
			return;
		}

		U.must(responseCode >= 100);

		write(new byte[0]);

		U.must(bodyPos >= 0);

		long responseSize = output().size() - bodyPos;
		U.must(responseSize <= Integer.MAX_VALUE, "Response too big!");

		int pos = startingPos + getResp(responseCode).contentLengthPos + 10;
		output().putNumAsText(pos, responseSize, false);

		closeIf(!isKeepAlive.value);

		complete = true;
	}

	private HttpResponse getResp(int code) {
		HttpResponse resp = responses.get(code, isKeepAlive.value);
		assert resp != null;
		return resp;
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
	public synchronized HttpExchange setContentType(MediaType MediaType) {
		U.must(!hasContentType, "Content type was already set!");

		addHeader(HttpHeader.CONTENT_TYPE.getBytes(), MediaType.getBytes());

		// this must be at the end of this method, because state might get restarted
		hasContentType = true;

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
		if (!writesBody) {
			if (!hasContentType) {
				html();
			}
			writesBody = true;
			write(CR_LF);
			bodyPos = output().size();
		}
	}

	@Override
	public synchronized HttpExchangeBody write(String s) {
		ensureHeadersComplete();
		return super.write(s);
	}

	@Override
	public synchronized HttpExchangeBody writeln(String s) {
		ensureHeadersComplete();
		return super.writeln(s);
	}

	@Override
	public synchronized HttpExchangeBody write(byte[] bytes) {
		ensureHeadersComplete();
		return super.write(bytes);
	}

	@Override
	public synchronized HttpExchangeBody write(byte[] bytes, int offset, int length) {
		ensureHeadersComplete();
		return super.write(bytes, offset, length);
	}

	@Override
	public synchronized HttpExchangeBody write(ByteBuffer buf) {
		ensureHeadersComplete();
		return super.write(buf);
	}

	@Override
	public synchronized HttpExchangeBody write(File file) {
		if (!hasContentType()) {
			download(file.getName());
		}

		ensureHeadersComplete();
		return super.write(file);
	}

	@Override
	public synchronized HttpExchangeBody writeJSON(Object value) {
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
		return hasContentType;
	}

	@Override
	public synchronized HttpExchangeBody sendFile(File file) {
		U.must(file.exists());
		setContentType(MediaType.getByFileName(file.getAbsolutePath()));
		write(file);
		return this;
	}

	@Override
	public synchronized HttpExchangeBody redirect(String url) {
		responseCode(303);
		addHeader(HttpHeader.LOCATION, url);
		this.redirectUrl = url;
		ensureHeadersComplete();
		return this;
	}

	@Override
	public synchronized String redirectUrl() {
		return redirectUrl;
	}

	@Override
	public synchronized HttpExchangeHeaders response(int httpResponseCode) {
		return response(httpResponseCode, null, null);
	}

	@Override
	public synchronized HttpExchangeHeaders response(int httpResponseCode, String response) {
		return response(httpResponseCode, response, null);
	}

	@Override
	public synchronized HttpExchangeHeaders response(int httpResponseCode, String response, Throwable err) {

		responseCode(httpResponseCode);
		ensureHeadersComplete();

		if (U.production()) {
			if (response != null) {
				write(response);
			}
		} else {
			String title = U.or(response, "Error occured!");
			if (err != null) {
				if (devMode()) {
					HTMLSnippets.writeErrorPage(this, title, err);
				} else {
					HTMLSnippets.writeFullPage(this, title, "");
				}
			} else {
				HTMLSnippets.writeFullPage(this, title, "<h1>The requested page cannot be found!</h1>");
			}
		}

		return this;
	}

	@Override
	public synchronized HttpExchangeHeaders startResponse(int httpResponseCode) {
		return responseCode(httpResponseCode);
	}

	@Override
	public synchronized String constructUrl(String path) {
		return (U.hasOption("https") ? "https://" : "http://") + host() + path;
	}

	@Override
	public synchronized String sessionId() {
		if (sessionId == null) {
			sessionId = cookie(SESSION_COOKIE, null);

			if (sessionId != null && !session.exists(sessionId)) {
				sessionId = null;
			}

			if (sessionId == null) {
				sessionId = U.rndStr(50);
				setCookie(SESSION_COOKIE, sessionId, "path=/");
				session.openSession(sessionId);
			}
		}

		return sessionId;
	}

	@Override
	public synchronized Map<String, Object> session() {
		return session.getSession(sessionId());
	}

	@Override
	public synchronized HttpExchangeHeaders sessionSet(String name, Object value) {
		if (value != null) {
			session.setAttribute(sessionId(), name, value);
		} else {
			session.deleteAttribute(sessionId(), name);
		}

		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized <T> T session(String name, T defaultValue) {
		return U.or((T) session.getAttribute(sessionId(), name), defaultValue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized <T> T session(String name) {
		T value = (T) session.getAttribute(sessionId(), name);
		U.notNull(value, "session[" + name + "]");
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized <T> T sessionGetOrCreate(String name, Class<T> valueClass, Object... constructorArgs) {
		T value = (T) session.getAttribute(sessionId(), name);

		if (value == null) {
			value = U.newInstance(valueClass, constructorArgs);
			session.setAttribute(sessionId(), name, value);
		}

		return value;
	}

	@Override
	public synchronized HttpExchangeHeaders closeSession() {
		session.closeSession(sessionId());
		sessionId = null;
		return this;
	}

	@Override
	public synchronized boolean hasSession() {
		String sessId = cookie(SESSION_COOKIE, null);
		return sessId != null && session.exists(sessId);
	}

	@Override
	public synchronized HttpExchangeHeaders notFound() {
		return response(404, "Error: page not found!");
	}

	@Override
	public synchronized boolean isLoggedIn() {
		return hasSession() && session(SESSION_USER, null) != null;
	}

	@Override
	public synchronized IUser user() {
		U.must(isLoggedIn(), "Must be logged in!");

		return session(SESSION_USER);
	}

	@Override
	public synchronized <T> T user(Class<T> userClass) {
		IUser user = user();

		T user2 = U.newInstance(userClass);

		Cls.setPropValue(user2, "username", user.username());
		Cls.setPropValue(user2, "email", user.email());
		Cls.setPropValue(user2, "name", user.name());

		return user2;
	}

	@Override
	public synchronized boolean isAdmin() {
		if (!isLoggedIn()) {
			return false;
		}

		return Secure.isAdmin(username());
	}

	private String username() {
		return isLoggedIn() ? user().username() : null;
	}

	@Override
	public synchronized boolean isManager() {
		if (!isLoggedIn()) {
			return false;
		}

		return Secure.isManager(username());
	}

	@Override
	public synchronized boolean isModerator() {
		if (!isLoggedIn()) {
			return false;
		}

		return Secure.isModerator(username());
	}

	@Override
	public synchronized boolean hasRole(String role) {
		if (!isLoggedIn()) {
			return false;
		}

		return Secure.hasRole(username(), role);
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
	public synchronized byte[] sessionSerialize() {
		return UTILS.serialize(session);
	}

	@Override
	public synchronized void sessionDeserialize(byte[] bytes) {
		session = (HttpSession) UTILS.deserialize(bytes);
	}

	@Override
	public synchronized OutputStream outputStream() {
		return new HttpOutputStream(this);
	}

	@Override
	public synchronized boolean devMode() {
		if (U.dev()) {
			return true;
		}

		if (U.production()) {
			return false;
		}

		String host = host();
		return host == null || host.equals("localhost") || host.equals("127.0.0.1") || host.startsWith("localhost:")
				|| host.startsWith("127.0.0.1:");
	}

	@Override
	public int responseCode() {
		return this.responseCode;
	}

	@Override
	public void run() {
		router.dispatch(this);
	}

	public void setRouter(Router router) {
		this.router = router;
	}

	@Override
	public HttpExchange exchange() {
		return this;
	}

	@Override
	public boolean hasError() {
		return error != null;
	}

	@Override
	public Throwable error() {
		return error;
	}

	@Override
	public String pathSegment(int segmentIndex) {
		return path().substring(1).split("/")[segmentIndex];
	}

	@Override
	public HttpExchangeHeaders accessDeniedIf(boolean accessDeniedCondition) {
		if (accessDeniedCondition) {
			throw new SecurityException("Access denied!");
		}
		return this;
	}

	@Override
	public HttpExchangeHeaders errorResponse(Throwable err) {
		Throwable cause = U.rootCause(err);
		if (cause instanceof SecurityException) {
			return response(500, "Access Denied!", cause);
		} else {
			return response(500, "Internal server error!", cause);
		}
	}

	@Override
	public HttpExchangeHeaders authorize(Class<?> clazz) {
		return accessDeniedIf(!Secure.canAccessClass(username(), clazz));
	}

	@Override
	public boolean serveStatic() {
		if (isGetReq()) {
			String filename = path().substring(1);

			if (filename.isEmpty()) {
				filename = "index.html";
			}

			if (!filename.contains("..") && STATIC_RESOURCE_PATTERN.matcher(filename).matches()) {
				URL res = U.resource("public/" + filename);
				if (res != null) {
					startResponse(200);
					sendFile(new File(res.getFile()));
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public HttpExchangeBody goBack(int steps) {
		String dest = "/";
		List<String> stack = session(SESSION_PAGE_STACK, null);

		if (stack != null) {
			if (!stack.isEmpty()) {
				dest = stack.get(stack.size() - 1);
			}

			for (int i = 0; i < steps; i++) {
				if (!stack.isEmpty()) {
					stack.remove(stack.size() - 1);
					if (!stack.isEmpty()) {
						dest = stack.remove(stack.size() - 1);
					}
				}
			}
		}

		return redirect(dest);
	}

	@SuppressWarnings("unchecked")
	public HttpExchangeBody addToPageStack() {
		List<String> stack = sessionGetOrCreate(SESSION_PAGE_STACK, ArrayList.class);

		String last = !stack.isEmpty() ? stack.get(stack.size() - 1) : null;
		String current = uri();

		if (!U.eq(current, last)) {
			stack.add(current);
			if (stack.size() > 7) {
				stack.remove(0);
			}
		}

		return this;
	}

}
