package org.rapidoid.net;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.net.impl.RapidoidHelper;
import org.rapidoid.net.impl.RapidoidServerLoop;

/*
 * #%L
 * rapidoid-net
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
@Since("5.1.0")
public class ServerBuilder extends RapidoidThing {

	private volatile String address = "0.0.0.0";

	private volatile int port = 8888;

	private volatile int workers = Runtime.getRuntime().availableProcessors();

	private volatile org.rapidoid.net.Protocol protocol = null;

	private volatile Class<? extends org.rapidoid.net.impl.DefaultExchange<?>> exchangeClass = null;

	private volatile Class<? extends org.rapidoid.net.impl.RapidoidHelper> helperClass = RapidoidHelper.class;

	private volatile int bufSizeKB = 16;

	private volatile boolean noNelay = false;

	private volatile boolean syncBufs = true;

	public ServerBuilder address(String address) {
		this.address = address;
		return this;
	}

	public String address() {
		return this.address;
	}

	public ServerBuilder port(int port) {
		this.port = port;
		return this;
	}

	public int port() {
		return this.port;
	}

	public ServerBuilder workers(int workers) {
		this.workers = workers;
		return this;
	}

	public int workers() {
		return this.workers;
	}

	public ServerBuilder protocol(org.rapidoid.net.Protocol protocol) {
		this.protocol = protocol;
		return this;
	}

	public org.rapidoid.net.Protocol protocol() {
		return this.protocol;
	}

	public ServerBuilder exchangeClass(Class<? extends org.rapidoid.net.impl.DefaultExchange<?>> exchangeClass) {
		this.exchangeClass = exchangeClass;
		return this;
	}

	public Class<? extends org.rapidoid.net.impl.DefaultExchange<?>> exchangeClass() {
		return this.exchangeClass;
	}

	public ServerBuilder helperClass(Class<? extends org.rapidoid.net.impl.RapidoidHelper> helperClass) {
		this.helperClass = helperClass;
		return this;
	}

	public Class<? extends org.rapidoid.net.impl.RapidoidHelper> helperClass() {
		return this.helperClass;
	}

	public int bufSizeKB() {
		return bufSizeKB;
	}

	public void bufSizeKB(int bufSizeKB) {
		this.bufSizeKB = bufSizeKB;
	}

	public boolean noNelay() {
		return noNelay;
	}

	public void noNelay(boolean noNelay) {
		this.noNelay = noNelay;
	}

	public boolean syncBufs() {
		return syncBufs;
	}

	public ServerBuilder syncBufs(boolean syncBufs) {
		this.syncBufs = syncBufs;
		return this;
	}

	public Server build() {
		return new RapidoidServerLoop(protocol, exchangeClass, helperClass, address, port, workers, bufSizeKB, noNelay, syncBufs);
	}

}
