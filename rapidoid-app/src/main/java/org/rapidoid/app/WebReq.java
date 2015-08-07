package org.rapidoid.app;

/*
 * #%L
 * rapidoid-rest
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

import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.dispatch.PojoRequest;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class WebReq implements PojoRequest {

	private final HttpExchange exchange;

	public WebReq(HttpExchange x) {
		this.exchange = x;
	}

	@Override
	public String command() {
		return exchange.verb();
	}

	@Override
	public String path() {
		return exchange.path();
	}

	@Override
	public Map<String, Object> params() {
		Map<String, Object> params = U.map();

		params.putAll(exchange.data());
		params.putAll(exchange.files());

		return params;
	}

	public HttpExchange getExchange() {
		return exchange;
	}

	@Override
	public Object param(String name) {
		Object value = exchange.param(name);

		if (value == null) {
			value = exchange.posted(name);

			if (value == null) {
				value = exchange.file(name);
			}
		}

		return value;
	}

}
