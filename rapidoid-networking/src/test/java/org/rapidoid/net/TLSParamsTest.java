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
        } catch(Exception e) {
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
