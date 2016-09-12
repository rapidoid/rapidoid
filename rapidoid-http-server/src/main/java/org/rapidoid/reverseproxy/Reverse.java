package org.rapidoid.reverseproxy;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.setup.On;

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
public class Reverse extends RapidoidThing {

	private static volatile ReverseProxy DEFAULT_PROXY;

	public static synchronized ReverseProxy proxy() {
		if (DEFAULT_PROXY == null) {
			DEFAULT_PROXY = newProxy();
			On.req(DEFAULT_PROXY);
		}

		return DEFAULT_PROXY;
	}

	public static ReverseProxy newProxy() {
		return new ReverseProxy();
	}

	public static synchronized void reset() {
		if (DEFAULT_PROXY != null) {
			DEFAULT_PROXY.reset();
			DEFAULT_PROXY = null;
		}
	}

}
