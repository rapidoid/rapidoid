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
import java.util.concurrent.atomic.AtomicLong;

@Authors("Nikolche Mihajlovski")
@Since("5.2.0")
public class RoundRobinLoadBalancer extends RapidoidThing implements LoadBalancer {

	private final ProxyMapping mapping;

	private final AtomicLong counter = new AtomicLong();

	public RoundRobinLoadBalancer(ProxyMapping mapping) {
		this.mapping = mapping;
	}

	@Override
	public String getTargetUrl(Req req) {
		long n = counter.incrementAndGet();

		List<String> targets = mapping.targets();
		int index = (int) (n % targets.size());
		String target = targets.get(index);

		String trimmed = Str.triml(req.uri(), mapping.prefix());
		return target + trimmed;
	}

}
