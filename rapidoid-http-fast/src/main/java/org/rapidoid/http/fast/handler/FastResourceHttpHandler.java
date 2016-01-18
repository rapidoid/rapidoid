package org.rapidoid.http.fast.handler;

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
import org.rapidoid.http.Req;
import org.rapidoid.http.fast.FastHttp;
import org.rapidoid.http.fast.HttpStatus;
import org.rapidoid.io.Res;
import org.rapidoid.net.abstracts.Channel;

@Authors("Nikolche Mihajlovski")
@Since("4.3.0")
public class FastResourceHttpHandler extends AbstractFastHttpHandler {

	private final Res resource;

	public FastResourceHttpHandler(FastHttp http, MediaType contentType, Res resource) {
		super(http, contentType);
		this.resource = resource;
	}

	@Override
	public HttpStatus handle(Channel ctx, boolean isKeepAlive, Req req, Object extra) {
		http.getListener().state(this, req);

		byte[] bytes = resource.getBytesOrNull();

		if (bytes != null) {
			http.getListener().result(this, contentType, bytes);
			http.write200(ctx, isKeepAlive, contentType, bytes);
			return HttpStatus.DONE;
		} else {
			http.getListener().resultNotFound(this);
			return HttpStatus.NOT_FOUND;
		}
	}

}
