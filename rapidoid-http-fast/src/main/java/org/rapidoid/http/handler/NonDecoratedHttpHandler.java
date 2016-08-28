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
import org.rapidoid.http.HttpStatus;
import org.rapidoid.http.MediaType;
import org.rapidoid.http.Req;
import org.rapidoid.http.impl.HttpIO;
import org.rapidoid.http.impl.RouteOptions;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.util.Msc;

import java.util.concurrent.Callable;

@Authors("Nikolche Mihajlovski")
@Since("5.2.0")
public class NonDecoratedHttpHandler extends AbstractHttpHandler {

	private final Callable<?> logic;
	private final boolean returnsJson;

	public NonDecoratedHttpHandler(RouteOptions options, Callable<?> logic) {
		super(options);

		this.logic = logic;
		this.returnsJson = options.contentType() == MediaType.JSON;
	}

	@Override
	public HttpStatus handle(Channel ctx, boolean isKeepAlive, Req req, Object extra) {
		Object result;

		try {
			result = logic.call();

		} catch (Exception e) {
			HttpIO.write200(ctx, isKeepAlive, contentType, "Internal server error!".getBytes());
			return HttpStatus.ERROR;
		}

		if (returnsJson) {
			HttpIO.writeAsJson(ctx, 200, isKeepAlive, result);
		} else {
			HttpIO.write200(ctx, isKeepAlive, contentType, Msc.toBytes(result));
		}

		return HttpStatus.DONE;
	}

	@Override
	public String toString() {
		return contentTypeInfo("() -> (non-blocking logic)");
	}

}
