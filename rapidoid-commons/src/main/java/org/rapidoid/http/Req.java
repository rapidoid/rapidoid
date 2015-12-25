package org.rapidoid.http;

/*
 * #%L
 * rapidoid-commons
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

import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

/**
 * HTTP Request API.
 */
@Authors("Nikolche Mihajlovski")
@Since("5.0.2")
public interface Req {

	/* HTTP REQUEST DATA */

	/** Gets the <b>verb</b> of the HTTP request. */
	String verb();

	/** Sets the <b>verb</b> of the HTTP request. */
	Req verb(String verb);

	/** Gets the <b>uri</b> of the HTTP request. */
	String uri();

	/** Sets the <b>uri</b> of the HTTP request. */
	Req uri(String uri);

	/** Gets the <b>path</b> of the HTTP request. */
	String path();

	/** Sets the <b>path</b> of the HTTP request. */
	Req path(String path);

	/** Gets the <b>query</b> of the HTTP request. */
	String query();

	/** Sets the <b>query</b> of the HTTP request. */
	Req query(String query);

	/** Gets the <b>raw body data</b> of the HTTP request. */
	byte[] body();

	/** Sets the <b>raw body data</b> of the HTTP request. */
	Req body(byte[] body);

	/** Gets the value of the <b>Host header</b> of the HTTP request. */
	String host();

	/** Sets the value of the <b>Host header</b> of the HTTP request. */
	Req host(String host);

	/** Gets the <b>IP address</b> of the HTTP client sending the request. */
	String clientIpAddress();

	/** Gets the <b>HTTP connection ID</b>, which is unique per HTTP server instance. */
	long connectionId();

	/** Gets the <b>HTTP request ID</b>, which is unique per HTTP server instance. */
	long requestId();

	/* URL PARAMETERS: */

	/** Gets the <b>URL parameters</b> of the HTTP request. */
	Map<String, String> params();

	/**
	 * Returns the value of the specified <b>mandatory URL parameter</b> from the HTTP request, or throws a runtime
	 * exception if it is not found.
	 */
	String param(String name);

	/**
	 * Returns the value of the specified <b>optional URL parameter</b> from the HTTP request, or the specified default
	 * value, if not found.
	 */
	String param(String name, String defaultValue);

	/* POSTED PARAMETERS IN THE REQUEST BODY: */

	/** Gets the <b>posted parameters</b> of the HTTP request body. */
	Map<String, Object> posted();

	/**
	 * Returns the value of the specified <b>posted parameter</b> from the HTTP request body, or throws a runtime
	 * exception if it is not found.
	 */
	<T extends Serializable> T posted(String name);

	/**
	 * Returns the value of the specified <b>posted parameter</b> from the HTTP request body, or the specified default
	 * value, if it is not found.
	 */
	<T extends Serializable> T posted(String name, T defaultValue);

	/* POSTED FILES IN THE REQUEST BODY: */

	/** Gets the <b>posted files</b> from the HTTP request body. */
	Map<String, byte[]> files();

	/**
	 * Returns the content of the <b>posted file</b> from the HTTP request body, or throws a runtime exception if it is
	 * not found.
	 */
	byte[] file(String name);

	/**
	 * Returns the content of the <b>posted file</b> from the HTTP request body, or the specified default value, if it
	 * is not found.
	 */
	byte[] file(String name, byte[] defaultValue);

	/* REQUEST DATA PARAMETERS (URL PARAMETERS + POSTED PARAMETERS + POSTED FILES): */

	/** Gets the <b>data parameters (URL parameters + posted parameters + posted files)</b> of the HTTP request. */
	Map<String, Object> data();

	/**
	 * Returns the value of the specified <b>data parameter</b> from the HTTP request, or throws a runtime exception if
	 * it is not found.
	 */
	<T> T data(String name);

	/**
	 * Returns the value of the specified <b>data parameter</b> from the HTTP request, or the specified default value,
	 * if it is not found.
	 */
	<T> T data(String name, T defaultValue);

	/* EXTRA ATTRIBUTES ATTACHED TO THE REQUEST: */

	/** Gets the <b>extra attributes</b> of the HTTP request. */
	Map<String, Object> attrs();

	/**
	 * Returns the value of an <b>extra attribute</b> from the HTTP request, or throws a runtime exception if it is not
	 * found.
	 */
	<T> T attr(String name);

	/**
	 * Returns the value of the specified <b>extra attribute</b> from the HTTP request, or the specified default value,
	 * if it is not found.
	 */
	<T> T attr(String name, T defaultValue);

	/* SERVER-SIDE SESSION: */

	/**
	 * Returns the ID of the session (the value of the <b>"JSESSIONID" cookie</b>). If a session doesn't exist, a new
	 * session is created.
	 */
	String sessionId();

	/** Does the HTTP request have a server-side session attached? */
	boolean hasSession();

	/** Gets the <b>server-side session attributes</b> of the HTTP request. */
	Map<String, Serializable> session();

	/**
	 * Returns the value of the specified <b>server-side session attribute</b> from the HTTP request, or throws a
	 * runtime exception if it is not found.
	 */
	<T extends Serializable> T session(String name);

	/**
	 * Returns the value of the specified <b>server-side session attribute</b> from the HTTP request, or the specified
	 * default value, if it is not found.
	 */
	<T extends Serializable> T session(String name, T defaultValue);

	/* COOKIE-PERSISTED SESSION: */

	/** Does the HTTP request have a cookie-persisted session attached? */
	boolean hasCookiepack();

	/** Gets the <b>cookie-persisted session attributes</b> of the HTTP request. */
	Map<String, Serializable> cookiepack();

	/**
	 * Returns the value of the specified <b>cookie-persisted session attribute</b> from the HTTP request, or throws a
	 * runtime exception if it is not found.
	 */
	<T extends Serializable> T cookiepack(String name);

	/**
	 * Returns the value of the specified <b>cookie-persisted session attribute</b> from the HTTP request, or the
	 * specified default value, if it is not found.
	 */
	<T extends Serializable> T cookiepack(String name, T defaultValue);

	/* REQUEST HEADERS: */

	/** Gets the <b>headers</b> of the HTTP request. */
	Map<String, String> headers();

	/**
	 * Returns the value of the specified <b>header</b> from the HTTP request, or throws a runtime exception if it is
	 * not found.
	 */
	String header(String name);

	/**
	 * Returns the value of the specified <b>header</b> from the HTTP request, or the specified default value, if it is
	 * not found.
	 */
	String header(String name, String defaultValue);

	/* REQUEST COOKIES: */

	/** Gets the <b>cookies</b> of the HTTP request. */
	Map<String, String> cookies();

	/**
	 * Returns the value of the specified <b>cookie</b> from the HTTP request, or throws a runtime exception if it is
	 * not found.
	 */
	String cookie(String name);

	/**
	 * Returns the value of the specified <b>cookie</b> from the HTTP request, or the specified default value, if it is
	 * not found.
	 */
	String cookie(String name, String defaultValue);

	/* RESPONSE: */

	/** Gets the reference to the <b>response object</b>. */
	Resp response();

	/**
	 * Renders the response headers based on the response() object, and then returns an <i>OutputStream</i> representing
	 * the <b>response body</b>. The response body will be constructed by rendering to the <i>OutputStream</i>.
	 */
	OutputStream out();

	/* ASYNCHRONOUS REQUEST HANDLING: */

	/**
	 * Informs the HTTP server that the request will be handled asynchronously (typically on another thread). When the
	 * response is complete, <b>the <code>Req#done()</code> or <code>Resp#done()</code> method must be called</b>, to
	 * inform the server.
	 */
	Req async();

	/** Is/was the request being handled in asynchronous mode? */
	boolean isAsync();

	/**
	 * Informs the HTTP server that the asynchronous handling has finished and the response is complete.
	 */
	Req done();

	/** Has the request handling and response construction finished? */
	boolean isDone();

}
