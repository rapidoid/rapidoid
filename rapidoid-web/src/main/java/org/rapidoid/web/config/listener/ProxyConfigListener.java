package org.rapidoid.web.config.listener;

/*
 * #%L
 * rapidoid-web
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.ConfigChanges;
import org.rapidoid.lambda.Operation;
import org.rapidoid.reverseproxy.Reverse;
import org.rapidoid.reverseproxy.ReverseProxyMapDSL;
import org.rapidoid.setup.App;
import org.rapidoid.web.config.bean.ProxyConfig;

import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class ProxyConfigListener extends RapidoidThing implements Operation<ConfigChanges> {

	@Override
	public void execute(ConfigChanges changes) throws Exception {
		for (Map.Entry<String, ProxyConfig> e : changes.getAddedOrChangedAs(ProxyConfig.class).entrySet()) {

			String uri = e.getKey().trim();
			ProxyConfig proxy = e.getValue();

			applyProxyEntry(uri, proxy);
		}
	}

	private void applyProxyEntry(String uri, ProxyConfig config) {
		ReverseProxyMapDSL proxy = Reverse.proxy(uri);

		if (config.upstreams != null) proxy.to(config.upstreams);
		if (config.cacheTTL != null) proxy.cacheTTL(config.cacheTTL);
		if (config.cacheCapacity != null) proxy.cacheCapacity(config.cacheCapacity);

		if (config.roles != null) {
			proxy.roles(config.roles);
			App.boot().auth();
		}

		proxy.add();
	}

}
