package org.rapidoid.http.handler;

/*
 * #%L
 * rapidoid-http-fast
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.MediaType;
import org.rapidoid.http.HttpWrapper;
import org.rapidoid.http.RouteOptions;

import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("4.3.0")
public abstract class AbstractFastHttpHandler implements FastHttpHandler {

	protected final RouteOptions options;

	protected final MediaType contentType;

	protected final HttpWrapper[] wrappers;

	public AbstractFastHttpHandler(RouteOptions options) {
		this.options = options;
		this.contentType = options.contentType;
		this.wrappers = options.wrappers.toArray(new HttpWrapper[options.wrappers.size()]);
	}

	@Override
	public boolean needsParams() {
		return false;
	}

	@Override
	public MediaType contentType() {
		return contentType;
	}

	@Override
	public Map<String, String> getParams() {
		return null;
	}

	@Override
	public FastHttpHandler getHandler() {
		return this;
	}

}
