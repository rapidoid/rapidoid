/*-
 * #%L
 * rapidoid-networking
 * %%
 * Copyright (C) 2014 - 2020 Nikolche Mihajlovski and contributors
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
import org.rapidoid.net.impl.RapidoidClientLoop;
import org.rapidoid.net.impl.RapidoidHelper;
import org.rapidoid.u.U;

import javax.net.ssl.SSLContext;

@Authors("Nikolche Mihajlovski")
@Since("5.5.0")
public class TCPClientBuilder extends RapidoidThing {

    private volatile int connections;

    private volatile boolean reconnecting = true;

    private final NetworkingParams netParams;

    private final TLSParams tlsParams = new TLSParams();

    private volatile boolean built;

    public TCPClientBuilder(BasicConfig cfg) {
        this.netParams = new NetworkingParams(cfg);
    }

    public TCPClientBuilder host(String host) {
        netParams.address(host);
        return this;
    }

    public TCPClientBuilder port(int port) {
        netParams.port(port);
        return this;
    }

    public TCPClientBuilder workers(int workers) {
        netParams.workers(workers);
        return this;
    }

    public TCPClientBuilder bufSizeKB(int bufSizeKB) {
        netParams.bufSizeKB(bufSizeKB);
        return this;
    }

    public TCPClientBuilder noDelay(boolean noDelay) {
        netParams.noDelay(noDelay);
        return this;
    }

    public TCPClientBuilder maxPipeline(long maxPipelineSize) {
        netParams.maxPipeline(maxPipelineSize);
        return this;
    }

    public TCPClientBuilder syncBufs(boolean syncBufs) {
        netParams.syncBufs(syncBufs);
        return this;
    }

    public TCPClientBuilder blockingAccept(boolean blockingAccept) {
        netParams.blockingAccept(blockingAccept);
        return this;
    }

    public TCPClientBuilder protocol(Protocol protocol) {
        netParams.protocol(protocol);
        return this;
    }

    public TCPClientBuilder exchangeClass(Class<? extends DefaultExchange<?>> exchangeClass) {
        netParams.exchangeClass(exchangeClass);
        return this;
    }

    public TCPClientBuilder helperClass(Class<? extends RapidoidHelper> helperClass) {
        netParams.helperClass(helperClass);
        return this;
    }

    public TCPClientBuilder tls(boolean tls) {
        tlsParams.tls(tls);
        return this;
    }

    public TCPClientBuilder keystore(String keystore) {
        tlsParams.keystore(keystore);
        return this;
    }

    public TCPClientBuilder keystorePassword(char[] keystorePassword) {
        tlsParams.keystorePassword(keystorePassword);
        return this;
    }

    public TCPClientBuilder keyManagerPassword(char[] keyManagerPassword) {
        tlsParams.keyManagerPassword(keyManagerPassword);
        return this;
    }

    public TCPClientBuilder truststore(String truststore) {
        tlsParams.truststore(truststore);
        return this;
    }

    public TCPClientBuilder truststorePassword(char[] truststorePassword) {
        tlsParams.truststorePassword(truststorePassword);
        return this;
    }

    public TCPClientBuilder selfSignedTLS(boolean selfSignedTLS) {
        tlsParams.selfSignedTLS(selfSignedTLS);
        return this;
    }

    public TCPClientBuilder tlsContext(SSLContext tlsContext) {
        tlsParams.tlsContext(tlsContext);
        return this;
    }

    public TCPClientBuilder connections(int connections) {
        this.connections = connections;
        return this;
    }

    public TCPClientBuilder reconnecting(boolean reconnecting) {
        this.reconnecting = reconnecting;
        return this;
    }

    public synchronized TCPClient build() {
        U.must(!built, "This builder was already used! Please instantiate a new one!");
        built = true;
        return new RapidoidClientLoop(netParams, reconnecting, connections, tlsParams);
    }

}
