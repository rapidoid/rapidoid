/*-
 * #%L
 * rapidoid-http-client
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

package org.rapidoid.http.client;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.data.BufRange;
import org.rapidoid.data.BufRanges;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.impl.FiniteStateProtocol;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.5.0")
public class HttpClientProtocol extends FiniteStateProtocol {

    private final String request;

    private final HttpClientCallback callback;

    public HttpClientProtocol(String request, HttpClientCallback callback) {
        super(2);
        this.request = request;
        this.callback = callback;
    }

    /*
     * Send request.
     */
    @Override
    protected int state0(Channel ctx) {
        ctx.log("sending request");

        ctx.write(request.getBytes());

        return 1;
    }

    /*
     * Receive response.
     */
    @Override
    protected int state1(Channel ctx) {
        ctx.log("receiving response");

        final BufRanges head = ctx.helper().ranges1.reset();
        final BufRanges body = ctx.helper().ranges2.reset();

        Buf in = ctx.input();
        in.scanLnLn(head);

        Map<String, String> headers = head.toMap(in.bytes(), 1, head.count - 1, "\\s*\\:\\s*");
        Map<String, String> headersLow = Msc.lowercase(headers);

        if ("chunked".equals(headersLow.get("transfer-encoding"))) {

            ctx.log("got chunked encoding");
            parseChunkedBody(in, body);
            callback.onResult(in, head, body);

        } else if (headersLow.containsKey("content-length")) {

            ctx.log("got content length");
            int clength = Integer.parseInt(headersLow.get("content-length"));

            parseBodyByContentLength(in, body, clength);
            callback.onResult(in, head, body);

        } else {
            // no content length is provided, read until connection is closed
            ctx.log("read until closed");

            readBodyUntilClosed(ctx, body);
            callback.onResult(in, head, body);
        }

        ctx.log("done");
        ctx.close(); // improve: keep-alive
        return STOP;
    }

    private void parseChunkedBody(Buf in, BufRanges chunks) {
        int count;
        do {
            String cnt = in.readLn();
            count = Integer.parseInt(cnt, 16);

            if (count > 0) {
                BufRange chunk = chunks.ranges[chunks.add()];
                in.scanN(count, chunk);

                // each chunk is terminated with a new line
                String line = in.readLn();
                U.must(line.isEmpty());
            }

        } while (count > 0);
    }

    private void parseBodyByContentLength(Buf in, BufRanges body, int clength) {
        body.add(); // there will be only 1 body part
        in.scanN(clength, body.ranges[0]);
    }

    private void readBodyUntilClosed(Channel ctx, BufRanges body) {
        ctx.waitUntilClosing();

        body.add(); // there will be only 1 body part
        Buf in = ctx.input();
        in.scanN(in.remaining(), body.ranges[0]);
    }

}
