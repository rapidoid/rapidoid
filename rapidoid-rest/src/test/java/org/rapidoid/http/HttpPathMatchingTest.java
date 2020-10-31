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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class HttpPathMatchingTest extends IsolatedIntegrationTest {

    @Test
    public void testHttpPathMatching() {
        App app = new App().start();

        app.get("/movies/{id}").json(Req::params);
        app.post("/users/{x}/").json(Req::params);
        app.put("/books/{__}/abc").json(Req::params);
        app.delete("/tags/{_f}/ref{n:\\d+}/").json(Req::params);

        onlyGet("/movies/123");
        onlyGet("/movies/1/");

        onlyPost("/users/abc-def");
        onlyPost("/users/a/");

        onlyPut("/books/12df/abc");
        onlyPut("/books/x-y-z/abc/");

        onlyDelete("/tags/comedy/ref45");
        onlyDelete("/tags/drama/ref3/");
    }

}
