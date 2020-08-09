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

import org.junit.jupiter.api.Test;
import org.rapidoid.NetTestCommons;
import org.rapidoid.config.Conf;

public class TLSParamsTest extends NetTestCommons {
    @Test
    public void testInitialization() {
        Conf.TLS.set("needClientAuth", true);
        Conf.TLS.set("wantClientAuth", true);

        boolean errorCaught = false;

        try {
            new TLSParams();
        } catch (Exception e) {
            errorCaught = true;
        }
        isTrue(errorCaught);
    }

    @Test
    public void testSetter() {
        TLSParams tlsParams = new TLSParams();

        tlsParams.wantClientAuth(true);
        isTrue(tlsParams.wantClientAuth());
        isFalse(tlsParams.needClientAuth());

        tlsParams.needClientAuth(true);
        isTrue(tlsParams.needClientAuth());
        isFalse(tlsParams.wantClientAuth());

        tlsParams.wantClientAuth(true);
        isTrue(tlsParams.wantClientAuth());
        isFalse(tlsParams.needClientAuth());
    }
}
