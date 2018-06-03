/*-
 * #%L
 * rapidoid-http-server
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
import org.rapidoid.annotation.TransactionMode;
import org.rapidoid.collection.Coll;
import org.rapidoid.http.HttpUtils;
import org.rapidoid.http.HttpWrapper;
import org.rapidoid.http.MediaType;
import org.rapidoid.http.RouteConfig;
import org.rapidoid.u.U;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class RouteOptions extends RapidoidThing implements RouteConfig {

	private volatile MediaType contentType = HttpUtils.getDefaultContentType();

	private volatile boolean contentTypeCustomized;

	private volatile String view;

	private volatile boolean mvc;

	private volatile String zone;

	private volatile boolean managed = true;

	private volatile TransactionMode transaction = TransactionMode.NONE;

	private final Set<String> roles = Coll.synchronizedSet();

	private volatile HttpWrapper[] wrappers;

	private volatile long cacheTTL;

	private volatile int cacheCapacity = 100;

	private volatile boolean internal;

	private volatile RouteMeta meta = new RouteMeta();

	@Override
	public String toString() {
		return "RouteOptions{" +
			"contentType=" + contentType +
			", contentTypeCustomized=" + contentTypeCustomized +
			", view='" + view + '\'' +
			", mvc=" + mvc +
			", zone='" + zone + '\'' +
			", managed=" + managed +
			", transaction=" + transaction +
			", roles=" + roles +
			", wrappers=" + Arrays.toString(wrappers) +
			", cacheTTL=" + cacheTTL +
			", cacheCapacity=" + cacheCapacity +
			", internal=" + internal +
			", meta=" + meta +
			'}';
	}

	@Override
	public MediaType contentType() {
		return contentType;
	}

	@Override
	public RouteOptions contentType(MediaType contentType) {
		this.contentType = contentType;
		this.contentTypeCustomized = true;
		return this;
	}

	@Override
	public String view() {
		return view;
	}

	@Override
	public RouteOptions view(String view) {
		HttpUtils.validateViewName(view);
		this.view = view;
		return this;
	}

	@Override
	public boolean mvc() {
		return mvc;
	}

	@Override
	public RouteOptions mvc(boolean mvc) {
		this.mvc = mvc;
		return this;
	}

	@Override
	public TransactionMode transaction() {
		return transaction;
	}

	@Override
	public RouteOptions transaction(TransactionMode transaction) {
		this.transaction = transaction;
		return this;
	}

	@Override
	public Set<String> roles() {
		return roles;
	}

	@Override
	public RouteOptions roles(String... roles) {
		Coll.assign(this.roles, roles);
		return this;
	}

	@Override
	public HttpWrapper[] wrappers() {
		return wrappers;
	}

	@Override
	public RouteOptions wrappers(HttpWrapper... wrappers) {
		this.wrappers = wrappers;
		return this;
	}

	@Override
	public String zone() {
		return zone;
	}

	@Override
	public RouteOptions zone(String zone) {
		this.zone = zone;
		return this;
	}

	@Override
	public boolean managed() {
		return managed;
	}

	@Override
	public RouteOptions managed(boolean managed) {
		this.managed = managed;
		return this;
	}

	@Override
	public long cacheTTL() {
		return cacheTTL;
	}

	@Override
	public RouteOptions cacheTTL(long cacheTTL) {
		this.cacheTTL = cacheTTL;
		return this;
	}

	@Override
	public int cacheCapacity() {
		return cacheCapacity;
	}

	@Override
	public RouteOptions cacheCapacity(int cacheCapacity) {
		this.cacheCapacity = cacheCapacity;
		return this;
	}

	@Override
	public RouteMeta meta() {
		return meta;
	}

	@Override
	public RouteOptions meta(RouteMeta meta) {
		this.meta = meta;
		return this;
	}

	@Override
	public boolean internal() {
		return internal;
	}

	@Override
	public RouteOptions internal(boolean internal) {
		this.internal = internal;
		return this;
	}

	public RouteOptions copy() {
		RouteOptions copy = new RouteOptions();

		copy.contentType = this.contentType;
		copy.view = this.view;
		copy.mvc = this.mvc;
		copy.transaction = this.transaction;
		Coll.assign(copy.roles, this.roles);
		copy.wrappers = U.array(this.wrappers);
		copy.zone = this.zone;
		copy.managed = this.managed;
		copy.cacheTTL = this.cacheTTL;
		copy.cacheCapacity = this.cacheCapacity;
		copy.internal = this.internal;
		copy.meta = this.meta.copy();

		return copy;
	}

	public boolean contentTypeCustomized() {
		return contentTypeCustomized;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RouteOptions that = (RouteOptions) o;
		return contentTypeCustomized == that.contentTypeCustomized &&
			mvc == that.mvc &&
			managed == that.managed &&
			cacheTTL == that.cacheTTL &&
			cacheCapacity == that.cacheCapacity &&
			internal == that.internal &&
			Objects.equals(contentType, that.contentType) &&
			Objects.equals(view, that.view) &&
			Objects.equals(zone, that.zone) &&
			transaction == that.transaction &&
			Objects.equals(roles, that.roles) &&
			Arrays.equals(wrappers, that.wrappers) &&
			Objects.equals(meta, that.meta);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(contentType, contentTypeCustomized, view, mvc, zone, managed, transaction, roles, cacheTTL, cacheCapacity, internal, meta);
		result = 31 * result + Arrays.hashCode(wrappers);
		return result;
	}
}
