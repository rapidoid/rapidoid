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
import org.rapidoid.setup.App;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeout;

@Authors("Nikolche Mihajlovski")
@Since("5.4.0")
public class HttpChunkedResponseTest extends IsolatedIntegrationTest {

    private static final int REQUESTS = Msc.normalOrHeavy(100, 10000);

    @Test
    public void testChunkedEncoding() {
        App app = new App().start();

        app.req((req, resp) -> {

            resp.chunk("ab".getBytes());
            resp.chunk("c".getBytes());
            resp.chunk("d".getBytes());

            return resp;
        });

        getReq("/");

        assertTimeout(Duration.ofSeconds(20), () -> {
            Self.get("/").expect("abcd").execute();
            Self.get("/").expect("abcd").benchmark(1, 100, REQUESTS);
            Self.post("/").expect("abcd").benchmark(1, 100, REQUESTS);
        });
    }

    @Test
    public void testChunkedEncodingAsync() {
        App app = new App().start();

        app.req((req, resp) -> {
            U.must(!req.isAsync());
            req.async();
            U.must(req.isAsync());

            async(() -> {
                resp.chunk("ab".getBytes());

                async(() -> {
                    resp.chunk("c".getBytes());

                    async(() -> {
                        resp.chunk("d".getBytes());
                        req.done();
                    });
                });
            });

            return req;
        });

        getReq("/");

        assertTimeout(Duration.ofSeconds(20), () -> {
            Self.get("/").expect("abcd").execute();
            Self.get("/").expect("abcd").benchmark(1, 100, REQUESTS);
            Self.post("/").expect("abcd").benchmark(1, 100, REQUESTS);
        });
    }

}
