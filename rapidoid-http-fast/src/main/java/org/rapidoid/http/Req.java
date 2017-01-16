package org.rapidoid.http;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.io.Upload;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

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

/**
 * HTTP Request API.
 */
@Authors("Nikolche Mihajlovski")
@Since("5.0.2")
public interface Req {

	/* HTTP REQUEST DATA */

	/**
	 * Gets the <b>verb</b> of the HTTP request.
	 */
	String verb();

	/**
	 * Gets the <b>uri</b> of the HTTP request.
	 */
	String uri();

	/**
	 * Gets the <b>path</b> of the HTTP request.
	 */
	String path();

	/**
	 * Gets the <b>query</b> of the HTTP request.
	 */
	String query();

	/**
	 * Gets the <b>raw body data</b> of the HTTP request.
	 */
	byte[] body();

	/**
	 * Gets the value of the <b>Host header</b> of the HTTP request.
	 */
	String host();

	/**
	 * Gets the name of the <b>application zone</b> handling the request. <br>
	 * The default zone name is <code>main</code> for the <code>On</code> API, and <code>admin</code> for the <code>Admin</code> API.
	 */
	String zone();

	/**
	 * Gets the <b>context path</b> of the application zone handling the request. <br>
	 * The default context path is <code>/</code> for the <code>On</code> API, and <code>/_</code> for the <code>Admin</code> API.
	 */
	String contextPath();

	/**
	 * Gets the <b>IP address</b> of the HTTP client directly sending the request.<br>
	 * This can be the address of a real user, or a HTTP proxy (if the user uses such), or a reverse proxy (if the application/server uses such).
	 */
	String clientIpAddress();

	/**
	 * A best-effort attempt to infer the real <b>IP address</b> of the end user/client/client proxy.<br>
	 * If a reverse proxy is detected with high confidence (or configured), its headers will be used to get the real IP address of the user.<br>
	 * Otherwise, the value of <code>Req#clientIpAddress()</code> is returned.
	 */
	String realIpAddress();

	/**
	 * Gets the <b>HTTP connection ID</b>, which is unique per HTTP server instance.
	 */
	long connectionId();

	/**
	 * Gets the <b>HTTP request ID</b>, which is unique per HTTP server instance.
	 */
	long requestId();

	/* URL PARAMETERS: */

	/**
	 * Gets the <b>URL parameters</b> of the HTTP request.
	 */
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

	/**
	 * Returns a new instance of the specified bean type, with properties initialized from the <b>URL parameters</b> of the HTTP request.
	 */
	<T> T param(Class<T> beanType);

	/* POSTED PARAMETERS IN THE REQUEST BODY: */

	/**
	 * Gets the <b>posted parameters</b> of the HTTP request body.
	 */
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

	/**
	 * Returns a new instance of the specified bean type, with properties initialized from the <b>posted parameters</b> of the HTTP request.
	 */
	<T> T posted(Class<T> beanType);

	/* UPLOADED FILES IN THE REQUEST BODY: */

	/**
	 * Gets the <b>uploaded files</b> from the HTTP request body.
	 */
	Map<String, List<Upload>> files();

	/**
	 * Returns the <b>uploaded files</b> with the specified form parameter name (not filename) from the HTTP request body,
	 * or throws a runtime exception if not found.
	 */
	List<Upload> files(String name);

	/**
	 * Returns exactly one <b>posted file</b> with the specified form parameter name (not filename) from the HTTP request body,
	 * or throws a runtime exception if not found.
	 */
	Upload file(String name);

	/* REQUEST DATA PARAMETERS (URL PARAMETERS + POSTED PARAMETERS + UPLOADED FILES): */

	/**
	 * Gets the <b>data parameters (URL parameters + posted parameters + uploaded files)</b> of the HTTP request.
	 */
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

	/**
	 * Returns a new instance of the specified bean type, with properties initialized from the <b>data parameters</b> of the HTTP request.
	 */
	<T> T data(Class<T> beanType);

	/* EXTRA ATTRIBUTES ATTACHED TO THE REQUEST: */

	/**
	 * Gets the <b>extra attributes</b> of the HTTP request.
	 */
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

	/**
	 * Does the HTTP request have a server-side session attached?
	 */
	boolean hasSession();

	/**
	 * Provides <b>read/write access</b> to the <b>server-side session attributes</b> of the HTTP request/response.
	 */
	Map<String, Serializable> session();

	/**
	 * Returns the value of the specified <b>server-side session attribute</b> from the HTTP request/response, or throws a
	 * runtime exception if it is not found.
	 */
	<T extends Serializable> T session(String name);

	/**
	 * Returns the value of the specified <b>server-side session attribute</b> from the HTTP request/response, or the specified
	 * default value, if it is not found.
	 */
	<T extends Serializable> T session(String name, T defaultValue);

	/* TOKEN DATA: */

	/**
	 * Does the HTTP request have a token attached?
	 */
	boolean hasToken();

	/**
	 * Provides <b>read/write access</b> to the <b>token attributes</b> of the HTTP request/response.
	 */
	Map<String, Serializable> token();

	/**
	 * Returns the value of the specified <b>token attribute</b> from the HTTP request/response, or throws a
	 * runtime exception if it is not found.
	 */
	<T extends Serializable> T token(String name);

	/**
	 * Returns the value of the specified <b>token attribute</b> from the HTTP request/response, or the
	 * specified default value, if it is not found.
	 */
	<T extends Serializable> T token(String name, T defaultValue);

	/* REQUEST HEADERS: */

	/**
	 * Gets the <b>headers</b> of the HTTP request.
	 */
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

	/**
	 * Gets the <b>cookies</b> of the HTTP request.
	 */
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

	/**
	 * Gets the reference to the <b>response object</b>.
	 */
	Resp response();

	/* ASYNCHRONOUS REQUEST HANDLING: */

	/**
	 * Informs the HTTP server that the request will be handled asynchronously (typically on another thread). When the
	 * response is complete, <b>the <code>Req#done()</code> or <code>Resp#done()</code> method must be called</b>, to
	 * inform the server.
	 */
	Req async();

	/**
	 * Is/was the request being handled in asynchronous mode?
	 */
	boolean isAsync();

	/**
	 * Informs the HTTP server that the asynchronous handling has finished and the response is complete.
	 */
	Req done();

	/**
	 * Has the request handling and response construction finished?
	 */
	boolean isDone();

	/* WEB APPLICATION SETUP: */

	/**
	 * Provides access to the <b>HTTP routes</b> of the web application setup.
	 */
	HttpRoutes routes();

	/**
	 * Provides access to the <b>matching HTTP route (if any)</b> of the web application setup.<br>
	 * In case a generic handler handles the request, or no matching route was found, <code>null</code> is returned.
	 */
	Route route();

	/**
	 * Provides access to the <b>customization</b> of the web application setup.
	 */
	Customization custom();

	/**
	 * Reverts the previous processing of the request, usually with intention to process the same request again.
	 */
	void revert();

	/**
	 * First renders the response headers, then returns an <i>OutputStream</i> representing
	 * the <b>response body</b>. The response body will be constructed by writing to the <i>OutputStream</i>.
	 */
	OutputStream out();

	/**
	 * Gets the <b><code>Content-Type</code> header</b> of the HTTP response if it has been assigned,
	 * or the default value as configured in the HTTP route.
	 */
	MediaType contentType();

	/**
	 * Returns the request handle, which is used when resuming the request handling in asynchronous way.<br>
	 * See <code>Resp#resume</code>.
	 */
	long handle();

}
