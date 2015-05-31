package org.rapidoid.dispatch.impl;

/*
 * #%L
 * rapidoid-dispatch
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class PojoRequestImpl implements PojoRequest {

	private final String command;
	private final String path;
	private final Map<String, String> params;

	public PojoRequestImpl(String command, String path, Map<String, String> params) {
		this.command = command;
		this.path = path;
		this.params = params;
	}

	@Override
	public String command() {
		return command;
	}

	@Override
	public String path() {
		return path;
	}

	@Override
	public Map<String, String> params() {
		return params;
	}

	@Override
	public String toString() {
		return "PojoRequestImpl [command=" + command + ", path=" + path + ", params=" + params + "]";
	}

}
