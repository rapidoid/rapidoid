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

package org.rapidoid.setup;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Arr;
import org.rapidoid.commons.RapidoidInitializer;
import org.rapidoid.config.Conf;
import org.rapidoid.config.Config;
import org.rapidoid.env.Env;
import org.rapidoid.http.HttpRoutes;
import org.rapidoid.http.ReqHandler;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.handler.HttpHandler;
import org.rapidoid.http.impl.RouteOptions;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class App extends RapidoidInitializer {

    private final ServerSetup serverSetup = new ServerSetup(Conf.APP);

    private final Setup setup = Setups.create("app");

    private volatile AppStatus status = AppStatus.NOT_STARTED;

    /**
     * Initializes the app in atomic way.
     * Won't serve requests until App.ready() is called.
     */
    public App() {
        this(new String[0]);
    }

    /**
     * Initializes the app in atomic way.
     * Won't serve requests until App.ready() is called.
     */
    public App(String[] args, String... extraArgs) {
//        AppStarter.startUp(args, extraArgs);

        args = Arr.concat(extraArgs, args);

//        Env.setArgs(args); // FIXME should not be global!
//        U.must(!Conf.isInitialized(), "The configuration shouldn't be initialized yet!");  // FIXME should not be global!

        status = AppStatus.INITIALIZING;

        Apps.boot();
    }

    public synchronized void profiles(String... profiles) {
        Env.setProfiles(profiles);
        Conf.reset();
    }

    public App beans(Object... beans) {
        setup.beans(beans);
        return this;
    }

    public synchronized void shutdown() {
        status = AppStatus.STOPPING;

        setup.shutdown();

        status = AppStatus.STOPPED;
    }

    /**
     * Completes the initialization and starts the application.
     */
    public synchronized App start() {
        U.must(status == AppStatus.INITIALIZING, "The application is not initializing!");

        setup.activate();

        status = AppStatus.RUNNING;

        Log.info("!Ready.");

        return this;
    }

    public AppStatus status() {
        return status;
    }

    public Setup setup() {
        return setup;
    }

    public Config config() {
        return setup.config();
    }

    public Customization custom() {
        return setup.custom();
    }

    public HttpRoutes routes() {
        return setup.routes();
    }

    public RouteOptions defaults() {
        return setup.defaults();
    }

    public OnRoute route(String verb, String path) {
        return setup.on(verb, path);
    }

    public OnRoute any(String path) {
        return setup.any(path);
    }

    public OnRoute get(String path) {
        return setup.get(path);
    }

    public OnRoute post(String path) {
        return setup.post(path);
    }

    public OnRoute put(String path) {
        return setup.put(path);
    }

    public OnRoute delete(String path) {
        return setup.delete(path);
    }

    public OnRoute patch(String path) {
        return setup.patch(path);
    }

    public OnRoute options(String path) {
        return setup.options(path);
    }

    public OnRoute head(String path) {
        return setup.head(path);
    }

    public OnRoute trace(String path) {
        return setup.trace(path);
    }

    public OnRoute page(String path) {
        return setup.page(path);
    }

    public App req(ReqHandler handler) {
        setup.req(handler);
        return this;
    }

    public App req(ReqRespHandler handler) {
        setup.req(handler);
        return this;
    }

    public App req(HttpHandler handler) {
        setup.req(handler);
        return this;
    }

    public App port(int port) {
        serverSetup.port(port);
        return this;
    }

    public App address(String address) {
        serverSetup.address(address);
        return this;
    }

    public OnError error(Class<? extends Throwable> error) {
        return setup.error(error);
    }

}
