package org.rapidoid.reverseproxy;

/*
 * #%L
 * rapidoid-http-server
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Str;
import org.rapidoid.http.Req;

import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.2.0")
public class ProxyMapping extends RapidoidThing {

	private final String prefix;

	private volatile List<ProxyUpstream> upstreams;

	private volatile LoadBalancer loadBalancer = new RoundRobinLoadBalancer();

	public ProxyMapping(String prefix, List<ProxyUpstream> upstreams) {
		this.prefix = prefix;
		this.upstreams = upstreams;
	}

	public String prefix() {
		return prefix;
	}

	public List<ProxyUpstream> upstreams() {
		return upstreams;
	}

	public ProxyMapping upstreams(List<ProxyUpstream> upstreams) {
		this.upstreams = upstreams;
		return this;
	}

	public boolean matches(Req req) {
		return req.path().startsWith(prefix);
	}

	public String getTargetUrl(Req req) {
		ProxyUpstream upstream = loadBalancer.pickUpstream(req, upstreams);

		String trimmed = prefix.equals("/") ? req.uri() : Str.triml(req.uri(), prefix);

		return upstream.url() + trimmed;
	}

	public LoadBalancer loadBalancer() {
		return loadBalancer;
	}

	public ProxyMapping loadBalancer(LoadBalancer loadBalancer) {
		this.loadBalancer = loadBalancer;
		return this;
	}

}
