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

	private final Config segments;
	private final Value<String> home;

	public DefaultErrorHandler(Customization customization) {
		this.segments = customization.appConfig().sub("segments");
		this.home = customization.appConfig().sub("app").entry("home").str();
	}

	@Override
	public Object handleError(Req req, Resp resp, Throwable error) {

		boolean validation = Msc.isValidationError(error);

		if (!validation) {
			if (error instanceof SecurityException) {
				Log.warn("Access denied for request: " + req, "client", req.clientIpAddress());
			} else {
				Log.error("Error occurred when handling request: " + req, error);
			}
		} else {
			Log.debug("Validation error when handling request: " + req);
			if (Log.isDebugEnabled()) {
				error.printStackTrace();
			}
		}

		if (resp.contentType() == MediaType.JSON_UTF_8) {
			return HttpUtils.getErrorInfo(resp, error);

		} else if (resp.contentType() == MediaType.PLAIN_TEXT_UTF_8) {
			return HttpUtils.getErrorMessageAndSetCode(resp, error);

		} else {
			return page(req, resp, error);
		}
	}

	private Object page(Req req, Resp resp, Throwable error) {
		if (error instanceof SecurityException) {
			return resp.code(403).view("login").mvc(true);
		} else {

			String seg = req.segment();
			String homeUri = seg != null ? segments.sub(seg).entry("home").str().orElse(this.home).or("/") : "/";

			Map<String, ?> errorInfo = HttpUtils.getErrorInfo(resp, error);
			resp.model().put("error", errorInfo);
			resp.model().put("home", homeUri);

			return resp.mvc(true).view("error");
		}
	}

}
