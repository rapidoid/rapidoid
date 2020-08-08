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

import org.rapidoid.annotation.*;
import org.rapidoid.commons.AnyObj;
import org.rapidoid.commons.RapidoidInitializer;
import org.rapidoid.config.Config;
import org.rapidoid.env.RapidoidEnv;
import org.rapidoid.http.FastHttp;
import org.rapidoid.http.HttpRoutes;
import org.rapidoid.http.ReqHandler;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.handler.HttpHandler;
import org.rapidoid.http.handler.optimized.DelegatingParamsAwareReqHandler;
import org.rapidoid.http.handler.optimized.DelegatingParamsAwareReqRespHandler;
import org.rapidoid.http.impl.HttpRoutesImpl;
import org.rapidoid.http.impl.RouteOptions;
import org.rapidoid.http.processor.HttpProcessor;
import org.rapidoid.lambda.NParamLambda;
import org.rapidoid.log.Log;
import org.rapidoid.net.Server;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.util.Once;

import javax.inject.Named;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.util.Map;

import static org.rapidoid.util.Constants.*;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class SetupImpl extends RapidoidInitializer implements Setup {

    private static final String DEFAULT_ADDRESS = "0.0.0.0";
    private static final int DEFAULT_PORT = Msc.isPlatform() ? 8888 : 8080;

    private static final Class<? extends Annotation>[] IOC_ANNOTATIONS = new Class[]{
            Controller.class, Service.class, Run.class, Named.class, Singleton.class
    };

    private final String name;

    private final String zone;
    private final Config serverConfig;
    private final FastHttp http;

    private final Customization customization;

    private final HttpRoutesImpl routes;
    private volatile RouteOptions defaults = new RouteOptions();

    private volatile Integer port;
    private volatile String address;

    private volatile HttpProcessor processor;
    private volatile boolean listening;
    private volatile Server server;
    private volatile boolean activated;
    private volatile boolean reloaded;
    private volatile boolean autoActivating = false;
    private volatile Runnable onInit;

    private final boolean isApp;

    private final Once bootstrappedBeans = new Once();

    SetupImpl(String name, String zone, FastHttp http,
              Config serverConfig, Customization customization,
              HttpRoutesImpl routes,
              boolean isApp) {

        this.name = name;
        this.zone = zone;
        this.http = http;

        this.serverConfig = serverConfig;
        this.customization = customization;
        this.routes = routes;
        this.isApp = isApp;

        this.defaults.zone(zone);
    }

    @Override
    public void destroy() {
        halt();
        Setups.deregister(this);
    }

    @Override
    public FastHttp http() {
        return http;
    }

    private synchronized void listen() {
        if (!listening && !reloaded) {

            listening = true;

            HttpProcessor proc = processor != null ? processor : http();

            if (server == null) {
                int targetPort = port();
                server = proc.listen(address(), targetPort);

                Log.info("!Server has started", "setup", name(), "!home", "http://localhost:" + targetPort);
                Log.info("!Static resources will be served from the following locations", "setup", name(), "!locations", custom().staticFilesPath());
            }
        }
    }

    void autoActivate() {
        if (autoActivating) activate();
    }

    @Override
    public synchronized void activate() {
        RapidoidEnv.touch();

        if (activated) {
            return;
        }
        activated = true;

        Runnable initializer = onInit;
        if (initializer != null) initializer.run();

        if (!reloaded) {
            listen();
        }
    }

    @Override
    public OnRoute on(String verb, String path) {
        return new OnRoute(this, verb.toUpperCase(), path);
    }

    @Override
    public OnRoute any(String path) {
        return on(ANY, path);
    }

    @Override
    public OnRoute get(String path) {
        return on(GET, path);
    }

    @Override
    public OnRoute post(String path) {
        return on(POST, path);
    }

    @Override
    public OnRoute put(String path) {
        return on(PUT, path);
    }

    @Override
    public OnRoute delete(String path) {
        return on(DELETE, path);
    }

    @Override
    public OnRoute patch(String path) {
        return on(PATCH, path);
    }

    @Override
    public OnRoute options(String path) {
        return on(OPTIONS, path);
    }

    @Override
    public OnRoute head(String path) {
        return on(HEAD, path);
    }

    @Override
    public OnRoute trace(String path) {
        return on(TRACE, path);
    }

    @Override
    public OnRoute page(String path) {
        return on(GET_OR_POST, path);
    }

    @Override
    public Setup req(ReqHandler handler) {
        routes.addGenericHandler(new DelegatingParamsAwareReqHandler(http(), routes, opts(), handler));
        autoActivate();
        return this;
    }

    @Override
    public Setup req(ReqRespHandler handler) {
        routes.addGenericHandler(new DelegatingParamsAwareReqRespHandler(http(), routes, opts(), handler));
        autoActivate();
        return this;
    }

    @Override
    public Setup req(HttpHandler handler) {
        routes.addGenericHandler(handler);
        autoActivate();
        return this;
    }

    @Override
    public Setup beans(Object... beans) {
        RapidoidEnv.touch();
        beans = AnyObj.flat(beans);

        for (Object bean : beans) {
            U.notNull(bean, "bean");

            if (bean instanceof NParamLambda) {
                throw U.rte("Expected a bean, but found lambda: " + bean);
            }
        }

        PojoHandlersSetup.from(this, beans).register();

        return this;
    }

    @Override
    public Setup port(int port) {
        this.port = port;
        return this;
    }

    @Override
    public Setup address(String address) {
        this.address = address;
        return this;
    }

    @Override
    public Setup processor(HttpProcessor processor) {
        U.must(!listening, "The server was already initialized!");
        this.processor = processor;
        return this;
    }

    @Override
    public synchronized Setup shutdown() {
        if (this.server != null) {
            if (this.server.isActive()) {
                this.server.shutdown();
            }
            this.server = null;
        }

        reset();

        return this;
    }

    @Override
    public synchronized Setup halt() {
        if (this.server != null) {
            if (this.server.isActive()) {
                this.server.halt();
            }
            this.server = null;
        }

        reset();

        return this;
    }

    @Override
    public void reset() {
        http().resetConfig();
        listening = false;
        reloaded = false;
        port = null;
        address = null;
        processor = null;
        activated = false;
        autoActivating = false;

        defaults = new RouteOptions();
        defaults().zone(zone);

        bootstrappedBeans.reset();

        Setups.initDefaults();
    }

    @Override
    public Server server() {
        return server;
    }

    @Override
    public Map<String, Object> attributes() {
        return http().attributes();
    }

    @Override
    public Setup deregister(String verb, String path) {
        routes.remove(verb, path);
        return this;
    }

    @Override
    public Setup deregister(Object... controllers) {
        PojoHandlersSetup.from(this, controllers).deregister();
        return this;
    }

    @Override
    public void reload() {
        reloaded = true;
        bootstrappedBeans.reset();
        http().resetConfig();
        defaults = new RouteOptions();
        defaults.zone(zone);
        attributes().clear();
    }

    @Override
    public Config config() {
        return serverConfig;
    }

    @Override
    public Customization custom() {
        return customization;
    }

    @Override
    public HttpRoutes routes() {
        return routes;
    }

    private RouteOptions opts() {
        return new RouteOptions();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public RouteOptions defaults() {
        return defaults;
    }

    @Override
    public String zone() {
        return zone;
    }

    @Override
    public boolean isRunning() {
        return activated;
    }

    @Override
    public int port() {
        if (port == null) {
            port = serverConfig.entry("port").or(DEFAULT_PORT);
        }

        U.must(port >= 0, "The port of server setup '%s' is negative!", name());

        return port;
    }

    @Override
    public String address() {
        if (address == null) {
            address = serverConfig.entry("address").or(DEFAULT_ADDRESS);
        }

        U.must(U.notEmpty(address), "The address of server setup '%s' is empty!", name());

        return address;
    }

    @Override
    public OnError error(Class<? extends Throwable> error) {
        return new OnError(customization, error);
    }

    @Override
    public String toString() {
        return "Setup{" +
                "name='" + name + '\'' +
                ", zone='" + zone + '\'' +
                ", serverConfig=" + serverConfig +
                ", customization=" + customization +
                ", routes=" + routes +
                '}';
    }

    @Override
    public void onInit(Runnable onInit) {
        this.onInit = onInit;
    }

    @Override
    public boolean autoActivating() {
        return autoActivating;
    }

    @Override
    public Setup autoActivating(boolean autoActivating) {
        this.autoActivating = autoActivating;
        return this;
    }

    private boolean isApp() {
        return isApp;
    }

}
