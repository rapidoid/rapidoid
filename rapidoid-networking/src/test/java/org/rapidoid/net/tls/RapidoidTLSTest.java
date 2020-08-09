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

package org.rapidoid.net.tls;


import org.junit.jupiter.api.Test;
import org.rapidoid.NetTestCommons;
import org.rapidoid.net.TLSParams;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class RapidoidTLSTest extends NetTestCommons {
    @Test
    public void testNeedClientAuth() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext context = SSLContext.getDefault();

        TLSParams tlsParams = new TLSParams();
        tlsParams.needClientAuth(true);

        RapidoidTLS tls = new RapidoidTLS(context, null, tlsParams);

        isTrue(tls.engine().getNeedClientAuth());
        isFalse(tls.engine().getWantClientAuth());
    }

    @Test
    public void testWantClientAuth() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext context = SSLContext.getDefault();

        TLSParams tlsParams = new TLSParams();
        tlsParams.wantClientAuth(true);

        RapidoidTLS tls = new RapidoidTLS(context, null, tlsParams);

        isTrue(tls.engine().getWantClientAuth());
        isFalse(tls.engine().getNeedClientAuth());
    }

    @Test
    public void testNoClientAuth() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext context = SSLContext.getDefault();

        TLSParams tlsParams = new TLSParams();

        RapidoidTLS tls = new RapidoidTLS(context, null, tlsParams);

        isFalse(tls.engine().getNeedClientAuth());
        isFalse(tls.engine().getWantClientAuth());
    }
}
