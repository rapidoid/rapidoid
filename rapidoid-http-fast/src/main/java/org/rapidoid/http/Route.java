package org.rapidoid.http;

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
import org.rapidoid.cache.Cache;
import org.rapidoid.http.handler.HttpHandler;
import org.rapidoid.http.impl.CachedResp;
import org.rapidoid.http.impl.HTTPCacheKey;

import java.util.Date;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public interface Route {

	HttpVerb verb();

	String path();

	HttpHandler handler();

	RouteConfig config();

	Cache<HTTPCacheKey, CachedResp> cache();

	Date lastChangedAt();

}
