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
