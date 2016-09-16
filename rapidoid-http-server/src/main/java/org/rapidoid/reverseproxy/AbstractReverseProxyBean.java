package org.rapidoid.reverseproxy;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.HttpClient;
import org.rapidoid.util.LazyInit;

import java.util.concurrent.Callable;

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
public abstract class AbstractReverseProxyBean<T> extends RapidoidThing {

	private volatile boolean reuseConnections = true;

	private volatile int maxConnTotal = 100;

	private volatile int maxConnPerRoute = 100;

	private final LazyInit<HttpClient> client = new LazyInit<HttpClient>(new Callable<HttpClient>() {
		@Override
		public HttpClient call() throws Exception {
			return createClient();
		}
	});

	protected abstract HttpClient createClient();

	@SuppressWarnings("unchecked")
	protected T me() {
		return (T) this;
	}

	public boolean reuseConnections() {
		return reuseConnections;
	}

	public T reuseConnections(boolean reuseConnections) {
		this.reuseConnections = reuseConnections;
		return me();
	}

	public int maxConnTotal() {
		return maxConnTotal;
	}

	public T maxConnTotal(int maxConnTotal) {
		this.maxConnTotal = maxConnTotal;
		return me();
	}

	public int maxConnPerRoute() {
		return maxConnPerRoute;
	}

	public T maxConnPerRoute(int maxConnPerRoute) {
		this.maxConnPerRoute = maxConnPerRoute;
		return me();
	}

	public HttpClient client() {
		return client.getValue();
	}

	public T client(HttpClient client) {
		this.client.setValue(client);
		return me();
	}

	protected HttpClient getOrCreateClient() {
		return client.get();
	}

}
