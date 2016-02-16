package org.rapidoid.net;

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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.net.impl.RapidoidHelper;
import org.rapidoid.net.impl.RapidoidServerLoop;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class ServerBuilder {

	private String address = "0.0.0.0";

	private int port = 8888;

	private int workers = Runtime.getRuntime().availableProcessors();

	private org.rapidoid.net.Protocol protocol = null;

	private Class<? extends org.rapidoid.net.impl.DefaultExchange<?>> exchangeClass = null;

	private Class<? extends org.rapidoid.net.impl.RapidoidHelper> helperClass = RapidoidHelper.class;

	public synchronized ServerBuilder address(String address) {
		this.address = address;
		return this;
	}

	public synchronized String address() {
		return this.address;
	}

	public synchronized ServerBuilder port(int port) {
		this.port = port;
		return this;
	}

	public synchronized int port() {
		return this.port;
	}

	public synchronized ServerBuilder workers(int workers) {
		this.workers = workers;
		return this;
	}

	public synchronized int workers() {
		return this.workers;
	}

	public synchronized ServerBuilder protocol(org.rapidoid.net.Protocol protocol) {
		this.protocol = protocol;
		return this;
	}

	public synchronized org.rapidoid.net.Protocol protocol() {
		return this.protocol;
	}

	public synchronized ServerBuilder exchangeClass(Class<? extends org.rapidoid.net.impl.DefaultExchange<?>> exchangeClass) {
		this.exchangeClass = exchangeClass;
		return this;
	}

	public synchronized Class<? extends org.rapidoid.net.impl.DefaultExchange<?>> exchangeClass() {
		return this.exchangeClass;
	}

	public synchronized ServerBuilder helperClass(Class<? extends org.rapidoid.net.impl.RapidoidHelper> helperClass) {
		this.helperClass = helperClass;
		return this;
	}

	public synchronized Class<? extends org.rapidoid.net.impl.RapidoidHelper> helperClass() {
		return this.helperClass;
	}

	public synchronized Server build() {
		return new RapidoidServerLoop(protocol, exchangeClass, helperClass, address, port, workers);
	}

}
