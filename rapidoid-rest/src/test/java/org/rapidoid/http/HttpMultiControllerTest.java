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
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.Since;
import org.rapidoid.setup.Apps;
import org.rapidoid.setup.On;

@Authors("Nikolche Mihajlovski")
@Since("5.0.11")
public class HttpMultiControllerTest extends IsolatedIntegrationTest {

    @Test
    public void testSequentialControllerRegistration() {

        // this will be overwritten by the third
        Apps.beans(new Object() {
            @GET
            public String foo(Req req) {
                return "FOO";
            }
        });

        Apps.beans(new Object() {
            @GET
            public String bar(Req req, Resp resp) {
                return "BAR";
            }
        });

        // this will overwrite the first
        Apps.beans(new Object() {
            @GET
            public String foo() {
                return "FOO2";
            }
        });

        On.req((ReqHandler) req -> req.verb().equals("GET") ? "generic:" + req.uri() : null);

        onlyGet("/baz?x=123");

        Apps.beans(new Object() {
            @GET
            public String baz() {
                return "BAZZZZZZ";
            }
        });

        onlyGet("/foo");
        onlyGet("/bar");
        onlyGet("/baz");
    }

}
