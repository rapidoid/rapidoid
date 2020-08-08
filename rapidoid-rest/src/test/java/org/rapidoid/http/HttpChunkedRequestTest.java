/*-
 * #%L
 * rapidoid-rest
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

package org.rapidoid.http;

import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.bytes.BytesUtil;
import org.rapidoid.config.Conf;
import org.rapidoid.data.BufRange;
import org.rapidoid.data.BufRanges;
import org.rapidoid.io.IO;
import org.rapidoid.net.TCP;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.impl.FiniteStateProtocol;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Authors({"Nikolche Mihajlovski", "Jezza"})
@Since("5.5.6")
public class HttpChunkedRequestTest extends IsolatedIntegrationTest {

    private static final byte[] REQ = IO.loadBytes("HttpChunkedRequestTest/req.txt");

    private static final String RESP_BODY = "Hello";

    private static final int CONNECTIONS = 10;

    private final AtomicBoolean err = new AtomicBoolean();

    @Test
    public void testChunkedRequests() throws InterruptedException {
        Conf.NET.set("workers", 1);

        On.post("/hi").plain(() -> RESP_BODY);

        int responsesForChunkedReq = sendChunkedRequests();

        // this shows there is a bug for the (problematic) chunked requests
        eq(responsesForChunkedReq, 0);

        // this shows that the bug doesn't crash the server (the normal requests can still be processed)
        for (int i = 0; i < 10; i++) {
            Self.post("/hi").expect(RESP_BODY);
        }

        Self.post("/hi").print(); // print one successful response, just to be sure everything is OK
    }

    private int sendChunkedRequests() throws InterruptedException {
        final AtomicInteger responsesForChunkedReq = new AtomicInteger();

        CountDownLatch latch = new CountDownLatch(CONNECTIONS);

        TCP.client()
                .host("localhost")
                .port(8080)
                .reconnecting(false)
                .connections(CONNECTIONS)
                .protocol(new FiniteStateProtocol(2) {

                    @Override
                    protected int state0(Channel ctx) {
                        ctx.write(REQ);

                        latch.countDown();

                        return 1;
                    }

                    @Override
                    protected int state1(Channel ctx) {
                        expectHttpResponse(ctx, RESP_BODY.getBytes());

                        responsesForChunkedReq.incrementAndGet();

                        ctx.close();

                        return STOP;
                    }

                }).build().start();

        latch.await();

        U.sleep(5000); // wait a bit more, to make sure the server has time to process the requests

        isFalse(err.get());

        return responsesForChunkedReq.get();
    }

    private void expectHttpResponse(Channel ctx, byte[] expectedBody) {
        final BufRanges lines = ctx.helper().ranges1;
        final BufRange resp = ctx.helper().ranges2.ranges[0];

        ctx.input().scanLnLn(lines.reset()); // read lines

        ctx.input().scanN(expectedBody.length, resp); // read response body

        if (!BytesUtil.matches(ctx.input().bytes(), resp, expectedBody, true)) {
            err.set(true);
        }
    }

}
