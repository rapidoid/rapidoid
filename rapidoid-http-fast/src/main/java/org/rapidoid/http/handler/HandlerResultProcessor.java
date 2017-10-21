package org.rapidoid.http.handler;

/*-
 * #%L
 * rapidoid-http-fast
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.datamodel.Results;
import org.rapidoid.http.HttpStatus;
import org.rapidoid.http.NotFound;
import org.rapidoid.http.Req;
import org.rapidoid.http.Resp;
import org.rapidoid.u.U;

import java.util.concurrent.Future;

public class HandlerResultProcessor extends RapidoidThing {

	public static final HandlerResultProcessor INSTANCE = new HandlerResultProcessor();

	@SuppressWarnings("unchecked")
	public Object postProcessResult(Req req, Object result) {

		if (result instanceof HttpStatus) {
			return result;

		} else if (result instanceof Req) {

			U.must(req == result, "Unknown request instance was received as result!");

			return reqToStatus(req, result);

		} else if (result instanceof Resp) {

			U.must(req != null && req.response() == result, "Unknown response instance was received as result!");

			return reqToStatus(req, result);

		} else if (result == null || result instanceof NotFound) {  // not found
			return HttpStatus.NOT_FOUND;

		} else if ((result instanceof Future<?>) || (result instanceof org.rapidoid.concurrent.Future<?>)) { // async

			if (req != null) {
				req.async();
			}

			return HttpStatus.ASYNC;

		} else if (result instanceof Results) {
			return ((Results) result).all(); // fetch while still inside tx (potentially)

		} else {
			return result;
		}

	}

	private HttpStatus reqToStatus(Req req, Object result) {
		if (req.isAsync()) {

			U.must(result instanceof HttpStatus || result instanceof Req || result instanceof Resp,
				"Didn't expect a direct result from an asynchronous handler!");

			return HttpStatus.ASYNC;
		}

		return HttpStatus.DONE;
	}

}
