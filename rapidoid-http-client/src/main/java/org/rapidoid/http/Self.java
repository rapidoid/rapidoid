package org.rapidoid.http;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

/*
 * #%L
 * rapidoid-http-client
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
@Since("5.3.0")
public class Self extends RapidoidThing {

	public static String localUrl(String urlPath) {
		U.must(urlPath.startsWith("/"), "Invalid URL path, it must start with '/'!");
		int port = Conf.ON.entry("port").or(8080);
		return Msc.http() + "://localhost:" + port + urlPath;
	}

	public static HttpReq req(HttpVerb verb, String urlPath) {
		return HTTP.req().verb(verb).url(localUrl(urlPath));
	}

	public static HttpReq get(String urlPath) {
		return req(HttpVerb.GET, urlPath);
	}

	public static HttpReq post(String urlPath) {
		return req(HttpVerb.POST, urlPath);
	}

	public static HttpReq put(String urlPath) {
		return req(HttpVerb.PUT, urlPath);
	}

	public static HttpReq delete(String urlPath) {
		return req(HttpVerb.DELETE, urlPath);
	}

	public static HttpReq patch(String urlPath) {
		return req(HttpVerb.PATCH, urlPath);
	}

	public static HttpReq options(String urlPath) {
		return req(HttpVerb.OPTIONS, urlPath);
	}

	public static HttpReq head(String urlPath) {
		return req(HttpVerb.HEAD, urlPath);
	}

	public static HttpReq trace(String urlPath) {
		return req(HttpVerb.TRACE, urlPath);
	}

}
