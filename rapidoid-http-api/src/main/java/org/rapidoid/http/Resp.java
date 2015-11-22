package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http-api
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
import java.nio.ByteBuffer;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.P;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.MediaType;
import org.rapidoid.io.Res;
import org.rapidoid.plugins.templates.ITemplate;

@Authors("Nikolche Mihajlovski")
@Since("4.2.0")
public interface Resp {

	/* RESPONSE: */

	HttpExchange result(@P("result") Object result);

	HttpExchange error(@P("err") Throwable err);

	HttpNotFoundException notFound();

	String constructUrl(@P("path") String path);

	HttpExchange startResponse(@P("httpResponseCode") int httpResponseCode);

	HttpExchange response(@P("httpResponseCode") int httpResponseCode);

	HttpExchange response(@P("httpResponseCode") int httpResponseCode, @P("response") String response);

	HttpExchange response(@P("httpResponseCode") int httpResponseCode, @P("response") String response,
			@P("err") Throwable err);

	HttpExchange plain();

	HttpExchange html();

	HttpExchange json();

	HttpExchange binary();

	HttpExchange download(@P("filename") String filename);

	HttpExchange addHeader(@P("name") byte[] name, @P("value") byte[] value);

	HttpExchange addHeader(@P("name") HttpHeader name, @P("value") String value);

	HttpExchange setCookie(@P("name") String name, @P("value") String value, @P("extras") String... extras);

	HttpExchange setContentType(@P("contentType") MediaType contentType);

	HttpExchange accessDeniedIf(@P("accessDeniedCondition") boolean accessDeniedCondition);

	int responseCode();

	String redirectUrl();

	boolean serveStaticFile();

	boolean serveStaticFile(@P("filename") String filename);

	HttpExchange sendFile(@P("file") File file);

	HttpExchange sendFile(@P("resource") Res resource);

	HttpExchange sendFile(@P("mediaType") MediaType mediaType, @P("bytes") byte[] bytes);

	HttpSuccessException redirect(@P("url") String url);

	OutputStream outputStream();

	HttpExchange write(String s);

	HttpExchange writeln(String s);

	HttpExchange write(@P("bytes") byte[] bytes);

	HttpExchange write(@P("bytes") byte[] bytes, @P("offset") int offset, @P("length") int length);

	HttpExchange write(@P("buf") ByteBuffer buf);

	HttpExchange write(@P("file") File file);

	HttpExchange writeJSON(@P("value") Object value);

	// due to async() web handling option, it ain't over till the fat lady sings "done"
	HttpExchange done();

	HttpExchange render(@P("template") ITemplate template, @P("model") Object model);

	HttpExchange renderPage(@P("model") Object model);

	String renderPageToHTML(@P("model") Object model);

}
