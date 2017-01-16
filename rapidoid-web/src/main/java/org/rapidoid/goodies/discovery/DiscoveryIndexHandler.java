package org.rapidoid.goodies.discovery;

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
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqHandler;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class DiscoveryIndexHandler extends RapidoidThing implements ReqHandler {

	private final DiscoveryState state;

	public DiscoveryIndexHandler(DiscoveryState state) {
		this.state = state;
	}

	@Override
	public Object execute(Req req) throws Exception {
		String scope = req.param("scope");
		return state.clients.get(scope);
	}

}
