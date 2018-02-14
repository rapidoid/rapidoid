/*-
 * #%L
 * rapidoid-http-fast
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
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

package org.rapidoid.http.handler;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.FastHttp;
import org.rapidoid.http.HttpStatus;
import org.rapidoid.http.Req;
import org.rapidoid.net.abstracts.Channel;

@Authors("Nikolche Mihajlovski")
@Since("5.5.1")
public abstract class AbstractHttpHandlerDecorator extends RapidoidThing {

	protected final AbstractDecoratingHttpHandler handler;

	protected final FastHttp http;

	AbstractHttpHandlerDecorator(AbstractDecoratingHttpHandler handler, FastHttp http) {
		this.handler = handler;
		this.http = http;
	}

	Object handleReqAndPostProcess(Channel ctx, boolean isKeepAlive, Req req) {
		Object result;

		try {
			result = handler.handleReq(ctx, isKeepAlive, req);

		} catch (Throwable e) {
			result = e;
		}

		return HandlerResultProcessor.INSTANCE.postProcessResult(req, result);
	}

	abstract HttpStatus handle(Channel ctx, boolean isKeepAlive, Req req);

}
