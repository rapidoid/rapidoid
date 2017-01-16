package org.rapidoid.http;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

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
public class Headers extends RapidoidThing {

	public static final String CONTENT_TYPE = "Content-Type";
	public static final String CONTENT_DISPOSITION = "Content-Disposition";
	public static final String CACHE_CONTROL = "Cache-Control";

	public static final String LOCATION = "Location";
	public static final String REFERER = "Referer";
	public static final String USER_AGENT = "User-Agent";

	public static final String X_FORWARDED_FOR = "X-Forwarded-For";
	public static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";
	public static final String X_FORWARDED_HOST = "X-Forwarded-Host";

	public static final String ACCEPT_CHARSET = "Accept-Charset";
	public static final String ACCEPT_ENCODING = "Accept-Encoding";
	public static final String ACCEPT_DATETIME = "Accept-Datetime";

	public static final String AUTHORIZATION = "Authorization";
	public static final String CONNECTION = "Connection";

	public static final String EXPECT = "Expect";
	public static final String FORWARDED = "Forwarded";
	public static final String FROM = "From";

	public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
	public static final String IF_RANGE = "If-Range";
	public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";

	public static final String VIA = "Via";
	public static final String WARNING = "Warning";
	public static final String FRONT_END_HTTPS = "Front-End-Https";

	public static final String CF_CONNECTING_IP = "CF-Connecting-IP";
	public static final String CF_IPCOUNTRY = "CF-Ipcountry";
	public static final String CF_VISITOR = "Cf-Visitor";
	public static final String CF_RAY = "CF-Ray";

}
