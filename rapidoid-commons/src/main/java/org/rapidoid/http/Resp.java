package org.rapidoid.http;

/*
 * #%L
 * rapidoid-commons
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
import java.util.Map;

import org.rapidoid.annotation.Since;
import org.rapidoid.commons.MediaType;

/**
 * HTTP Response API.
 */
@Since("5.0.x")
public interface Resp {

	/**
	 * Sets the <b>content</b> to be serialized into a body when the HTTP response is rendered.
	 */
	Resp content(Object content);

	/**
	 * Gets the <b>content</b> to be serialized into a body when the HTTP response is rendered.
	 */
	Object content();

	/**
	 * Sets the <b>raw body data as <code>byte[]</code></b> that is copied into the HTTP response body when it is
	 * rendered.
	 */
	Resp body(byte[] body);

	/**
	 * Sets the <b>raw body data as <code>ByteBuffer</code></b> that is copied into the HTTP response body when it is
	 * rendered.
	 */
	Resp body(ByteBuffer body);

	/** Gets the <b>raw body data</b> that is copied into the HTTP response body when it is rendered. */
	Object body();

	/** Sets the <b>status code</b> <i>(e.g. 200, 404, 500)</i> of the HTTP response. */
	Resp code(int code);

	/** Gets the <b>status code</b> <i>(e.g. 200, 404, 500)</i> of the HTTP response. */
	int code();

	/** Sets the <b><code>Content-Type</code> header</b> to be rendered in the HTTP response. */
	Resp contentType(MediaType contentType);

	/** Gets the <b><code>Content-Type</code> header</b> to be rendered in the HTTP response. */
	MediaType contentType();

	/**
	 * Sets the <b>redirect URI</b> of the HTTP response. <br>
	 * Setting this will cause a <b>HTTP 30x redirect</b> response.
	 */
	Resp redirect(String redirectURI);

	/** Gets the <b>redirect URI</b> of the HTTP response. */
	String redirect();

	/** Sets the <b>filename</b> when serving a file in the HTTP response. */
	Resp filename(String filename);

	/** Gets the <b>filename</b> when serving a file in the HTTP response. */
	String filename();

	/** Sets a custom <b>view name</b> of the HTTP response. */
	Resp view(String viewName);

	/** Gets a custom <b>view name</b> of the HTTP response. */
	String view();

	/** Sets the <b>file</b> to be served when the HTTP response is rendered. */
	Resp file(File file);

	/** Gets the <b>file</b> to be served when the HTTP response is rendered. */
	File file();

	/** Provides <b>read/write access</b> to the <b>headers</b> of the HTTP response. */
	Map<String, String> headers();

	/** Provides <b>read/write access</b> to the <b>cookies</b> of the HTTP response. */
	Map<String, String> cookies();

	/**
	 * Informs the HTTP server that the asynchronous handling has finished and the response is complete.<br>
	 * <i>Alias</i> to <code>request().done()</code>.
	 */
	Req done();

	/**
	 * Sets the <b><code>Content-Type: text/plain; charset=utf-8</code> header and the content</b> of the HTTP response. <br>
	 * <i>Alias</i> to <code>contentType(MediaType.PLAIN_TEXT_UTF_8).body(content)</code>.
	 */
	Resp plain(Object content);

	/**
	 * Sets the <b><code>Content-Type: text/html; charset=utf-8</code> header and the content</b> of the HTTP response. <br>
	 * <i>Alias</i> to <code>contentType(MediaType.HTML_UTF_8).body(content)</code>.
	 */
	Resp html(Object content);

	/**
	 * Sets the <b><code>Content-Type: application/json; charset=utf-8</code> header and the content</b> of the HTTP
	 * response. <br>
	 * <i>Alias</i> to <code>contentType(MediaType.JSON_UTF_8).body(content)</code>.
	 */
	Resp json(Object content);

	/**
	 * Sets the <b><code>Content-Type: application/octet-stream</code> header and the content</b> of the HTTP response. <br>
	 * <i>Alias</i> to <code>contentType(MediaType.BINARY).body(content)</code>.
	 */
	Resp binary(Object content);

	/**
	 * First renders the response headers, then returns an <i>OutputStream</i> representing
	 * the <b>response body</b>. The response body will be constructed by writing to the <i>OutputStream</i>.
	 */
	OutputStream out();

	/**
	 * Gets the reference to the <b>request object</b>.
	 */
	Req request();

}
