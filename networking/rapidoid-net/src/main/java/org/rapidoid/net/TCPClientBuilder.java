package org.rapidoid.net;

/*
 * #%L
 * rapidoid-net
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
import org.rapidoid.config.Conf;
import org.rapidoid.net.impl.DefaultExchange;
import org.rapidoid.net.impl.RapidoidClientLoop;
import org.rapidoid.net.impl.RapidoidHelper;

import javax.net.ssl.SSLContext;

@Authors("Nikolche Mihajlovski")
@Since("5.5.0")
public class TCPClientBuilder extends RapidoidThing {

	private volatile String host;

	private volatile int port;

	private volatile int connections;

	private volatile boolean reconnecting = true;

	private volatile int bufSizeKB = Conf.NET.entry("bufSizeKB").or(16);

	private volatile boolean syncBufs = Conf.NET.entry("syncBufs").or(true);

	private volatile int workers = Conf.NET.entry("workers").or(Runtime.getRuntime().availableProcessors());

	private volatile boolean noDelay = Conf.NET.entry("noDelay").or(false);

	private volatile Protocol protocol;

	private volatile Class<? extends DefaultExchange<?>> exchangeClass = null;

	private volatile Class<? extends RapidoidHelper> helperClass = RapidoidHelper.class;

	public String host() {
		return host;
	}

	public TCPClientBuilder host(String host) {
		this.host = host;
		return this;
	}

	public int port() {
		return port;
	}

	public TCPClientBuilder port(int port) {
		this.port = port;
		return this;
	}

	public int connections() {
		return connections;
	}

	public TCPClientBuilder connections(int connections) {
		this.connections = connections;
		return this;
	}

	public boolean reconnecting() {
		return reconnecting;
	}

	public TCPClientBuilder reconnecting(boolean reconnecting) {
		this.reconnecting = reconnecting;
		return this;
	}

	public int bufSizeKB() {
		return bufSizeKB;
	}

	public TCPClientBuilder bufSizeKB(int bufSizeKB) {
		this.bufSizeKB = bufSizeKB;
		return this;
	}

	public boolean syncBufs() {
		return syncBufs;
	}

	public TCPClientBuilder syncBufs(boolean syncBufs) {
		this.syncBufs = syncBufs;
		return this;
	}

	public int workers() {
		return workers;
	}

	public TCPClientBuilder workers(int workers) {
		this.workers = workers;
		return this;
	}

	public boolean noDelay() {
		return noDelay;
	}

	public TCPClientBuilder noDelay(boolean noDelay) {
		this.noDelay = noDelay;
		return this;
	}

	public Protocol protocol() {
		return protocol;
	}

	public TCPClientBuilder protocol(Protocol protocol) {
		this.protocol = protocol;
		return this;
	}

	public Class<? extends DefaultExchange<?>> exchangeClass() {
		return exchangeClass;
	}

	public TCPClientBuilder exchangeClass(Class<? extends DefaultExchange<?>> exchangeClass) {
		this.exchangeClass = exchangeClass;
		return this;
	}

	public Class<? extends RapidoidHelper> helperClass() {
		return helperClass;
	}

	public TCPClientBuilder helperClass(Class<? extends RapidoidHelper> helperClass) {
		this.helperClass = helperClass;
		return this;
	}

	public TCPClient build() {

		// FIXME TLS support (see ServerBuilder)
		SSLContext tlsCtx = null;

		return new RapidoidClientLoop(protocol, exchangeClass, helperClass, host, port, workers,
			bufSizeKB, noDelay, syncBufs, reconnecting, connections, tlsCtx);
	}

}
