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

import org.essentials4j.Do;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.rapidoid.RapidoidModules;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Arr;
import org.rapidoid.commons.Str;
import org.rapidoid.config.Conf;
import org.rapidoid.data.JSON;
import org.rapidoid.env.Env;
import org.rapidoid.job.Jobs;
import org.rapidoid.lambda.Executable;
import org.rapidoid.lambda.F3;
import org.rapidoid.lambda.Lmbd;
import org.rapidoid.net.util.NetUtil;
import org.rapidoid.setup.App;
import org.rapidoid.setup.Apps;
import org.rapidoid.setup.My;
import org.rapidoid.test.RapidoidIntegrationTest;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;

@Authors("Nikolche Mihajlovski")
@Since("5.2.5")
public abstract class IsolatedIntegrationTest extends TestCommons {

    // FIXME HEAD
    private static final List<String> HTTP_VERBS = U.list("GET", "DELETE", "OPTIONS", "TRACE", "POST", "PUT", "PATCH");

    public static final int DEFAULT_PORT = 8080;

    public static final String LOCALHOST = Msc.http() + "://localhost:8080";

    @BeforeEach
    public void openContext() {
        TimeZone.setDefault(TimeZone.getTimeZone("CET"));

        RapidoidModules.getAll(); // all modules must be present
        RapidoidIntegrationTest.before(this);

        Conf.ROOT.setPath(getTestNamespace());

        Msc.reset();
        My.reset();

//        app.setup().activate();
//        app.setup().reload();
//        App.path(getTestPackageName());
//        verifyNoRoutes(app);

        U.must(Msc.isInsideTest());

        Env.reset();

        Conf.reset();
        Conf.ROOT.setPath(getTestNamespace());

        U.must(Msc.isInsideTest());

        RapidoidIntegrationTest.start(this);
    }

    @AfterEach
    public void closeContext() {
        Jobs.shutdownNow();
        U.sleep(200);

        Apps.destroyAll();

        RapidoidIntegrationTest.after(this);
    }

    protected String localhost(String uri) {
        return localhost(DEFAULT_PORT, uri);
    }

    protected String localhost(int port, String uri) {
        return Msc.http() + "://localhost:" + port + uri;
    }

    protected String get(String uri) {
        return HTTP.get(localhost(uri)).fetch();
    }

    protected byte[] getBytes(String uri) {
        return HTTP.get(localhost(uri)).execute().bodyBytes();
    }

    protected void onlyGet(String uri) {
        onlyGet(DEFAULT_PORT, uri);
    }

    protected void onlyGet(int port, String uri) {
        onlyReq(port, "GET", uri, null, null);
    }

    protected void onlyPost(String uri) {
        onlyPost(DEFAULT_PORT, uri, null);
    }

    protected void onlyPost(String uri, Map<String, ?> data) {
        onlyPost(DEFAULT_PORT, uri, data);
    }

    protected void onlyPost(int port, String uri, Map<String, ?> data) {
        onlyReq(port, "POST", uri, data, null);
    }

    protected void onlyPost(String uri, String json) {
        onlyReq(DEFAULT_PORT, "POST", uri, null, json);
    }

    protected void onlyPut(String uri) {
        onlyPut(DEFAULT_PORT, uri, null);
    }

    protected void onlyPut(int port, String uri, Map<String, ?> data) {
        onlyReq(port, "PUT", uri, data, null);
    }

    protected void onlyPut(String uri, Map<String, ?> data) {
        onlyPut(DEFAULT_PORT, uri, data);
    }

    protected void onlyPut(String uri, String json) {
        onlyReq(DEFAULT_PORT, "PUT", uri, null, json);
    }

    protected void onlyDelete(String uri) {
        onlyDelete(DEFAULT_PORT, uri);
    }

    protected void onlyDelete(int port, String uri) {
        onlyReq(port, "DELETE", uri, null, null);
    }

    protected void getAndPost(String uri) {
        getAndPost(DEFAULT_PORT, uri);
    }

    protected void getAndPost(int port, String uri) {
        testReq(port, "GET", uri, null, null);
        testReq(port, "POST", uri, null, null);
        notFoundExcept(port, uri, "GET", "POST");
    }

    protected void getReq(String uri) {
        testReq(DEFAULT_PORT, "GET", uri, null, null);
    }

    protected void getReq(int port, String uri) {
        testReq(port, "GET", uri, null, null);
    }

    protected void postData(String uri, Map<String, ?> data) {
        testReq(DEFAULT_PORT, "POST", uri, data, null);
    }

    protected void postData(int port, String uri, Map<String, ?> data) {
        testReq(port, "POST", uri, data, null);
    }

    protected void postJson(String uri, Map<String, ?> data) {
        testReq(DEFAULT_PORT, "POST", uri, null, JSON.stringify(data));
    }

    protected void postJson(int port, String uri, Map<String, ?> data) {
        testReq(port, "POST", uri, null, JSON.stringify(data));
    }

    protected void putData(String uri, Map<String, ?> data) {
        testReq(DEFAULT_PORT, "PUT", uri, data, null);
    }

    protected void putData(int port, String uri, Map<String, ?> data) {
        testReq(port, "PUT", uri, data, null);
    }

    protected void patchData(String uri, Map<String, ?> data) {
        testReq(DEFAULT_PORT, "PATCH", uri, data, null);
    }

    protected void patchData(int port, String uri, Map<String, ?> data) {
        testReq(port, "PATCH", uri, data, null);
    }

    private void onlyReq(int port, String verb, String uri, Map<String, ?> data, String json) {
        testReq(port, verb, uri, data, json);
        notFoundExcept(port, uri, verb);
    }

    protected void deleteReq(String uri) {
        testReq(DEFAULT_PORT, "DELETE", uri, null, null);
    }

    protected void deleteReq(int port, String uri) {
        testReq(port, "DELETE", uri, null, null);
    }

    protected void notFoundExcept(String uri, String... exceptVerbs) {
        notFoundExcept(DEFAULT_PORT, uri, exceptVerbs);
    }

    protected void notFoundExcept(int port, String uri, String... exceptVerbs) {
        for (String verb : HTTP_VERBS) {
            if (Arr.indexOf(exceptVerbs, verb) < 0) {
                notFound(port, verb, uri);
            }
        }
    }

    protected void notFound(String uri) {
        notFound(DEFAULT_PORT, uri);
    }

    protected void notFound(int port, String uri) {
        notFoundExcept(port, uri);
    }

    protected void notFound(int port, String verb, String uri) {
        String resp = fetch(port, verb, uri, null, null);

        if (!resp.startsWith("HTTP/1.1 404 Not Found")) {
            eq(resp, "!!! Expected (404 Not Found) HTTP response!");
        }
    }

    protected void testReq(int port, String verb, String uri, Map<String, ?> data, String json) {
        String resp = fetch(port, verb, uri, data, json);
        String reqName = reqName(port, verb, uri);

        verifyCase(port + " " + verb + " " + uri, resp, reqName);
    }

    protected String fetch(int port, String verb, String uri, Map<String, ?> data, String json) {
        HttpReq req = HTTP.verb(HttpVerb.from(verb)).url(localhost(port, uri)).data(data).body(json != null ? json.getBytes() : null);
        return exec(req);
    }

    protected String fetch(HttpClient client, int port, String verb, String uri, Map<String, ?> data) {
        return exec(client.req().verb(HttpVerb.from(verb)).url(localhost(port, uri)).data(data));
    }

    private String exec(HttpReq req) {
        String url = req.url();

        if (url.contains("#")) {
            req.url(Str.cutToFirst(url, "#"));
        }

        req.raw(true);

        String resp = new String(req.execute().raw());
        resp = maskHttpResponse(resp);

        req.raw(false);
        return resp;
    }

    protected String maskHttpResponse(String resp) {
        resp = resp.replaceAll("(?<=\n)Date: .*? GMT(?=\r?\n)", "Date: XXXXX GMT");
        resp = resp.replaceAll("(?<=\nSet-Cookie: JSESSIONID=)[^;]+?;", "<THE-SESSION-ID>;");
        resp = resp.replaceAll("(?<=\nSet-Cookie: _token=)[^;]+?;", "<THE-TOKEN>;");
        resp = resp.replaceAll("(?<=\"token\":\")[^\"]+?\"", "<THE-TOKEN>\"");
        return resp;
    }

    protected String fetch(HttpClient client, String verb, String uri, Map<String, ?> data) {
        return fetch(client, DEFAULT_PORT, verb, uri, data);
    }

    protected String fetch(HttpClient client, String verb, String uri) {
        return fetch(client, DEFAULT_PORT, verb, uri, null);
    }

    protected String fetch(String verb, String uri) {
        return fetch(DEFAULT_PORT, verb, uri, null, null);
    }

    protected String fetch(String verb, String uri, Map<String, ?> data) {
        return fetch(DEFAULT_PORT, verb, uri, data, null);
    }

    private String reqName(int port, String verb, String uri) {
        String order = null;

        if (uri.contains("#")) {
            String[] parts = uri.split("#");
            order = parts[1];
            uri = parts[0];
        }

        String req = verb + uri.replace("/", "_").replace("?", "-");

        if (port != DEFAULT_PORT) {
            req = port + "__" + req;
        }

        if (order != null) {
            req = order + "__" + req;
        }

        return req;
    }

    protected static Map<String, Object> reqResp(Req req, Resp resp) {
        return U.map("verb", req.verb(), "uri", req.uri(), "data", req.data(), "code", resp.code());
    }

    protected String appRoutes(App app) {
        List<String> routes = Do.map(app.setup().routes().all()).toList(Object::toString);
        Collections.sort(routes);
        return U.join("\n", routes);
    }

    protected void verifyRoutes(App app) {
        verify("routes", appRoutes(app));
    }

    protected void verifyRoutes(App app, String name) {
        verify("routes-" + name, appRoutes(app));
    }

    protected void verifyNoRoutes(App app) {
        isTrue(app.setup().routes().all().isEmpty());
    }

    protected void verifyJson(String name, Object actual) {
        verifyCase(name, JSON.prettify(actual), name);
    }

    protected <T> T connect(F3<T, InputStream, BufferedReader, DataOutputStream> protocol) {
        int timeout = RAPIDOID_CI ? 30000 : 10000;
        return NetUtil.connect("localhost", 8080, timeout, protocol);
    }

    protected ScheduledFuture<Void> async(Executable executable) {
        return Jobs.after(5).milliseconds(() -> Lmbd.execute(executable));
    }

    protected App newApp() {
        return new App().start();
    }

}
