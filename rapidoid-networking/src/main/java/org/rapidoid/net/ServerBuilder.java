/*-
 * #%L
 * rapidoid-networking
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.BasicConfig;
import org.rapidoid.net.impl.DefaultExchange;
import org.rapidoid.net.impl.RapidoidHelper;
import org.rapidoid.net.impl.RapidoidServerLoop;
import org.rapidoid.u.U;

import javax.net.ssl.SSLContext;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class ServerBuilder extends RapidoidThing {

	private final NetworkingParams netParams;

	private final TLSParams tlsParams = new TLSParams();

	private volatile boolean built;

	public ServerBuilder(BasicConfig cfg) {
		this.netParams = new NetworkingParams(cfg);
	}

	public ServerBuilder address(String address) {
		netParams.address(address);
		return this;
	}

	public ServerBuilder port(int port) {
		netParams.port(port);
		return this;
	}

	public ServerBuilder workers(int workers) {
		netParams.workers(workers);
		return this;
	}

	public ServerBuilder bufSizeKB(int bufSizeKB) {
		netParams.bufSizeKB(bufSizeKB);
		return this;
	}

	public ServerBuilder noDelay(boolean noDelay) {
		netParams.noDelay(noDelay);
		return this;
	}

	public ServerBuilder maxPipeline(long maxPipelineSize) {
		netParams.maxPipeline(maxPipelineSize);
		return this;
	}

	public ServerBuilder syncBufs(boolean syncBufs) {
		netParams.syncBufs(syncBufs);
		return this;
	}

	public ServerBuilder blockingAccept(boolean blockingAccept) {
		netParams.blockingAccept(blockingAccept);
		return this;
	}

	public ServerBuilder protocol(Protocol protocol) {
		netParams.protocol(protocol);
		return this;
	}

	public ServerBuilder exchangeClass(Class<? extends DefaultExchange<?>> exchangeClass) {
		netParams.exchangeClass(exchangeClass);
		return this;
	}

	public ServerBuilder helperClass(Class<? extends RapidoidHelper> helperClass) {
		netParams.helperClass(helperClass);
		return this;
	}

	public ServerBuilder tls(boolean tls) {
		tlsParams.tls(tls);
		return this;
	}

	public ServerBuilder keystore(String keystore) {
		tlsParams.keystore(keystore);
		return this;
	}

	public ServerBuilder keystorePassword(char[] keystorePassword) {
		tlsParams.keystorePassword(keystorePassword);
		return this;
	}

	public ServerBuilder keyManagerPassword(char[] keyManagerPassword) {
		tlsParams.keyManagerPassword(keyManagerPassword);
		return this;
	}

	public ServerBuilder truststore(String truststore) {
		tlsParams.truststore(truststore);
		return this;
	}

	public ServerBuilder truststorePassword(char[] truststorePassword) {
		tlsParams.truststorePassword(truststorePassword);
		return this;
	}

	public ServerBuilder selfSignedTLS(boolean selfSignedTLS) {
		tlsParams.selfSignedTLS(selfSignedTLS);
		return this;
	}

	public ServerBuilder needClientAuth(boolean needClientAuth) {
		tlsParams.needClientAuth(needClientAuth);
		return this;
	}

	public ServerBuilder wantClientAuth(boolean wantClientAuth) {
		tlsParams.wantClientAuth(wantClientAuth);
		return this;
	}

	public ServerBuilder tlsContext(SSLContext tlsContext) {
		tlsParams.tlsContext(tlsContext);
		return this;
	}

	public synchronized Server build() {
		U.must(!built, "This builder was already used! Please instantiate a new one!");
		built = true;
		return new RapidoidServerLoop(netParams, tlsParams);
	}

}
