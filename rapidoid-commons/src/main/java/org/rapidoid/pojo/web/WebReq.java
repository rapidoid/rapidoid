package org.rapidoid.pojo.web;

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

import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.Req;
import org.rapidoid.pojo.PojoRequest;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class WebReq implements PojoRequest {

	private final Req req;

	public WebReq(Req req) {
		this.req = req;
	}

	@Override
	public String command() {
		return req.verb();
	}

	@Override
	public String path() {
		return req.path();
	}

	@Override
	public Map<String, Object> params() {
		Map<String, Object> params = U.map();

		params.putAll(req.data());
		params.putAll(req.files());

		return params;
	}

	public Req getReq() {
		return req;
	}

	@Override
	public Object param(String name) {
		return req.data(name, null);
	}

	@Override
	public boolean isEvent() {
		return false;
	}

}
