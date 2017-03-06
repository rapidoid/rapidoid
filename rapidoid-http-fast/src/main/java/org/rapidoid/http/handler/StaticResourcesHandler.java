package org.rapidoid.http.handler;

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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.HttpStatus;
import org.rapidoid.http.HttpUtils;
import org.rapidoid.http.MediaType;
import org.rapidoid.http.Req;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.customize.StaticFilesSecurity;
import org.rapidoid.http.impl.lowlevel.HttpIO;
import org.rapidoid.http.impl.RouteOptions;
import org.rapidoid.io.Res;
import org.rapidoid.log.LogLevel;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.0.0")
public class StaticResourcesHandler extends AbstractHttpHandler {

	private final Customization customization;

	public StaticResourcesHandler(Customization customization) {
		super(new RouteOptions());
		this.customization = customization;
	}

	@Override
	public HttpStatus handle(Channel ctx, boolean isKeepAlive, Req req, Object extra) {

		if (!HttpUtils.isGetReq(req)) return HttpStatus.NOT_FOUND;

		try {
			String[] staticFilesLocations = customization.staticFilesPath();
			if (!U.isEmpty(staticFilesLocations)) {

				Res res = HttpUtils.staticResource(req, staticFilesLocations);
				if (res != null) {

					StaticFilesSecurity staticFilesSecurity = customization.staticFilesSecurity();

					if (staticFilesSecurity.canServe(req, res)) {
						byte[] bytes = res.getBytesOrNull();

						if (bytes != null) {
							MediaType contentType = U.or(MediaType.getByFileName(res.getName()), MediaType.BINARY);
							HttpIO.INSTANCE.write200(HttpUtils.maybe(req), ctx, isKeepAlive, contentType, bytes);
							return HttpStatus.DONE;
						}
					}
				}
			}

			return HttpStatus.NOT_FOUND;

		} catch (Exception e) {
			return HttpIO.INSTANCE.errorAndDone(req, e, LogLevel.ERROR);
		}
	}

	@Override
	public boolean needsParams() {
		return true;
	}

}
