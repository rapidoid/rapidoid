/*-
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

package org.rapidoid.net;

import org.rapidoid.config.BasicConfig;
import org.rapidoid.net.impl.DefaultExchange;
import org.rapidoid.net.impl.RapidoidHelper;

public class NetworkingParams {

	private volatile String address;

	private volatile int port;

	private volatile int workers;

	private volatile int bufSizeKB;

	private volatile boolean noDelay;

	private volatile long maxPipeline;

	private volatile boolean syncBufs;

	private volatile boolean blockingAccept;

	private volatile Protocol protocol = null;

	private volatile Class<? extends DefaultExchange<?>> exchangeClass = null;

	private volatile Class<? extends RapidoidHelper> helperClass = RapidoidHelper.class;

	public NetworkingParams(BasicConfig cfg) {
		address = cfg.entry("address").or("0.0.0.0");
		port = cfg.entry("port").or(8080);
		workers = cfg.entry("workers").or(Runtime.getRuntime().availableProcessors());
		bufSizeKB = cfg.entry("bufSizeKB").or(16);
		noDelay = cfg.entry("noDelay").or(false);
		maxPipeline = cfg.entry("maxPipeline").or(0);
		syncBufs = cfg.entry("syncBufs").or(true);
		blockingAccept = cfg.entry("blockingAccept").or(false);
	}

	public String address() {
		return address;
	}

	public NetworkingParams address(String address) {
		this.address = address;
		return this;
	}

	public int port() {
		return port;
	}

	public NetworkingParams port(int port) {
		this.port = port;
		return this;
	}

	public int workers() {
		return workers;
	}

	public NetworkingParams workers(int workers) {
		this.workers = workers;
		return this;
	}

	public int bufSizeKB() {
		return bufSizeKB;
	}

	public NetworkingParams bufSizeKB(int bufSizeKB) {
		this.bufSizeKB = bufSizeKB;
		return this;
	}

	public boolean noDelay() {
		return noDelay;
	}

	public NetworkingParams noDelay(boolean noDelay) {
		this.noDelay = noDelay;
		return this;
	}

	public long maxPipeline() {
		return maxPipeline;
	}

	public NetworkingParams maxPipeline(long maxPipelineSize) {
		this.maxPipeline = maxPipelineSize;
		return this;
	}

	public boolean syncBufs() {
		return syncBufs;
	}

	public NetworkingParams syncBufs(boolean syncBufs) {
		this.syncBufs = syncBufs;
		return this;
	}

	public boolean blockingAccept() {
		return blockingAccept;
	}

	public NetworkingParams blockingAccept(boolean blockingAccept) {
		this.blockingAccept = blockingAccept;
		return this;
	}

	public Protocol protocol() {
		return protocol;
	}

	public NetworkingParams protocol(Protocol protocol) {
		this.protocol = protocol;
		return this;
	}

	public Class<? extends DefaultExchange<?>> exchangeClass() {
		return exchangeClass;
	}

	public NetworkingParams exchangeClass(Class<? extends DefaultExchange<?>> exchangeClass) {
		this.exchangeClass = exchangeClass;
		return this;
	}

	public Class<? extends RapidoidHelper> helperClass() {
		return helperClass;
	}

	public NetworkingParams helperClass(Class<? extends RapidoidHelper> helperClass) {
		this.helperClass = helperClass;
		return this;
	}
}
