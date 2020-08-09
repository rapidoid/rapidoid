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
import org.rapidoid.setup.Apps;
import org.rapidoid.setup.My;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.2.0")
public class HttpWrappersTest extends IsolatedIntegrationTest {

    @Test
    public void testWrappers() {
        HttpWrapper hey = wrapper("hey");
        My.wrappers(hey);

        Apps.defaults().wrappers(wrapper("on-def"));

        On.get("/def").plain("D");

        Apps.defaults().wrappers((HttpWrapper[]) null); // reset the default wrappers

        HttpWrapper[] wrappers = Apps.custom().wrappers();
        eq(U.array(hey), wrappers);

        On.get("/").wrappers(wrapper("index")).plain("home");
        On.post("/x").wrappers(wrapper("x"), wrapper("x2")).json("X");
        On.get("/y").html("YYY");

        Apps.custom().wrappers(wrapper("on"));

        onlyGet("/");
        onlyPost("/x");
        onlyGet("/y");
        onlyGet("/def");
    }

    @Test
    public void testDefaultWrappers() {
        My.wrappers(wrapper("def"));

        On.post("/z").plain("Zzz");

        onlyPost("/z");
    }

    @Test
    public void shouldTransformRespResult() {
        My.wrappers(wrapper("wrap1"), wrapper("wrap2"));

        On.get("/x").plain("X");

        On.get("/req").serve(req -> {
            req.response().plain("FOO");
            return req;
        });

        On.get("/resp").serve((Resp resp) -> resp.plain("BAR"));

        On.get("/json").json(() -> 123);

        On.get("/html").html(req -> "<p>hello</p>");

        On.get("/null").json(req -> null);

        onlyGet("/x");
        onlyGet("/req");
        onlyGet("/resp");
        onlyGet("/json");
        onlyGet("/html");
        onlyGet("/null");
    }

    @Test
    public void shouldThrowErrorsWithNonCatchingWrappers() {
        My.wrappers(wrapper("wrap1"), wrapper("wrap2"));

        On.get("/err").plain(() -> {
            throw U.rte("Intentional error!");
        });

        onlyGet("/err");
    }

    @Test
    public void shouldTransformErrorsWithCatchingWrappers() {
        My.wrappers(wrapper("wrap1"), catchingWrapper("wrap2"));

        On.get("/err").plain(() -> {
            throw U.rte("Intentional error!");
        });

        onlyGet("/err");
    }

    private static HttpWrapper wrapper(String msg) {
        return (req, next) -> next.invokeAndTransformResult(result -> msg + "(" + req.uri() + ":" + result + ")");
    }

    private static HttpWrapper catchingWrapper(String msg) {
        return (req, next) -> next.invokeAndTransformResultCatchingErrors(result -> msg + "(" + req.uri() + ":" + result + ")");
    }

}
