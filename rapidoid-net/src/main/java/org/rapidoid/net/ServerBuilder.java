package org.rapidoid.net;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.net.impl.RapidoidHelper;
import org.rapidoid.net.impl.RapidoidServerLoop;
import org.rapidoid.net.tls.TLSUtil;
import org.rapidoid.util.MscOpts;

import javax.net.ssl.SSLContext;

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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class ServerBuilder extends RapidoidThing {

	private volatile String address = Conf.NET.entry("address").or("0.0.0.0");

	private volatile int port = Conf.NET.entry("port").or(8080);

	private volatile int workers = Conf.NET.entry("workers").or(Runtime.getRuntime().availableProcessors());

	private volatile int bufSizeKB = Conf.NET.entry("bufSizeKB").or(16);

	private volatile boolean noDelay = Conf.NET.entry("noDelay").or(false);

	private volatile boolean syncBufs = Conf.NET.entry("syncBufs").or(true);

	private volatile org.rapidoid.net.Protocol protocol = null;

	private volatile Class<? extends org.rapidoid.net.impl.DefaultExchange<?>> exchangeClass = null;

	private volatile Class<? extends org.rapidoid.net.impl.RapidoidHelper> helperClass = RapidoidHelper.class;

	// auto-activate if TLS is enabled
	private volatile boolean tls = MscOpts.isTLSEnabled();

	private volatile String keystore = Conf.TLS.entry("keystore").or("");

	private volatile char[] keystorePassword = Conf.TLS.entry("keystorePassword").or("").toCharArray();

	private volatile char[] keyManagerPassword = Conf.TLS.entry("keyManagerPassword").or("").toCharArray();

	private volatile String truststore = Conf.TLS.entry("truststore").or("");

	private volatile char[] truststorePassword = Conf.TLS.entry("truststorePassword").or("").toCharArray();

	private volatile boolean selfSignedTLS = Conf.TLS.is("selfSigned");

	private volatile SSLContext tlsContext;

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

	public boolean noDelay() {
		return noDelay;
	}

	public void noDelay(boolean noDelay) {
		this.noDelay = noDelay;
	}

	public boolean syncBufs() {
		return syncBufs;
	}

	public ServerBuilder syncBufs(boolean syncBufs) {
		this.syncBufs = syncBufs;
		return this;
	}

	public boolean tls() {
		return tls;
	}

	public ServerBuilder tls(boolean tls) {
		this.tls = tls;
		return this;
	}

	public String keystore() {
		return keystore;
	}

	public ServerBuilder keystore(String keystore) {
		this.keystore = keystore;
		return this;
	}

	public char[] keystorePassword() {
		return keystorePassword;
	}

	public ServerBuilder keystorePassword(char[] keystorePassword) {
		this.keystorePassword = keystorePassword;
		return this;
	}

	public char[] keyManagerPassword() {
		return keyManagerPassword;
	}

	public ServerBuilder keyManagerPassword(char[] keyManagerPassword) {
		this.keyManagerPassword = keyManagerPassword;
		return this;
	}

	public String truststore() {
		return truststore;
	}

	public ServerBuilder truststore(String truststore) {
		this.truststore = truststore;
		return this;
	}

	public char[] truststorePassword() {
		return truststorePassword;
	}

	public ServerBuilder truststorePassword(char[] truststorePassword) {
		this.truststorePassword = truststorePassword;
		return this;
	}

	public SSLContext tlsContext() {
		return tlsContext;
	}

	public ServerBuilder tlsContext(SSLContext tlsContext) {
		this.tlsContext = tlsContext;
		return this;
	}

	public synchronized Server build() {

		if (tls && tlsContext == null) {
			tlsContext = TLSUtil.createContext(keystore, keystorePassword, keyManagerPassword, truststore, truststorePassword, selfSignedTLS);
		}

		// don't provide TLS context unless TLS is enabled
		SSLContext tlsCtx = tls ? tlsContext : null;

		return new RapidoidServerLoop(protocol, exchangeClass, helperClass, address, port,
			workers, bufSizeKB, noDelay, syncBufs, tlsCtx);
	}

}
