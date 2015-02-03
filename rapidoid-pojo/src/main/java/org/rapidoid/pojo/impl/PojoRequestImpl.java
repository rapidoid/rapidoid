package org.rapidoid.pojo.impl;

/*
 * #%L
 * rapidoid-pojo
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
import org.rapidoid.pojo.PojoRequest;

@Authors("Nikolche Mihajlovski")
public class PojoRequestImpl implements PojoRequest {

	private final String command;
	private final String uri;
	private final Map<String, String> extra;

	public PojoRequestImpl(String command, String uri, Map<String, String> extra) {
		this.command = command;
		this.uri = uri;
		this.extra = extra;
	}

	@Override
	public String command() {
		return command;
	}

	@Override
	public String path() {
		return uri;
	}

	@Override
	public Map<String, String> params() {
		return extra;
	}

	@Override
	public String toString() {
		return "PojoRequestImpl [command=" + command + ", uri=" + uri + ", extra=" + extra + "]";
	}

}
