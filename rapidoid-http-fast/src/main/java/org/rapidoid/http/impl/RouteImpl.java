package org.rapidoid.http.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cache.Caching;
import org.rapidoid.cache.Cache;
import org.rapidoid.http.HttpVerb;
import org.rapidoid.http.Route;
import org.rapidoid.http.RouteConfig;
import org.rapidoid.http.handler.HttpHandler;
import org.rapidoid.u.U;

import java.util.Date;

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
public class RouteImpl extends RapidoidThing implements Route {

	private volatile HttpVerb verb;

	private volatile String path;

	private volatile HttpHandler handler;

	private volatile RouteOptions options;

	private volatile Date lastChangedAt = new Date();

	private final Cache<HTTPCacheKey, CachedResp> cache;

	public RouteImpl(HttpVerb verb, String path, HttpHandler handler, RouteOptions options) {
		this.verb = verb;
		this.path = path;
		this.handler = handler;
		this.options = options;
		this.cache = createCache();
	}

	public static RouteImpl matching(HttpVerb verb, String path) {
		return new RouteImpl(verb, path, null, null);
	}

	protected Cache<HTTPCacheKey, CachedResp> createCache() {
		if (options == null || options.cacheTTL() <= 0) return null;

		return Caching.of(HTTPCacheKey.class, CachedResp.class)
			.name(verb + " " + path)
			.ttl(options.cacheTTL())
			.capacity(options.cacheCapacity())
			.manageable(true)
			.statistics(true)
			.build();
	}

	@Override
	public HttpVerb verb() {
		return verb;
	}

	@Override
	public String path() {
		return path;
	}

	@Override
	public HttpHandler handler() {
		return handler;
	}

	@Override
	public RouteConfig config() {
		return options;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		RouteImpl route = (RouteImpl) o;

		if (verb != route.verb) return false;
		return path.equals(route.path);
	}

	@Override
	public String toString() {
		RouteConfig cfg = config();
		return U.frmt("Route %s %s [zone %s] roles %s (TTL=%s) : %s",
			verb, path, cfg.zone(), cfg.roles(), cfg.cacheTTL(), handler);
	}

	@Override
	public int hashCode() {
		int result = verb.hashCode();
		result = 31 * result + path.hashCode();
		return result;
	}

	public RouteImpl handler(HttpHandler handler) {
		this.handler = handler;
		return this;
	}

	@Override
	public Cache<HTTPCacheKey, CachedResp> cache() {
		return cache;
	}

	@Override
	public Date lastChangedAt() {
		return lastChangedAt;
	}
}
