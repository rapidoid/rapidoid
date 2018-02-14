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

package org.rapidoid.http.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.*;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.customize.HttpResponseRenderer;
import org.rapidoid.http.impl.lowlevel.HttpIO;
import org.rapidoid.log.Log;
import org.rapidoid.log.LogLevel;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

@Authors("Nikolche Mihajlovski")
@Since("5.5.2")
public class BodyRenderer extends RapidoidThing {

	static RespBody toRespBody(Req req, RespImpl resp) {
		try {
			return createRespBodyFromResult(req, resp);

		} catch (Throwable e) {
			HttpIO.INSTANCE.error(req, e, LogLevel.ERROR);

			try {
				return createRespBodyFromResult(req, resp);

			} catch (Exception e1) {
				Log.error("Internal rendering error!", e1);
				return new RespBodyBytes(HttpUtils.getErrorMessageAndSetCode(resp, e1).getBytes());
			}
		}
	}

	private static RespBody createRespBodyFromResult(Req req, RespImpl resp) {
		Object result = resp.result();
		Object body = resp.body();

		if (result instanceof RespBody) {
			return (RespBody) result;

		} else if (body instanceof RespBody) {
			return (RespBody) body;

		} else if (resp.mvc()) {
			byte[] bytes = ResponseRenderer.renderMvc((ReqImpl) req, resp);
			HttpUtils.postProcessResponse(resp); // the response might have been changed, so post-process again
			return new RespBodyBytes(bytes);

		} else if (body != null) {
			return new RespBodyBytes(Msc.toBytes(body));

		} else if (result != null) {
			return resultToRespBody(resp, result);

		} else {
			throw U.rte("There's nothing to render!");
		}
	}

	public static RespBody resultToRespBody(Resp resp, Object result) {
		if (result instanceof RespBody) return (RespBody) result;

		byte[] bytes = HttpUtils.responseToBytes(resp.request(), result, resp.contentType(), mediaResponseRenderer(resp));

		return new RespBodyBytes(bytes);
	}

	private static HttpResponseRenderer mediaResponseRenderer(Resp resp) {
		Customization customization = Customization.of(resp.request());

		if (resp.contentType().equals(MediaType.JSON)) {
			return customization.jsonResponseRenderer();

		} else if (resp.contentType().equals(MediaType.XML_UTF_8)) {
			return customization.xmlResponseRenderer();

		} else {
			// defaults to json
			return customization.jsonResponseRenderer();
		}
	}

}
