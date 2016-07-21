package org.rapidoid.http.customize;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.MediaType;
import org.rapidoid.config.Config;
import org.rapidoid.http.HttpUtils;
import org.rapidoid.http.Req;
import org.rapidoid.http.Resp;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.value.Value;

import java.util.Map;

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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class DefaultErrorHandler extends RapidoidThing implements ErrorHandler {

	@Override
	public Object handleError(Req req, Resp resp, Throwable error) {

		Customization custom = Customization.of(req);

		Object result = handleError(req, resp, error, custom);

		if (result instanceof Throwable) {
			Throwable errResult = (Throwable) result;
			return renderError(req, resp, errResult, custom);

		} else {
			return result;
		}
	}

	private Object renderError(Req req, Resp resp, Throwable error, Customization custom) {
		if (resp.contentType() == MediaType.JSON_UTF_8) {
			return HttpUtils.getErrorInfo(resp, error);

		} else if (resp.contentType() == MediaType.PLAIN_TEXT_UTF_8) {
			return HttpUtils.getErrorMessageAndSetCode(resp, error);

		} else {
			return page(req, resp, error, custom);
		}
	}

	protected Object handleError(Req req, Resp resp, Throwable error, Customization custom) {
		Throwable err = error;

		// if the handler throws error -> process it
		for (int i = 0; ; i++) {

			ErrorHandler handler = custom.findErrorHandlerByType(error.getClass());

			try {

				if (handler != null) {
					return handler.handleError(req, resp, err);
				} else {
					return defaultErrorHandling(req, err);
				}

			} catch (Exception e) {

				if (i >= getMaxReThrowCount(req)) {
					return U.rte("Too many times an error was re-thrown by the error handler(s)!");
				}

				err = e;
			}
		}
	}

	protected int getMaxReThrowCount(@SuppressWarnings("UnusedParameters") Req req) {
		return 5; // override to customize
	}

	protected Object defaultErrorHandling(Req req, Throwable error) {
		boolean validation = Msc.isValidationError(error);

		if (!validation) {
			if (error instanceof SecurityException) {
				Log.warn("Access denied for request: " + req, "client", req.clientIpAddress());
			} else {
				Log.error("Error occurred when handling request: " + req, error);
			}
		} else {
			if (Log.isDebugEnabled()) {
				Log.debug("Validation error when handling request: " + req);
				error.printStackTrace();
			}
		}

		return error;
	}

	protected Object page(Req req, Resp resp, Throwable error, Customization custom) {

		Config segments = custom.appConfig().sub("segments");
		Value<String> home = custom.appConfig().sub("app").entry("home").str();

		if (error instanceof SecurityException) {
			return resp.code(403).view("login").mvc(true).model("embedded", req.attr("_embedded", false));
		} else {

			String seg = req.segment();
			String homeUri = seg != null ? segments.sub(seg).entry("home").str().orElse(home).or("/") : "/";

			Map<String, ?> errorInfo = HttpUtils.getErrorInfo(resp, error);
			resp.model().put("error", errorInfo);
			resp.model().put("home", homeUri);

			return resp.mvc(true).view("error");
		}
	}

}
