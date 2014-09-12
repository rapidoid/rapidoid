package org.rapidoid.web;

/*
 * #%L
 * rapidoid-pojo
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

import java.util.Map;

import com.rapidoid.http.WebExchange;

public class WebReq implements PojowebRequest {

	private static final String[] EMPTY_PATH = {};

	private final WebExchange x;

	public WebReq(WebExchange x) {
		this.x = x;
	}

	@Override
	public String uri() {
		return x.path_().get();
	}

	@Override
	public String path() {
		return x.path_().get();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, Object> paramsMap() {
		return (Map) x.params_().get();
	}

	@Override
	public String[] pathParts() {
		String path = path();

		if (path.isEmpty() || path.equals("/")) {
			return EMPTY_PATH;
		}

		return path.replaceAll("^/", "").replaceAll("/$", "").split("/");
	}

}
