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
import org.rapidoid.pojo.PojoRequest;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class WebEventReq implements PojoRequest {

	private final String path;

	private final String event;

	@SuppressWarnings("unused")
	private final Object[] args;

	private final Map<String, Object> state;

	public WebEventReq(String path, String event, Object[] args, Map<String, Object> state) {
		this.path = path;
		this.event = event;
		this.args = args;
		this.state = state;
	}

	@Override
	public String command() {
		return event;
	}

	@Override
	public String path() {
		return path;
	}

	@Override
	public Map<String, Object> params() {
		return state;
	}

	@Override
	public Object param(String name) {
		return state.get(name);
	}

	@Override
	public boolean isEvent() {
		return true;
	}

}
