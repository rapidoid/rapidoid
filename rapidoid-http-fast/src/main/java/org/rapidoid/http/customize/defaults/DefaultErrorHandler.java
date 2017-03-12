package org.rapidoid.http.customize.defaults;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.BasicConfig;
import org.rapidoid.http.*;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.customize.ErrorHandler;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.Map;

/*
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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class DefaultErrorHandler extends RapidoidThing implements ErrorHandler {

	@Override
	public Object handleError(Req req, Resp resp, Throwable error) {

		resp.result(null);

		Customization custom = Customization.of(req);

		Object result = handleError(req, resp, error, custom);

		if (result instanceof Throwable) {
			Throwable errResult = (Throwable) result;
			return renderError(req, resp, errResult);

		} else {
			return result;
		}
	}

	private Object renderError(Req req, Resp resp, Throwable error) {

		if (resp.contentType() == MediaType.JSON) {
			return HttpUtils.getErrorInfo(resp, error);

		} else if (resp.contentType() == MediaType.PLAIN_TEXT_UTF_8) {
			return HttpUtils.getErrorMessageAndSetCode(resp, error);

		} else {
			return page(req, resp, error);
		}
	}

	protected Object handleError(Req req, Resp resp, Throwable error, Customization custom) {
		Throwable err = error;

		// if the handler throws error -> process it
		for (int i = 0; ; i++) {

			resp.result(null);

			ErrorHandler handler = custom.findErrorHandlerByType(error.getClass());

			try {

				Object result = null;

				if (handler != null) {
					result = handler.handleError(req, resp, err);
				}

				return result != null ? result : defaultErrorHandling(req, err);

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

		if (error instanceof NotFound) {
			Resp resp = req.response().code(404);

			if (resp.contentType() == MediaType.JSON) {
				return error;
			} else {
				return resp.view("404").result(U.map("req", req));
			}
		}

		return error;
	}

	protected Object page(Req req, Resp resp, Throwable error) {

		if (error instanceof SecurityException) {
			resp.model("embedded", req.attr("_embedded", false));
			resp.model("req", req);
			resp.model("loginUri", Msc.specialUri("login"));
			return resp.code(403).view("login").mvc(true);

		} else {

			BasicConfig zone = HttpUtils.zone(req);
			String home = zone.entry("home").or("/");

			Map<String, ?> errorInfo = HttpUtils.getErrorInfo(resp, error);
			resp.model("req", req);
			resp.model("error", errorInfo);
			resp.model("home", home);

			return resp.mvc(true).view("error");
		}
	}

}
