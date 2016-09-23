package org.rapidoid.reverseproxy;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import java.util.List;

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

@Authors("Nikolche Mihajlovski")
@Since("5.2.0")
public class ReverseProxyMapDSL extends RapidoidThing {

	private final ReverseProxy proxy;

	private final String uriPrefix;

	public ReverseProxyMapDSL(ReverseProxy proxy, String uriPrefix) {
		this.proxy = proxy;
		this.uriPrefix = uriPrefix;
	}

	public ProxyMapping to(String... upstreams) {
		return to(U.list(upstreams));
	}

	public ProxyMapping to(List<String> upstreams) {

		upstreams = refine(upstreams);

		Log.info("!Reverse proxy mapping", "!uriPrefix", uriPrefix, "!upstreams", upstreams);

		List<ProxyUpstream> proxyUpstreams = U.list();

		for (String upstream : upstreams) {
			proxyUpstreams.add(new ProxyUpstream(upstream));
		}

		ProxyMapping mapping = new ProxyMapping(uriPrefix, proxyUpstreams);
		proxy.mappings().add(mapping);

		return mapping;
	}

	private static List<String> refine(List<String> upstreams) {
		List<String> refinedUpstreams = U.list();

		for (String upstream : upstreams) {
			if (!upstream.startsWith("http://") && !upstream.startsWith("https://")) {
				upstream = "http://" + upstream;
			}

			refinedUpstreams.add(upstream);
		}

		return refinedUpstreams;
	}

}
