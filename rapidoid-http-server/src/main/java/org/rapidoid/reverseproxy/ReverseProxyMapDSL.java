package org.rapidoid.reverseproxy;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.setup.On;
import org.rapidoid.setup.OnRoute;
import org.rapidoid.setup.Setup;
import org.rapidoid.u.U;

import java.util.List;

/*
 * #%L
 * rapidoid-http-server
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
@Since("5.2.0")
public class ReverseProxyMapDSL extends RapidoidThing {

	private final String uriPrefix;

	private volatile List<String> upstreams;

	private volatile String[] roles;

	private volatile Long cacheTTL;

	private volatile Integer cacheCapacity;

	private volatile LoadBalancer loadBalancer;

	public ReverseProxyMapDSL(String uriPrefix) {
		this.uriPrefix = uriPrefix;
	}

	public ReverseProxyMapDSL to(String... upstreams) {
		return to(U.list(upstreams));
	}

	public ReverseProxyMapDSL to(List<String> upstreams) {
		this.upstreams = ProxyUpstream.refine(upstreams);
		return this;
	}

	public ReverseProxy addTo(Setup setup) {
		Log.info("!Reverse proxy mapping", "!uriPrefix", uriPrefix, "!upstreams", upstreams);

		ReverseProxy proxy = createReverseProxy();

		U.must(uriPrefix.startsWith("/"), "The URI prefix must start with '/'");

		String path = uriPrefix.equals("/") ? "/*" : uriPrefix + "/*";
		OnRoute route = setup.any(path);

		if (roles != null) route.roles(roles);
		if (cacheTTL != null) route.cacheTTL(cacheTTL);
		if (cacheCapacity != null) route.cacheCapacity(cacheCapacity);

		route.serve(proxy);

		return proxy;
	}

	public ReverseProxy add() {
		return addTo(On.setup());
	}

	private ReverseProxy createReverseProxy() {
		List<ProxyUpstream> proxyUpstreams = U.list();

		U.notNull(upstreams, "proxy upstreams");

		for (String upstream : upstreams) {
			proxyUpstreams.add(new ProxyUpstream(upstream));
		}

		LoadBalancer balancer = loadBalancer != null ? loadBalancer : new RoundRobinLoadBalancer();
		ProxyMapping mapping = new ProxyMapping(uriPrefix, balancer, proxyUpstreams);
		return new ReverseProxy(mapping);
	}

	public String[] roles() {
		return roles;
	}

	public ReverseProxyMapDSL roles(String... roles) {
		this.roles = roles;
		return this;
	}

	public Long cacheTTL() {
		return cacheTTL;
	}

	public ReverseProxyMapDSL cacheTTL(long cacheTTL) {
		this.cacheTTL = cacheTTL;
		return this;
	}

	public Integer cacheCapacity() {
		return cacheCapacity;
	}

	public ReverseProxyMapDSL cacheCapacity(int cacheCapacity) {
		this.cacheCapacity = cacheCapacity;
		return this;
	}

	public LoadBalancer loadBalancer() {
		return loadBalancer;
	}

	public ReverseProxyMapDSL loadBalancer(LoadBalancer loadBalancer) {
		this.loadBalancer = loadBalancer;
		return this;
	}
}
