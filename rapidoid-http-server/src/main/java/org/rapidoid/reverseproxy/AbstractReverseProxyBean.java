package org.rapidoid.reverseproxy;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.config.Config;
import org.rapidoid.http.HttpClient;
import org.rapidoid.util.LazyInit;

import java.util.concurrent.Callable;

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
public abstract class AbstractReverseProxyBean<T> extends RapidoidThing {

	private static final Config CFG = Conf.REVERSE_PROXY;
	private static final Config SET_HEADERS = CFG.sub("setHeaders");

	private volatile long retryDelay = CFG.entry("retryDelay").or(300);
	private volatile long timeout = CFG.entry("timeout").or(10000);

	private volatile boolean reuseConnections = CFG.entry("reuseConnections").or(true);

	private volatile int maxConnections = CFG.entry("maxConnections").or(100);
	private volatile int maxConnectionsPerRoute = CFG.entry("maxConnectionsPerRoute").or(100);

	private volatile boolean setXForwardedForHeader = SET_HEADERS.entry("X-Forwarded-For").or(true);
	private volatile boolean setXClientIPHeader = SET_HEADERS.entry("X-Client-IP").or(false);
	private volatile boolean setXRealIPHeader = SET_HEADERS.entry("X-Real-IP").or(false);
	private volatile boolean setXUsernameHeader = SET_HEADERS.entry("X-Username").or(false);
	private volatile boolean setXRolesHeader = SET_HEADERS.entry("X-Roles").or(false);

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

	public long retryDelay() {
		return retryDelay;
	}

	public AbstractReverseProxyBean retryDelay(long retryDelay) {
		this.retryDelay = retryDelay;
		return this;
	}

	public long timeout() {
		return timeout;
	}

	public AbstractReverseProxyBean timeout(long timeout) {
		this.timeout = timeout;
		return this;
	}

	public int maxConnections() {
		return maxConnections;
	}

	public AbstractReverseProxyBean maxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
		return this;
	}

	public int maxConnectionsPerRoute() {
		return maxConnectionsPerRoute;
	}

	public AbstractReverseProxyBean maxConnectionsPerRoute(int maxConnectionsPerRoute) {
		this.maxConnectionsPerRoute = maxConnectionsPerRoute;
		return this;
	}

	public HttpClient client() {
		return client.getValue();
	}

	public T client(HttpClient client) {
		this.client.setValue(client);
		return me();
	}

	public boolean setXForwardedForHeader() {
		return setXForwardedForHeader;
	}

	public AbstractReverseProxyBean setXForwardedForHeader(boolean setXForwardedForHeader) {
		this.setXForwardedForHeader = setXForwardedForHeader;
		return this;
	}

	public boolean setXClientIPHeader() {
		return setXClientIPHeader;
	}

	public AbstractReverseProxyBean setXClientIPHeader(boolean setXClientIPHeader) {
		this.setXClientIPHeader = setXClientIPHeader;
		return this;
	}

	public boolean setXRealIPHeader() {
		return setXRealIPHeader;
	}

	public AbstractReverseProxyBean setXRealIPHeader(boolean setXRealIPHeader) {
		this.setXRealIPHeader = setXRealIPHeader;
		return this;
	}

	public boolean setXUsernameHeader() {
		return setXUsernameHeader;
	}

	public AbstractReverseProxyBean setXUsernameHeader(boolean setXUsernameHeader) {
		this.setXUsernameHeader = setXUsernameHeader;
		return this;
	}

	public boolean setXRolesHeader() {
		return setXRolesHeader;
	}

	public AbstractReverseProxyBean setXRolesHeader(boolean setXRolesHeader) {
		this.setXRolesHeader = setXRolesHeader;
		return this;
	}

	protected HttpClient getOrCreateClient() {
		return client.get();
	}

}
