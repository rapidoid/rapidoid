package com.rapidoid.http;

import org.rapidoid.net.mime.MediaType;

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

public interface HttpExchangeHeaders extends HttpExchangeBody {

	HttpExchangeHeaders responseCode(int httpResponseCode);

	HttpExchangeHeaders response(int httpResponseCode);

	HttpExchangeHeaders response(int httpResponseCode, String response);

	HttpExchangeHeaders response(int httpResponseCode, String response, Throwable err);

	HttpExchangeHeaders plain();

	HttpExchangeHeaders html();

	HttpExchangeHeaders json();

	HttpExchangeHeaders binary();

	HttpExchangeHeaders download(String filename);

	HttpExchangeHeaders addHeader(byte[] name, byte[] value);

	HttpExchangeHeaders addHeader(HttpHeader name, String value);

	HttpExchangeHeaders setCookie(String name, String value);

	HttpExchangeHeaders setContentType(MediaType contentType);

	HttpExchangeHeaders setSession(String name, Object value);

	HttpExchangeHeaders closeSession();

}
