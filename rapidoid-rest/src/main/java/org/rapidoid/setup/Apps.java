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

import org.rapidoid.RapidoidModule;
import org.rapidoid.RapidoidModules;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.RapidoidInitializer;
import org.rapidoid.config.Conf;
import org.rapidoid.config.Config;
import org.rapidoid.env.Env;
import org.rapidoid.http.HttpRoutes;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.impl.RouteOptions;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.util.Once;

@Authors("Nikolche Mihajlovski")
@Since("6.0.0")
public class Apps extends RapidoidInitializer {

    private static volatile AppStatus status = AppStatus.NOT_STARTED;

    private static final Once boot = new Once();

    /**
     * Initializes the app in atomic way.
     * Won't serve requests until App.ready() is called.
     */
    public static synchronized void init(String[] args, String... extraArgs) {
        AppStarter.startUp(args, extraArgs);

        status = AppStatus.INITIALIZING;

        boot();
    }

    public synchronized static void boot() {
        if (boot.go()) {
            for (RapidoidModule module : RapidoidModules.getAll()) {
                module.boot();
            }
        }
    }

    public static synchronized void profiles(String... profiles) {
        Env.setProfiles(profiles);
        Conf.reset();
    }

    public static void beans(Object... beans) {
        setup().beans(beans);
    }

    public static synchronized void shutdown() {
        status = AppStatus.STOPPING;

        Setups.shutdownAll();

        status = AppStatus.STOPPED;
    }

    /**
     * Completes the initialization and starts the application.
     */
    public static synchronized void ready() {
        U.must(status == AppStatus.INITIALIZING, "App.init() must be called before App.ready()!");

        onAppReady();
    }

    private static void onAppReady() {
        status = AppStatus.RUNNING;
//		IoC.ready();
        Setups.ready();
        Log.info("!Ready.");
    }

    public static AppStatus status() {
        return status;
    }

    public static Setup setup() {
        return Setups.main();
    }

    public static Config config() {
        return On.setup().config();
    }

    public static Customization custom() {
        return On.setup().custom();
    }

    public static HttpRoutes routes() {
        return On.setup().routes();
    }

    public static RouteOptions defaults() {
        return On.setup().defaults();
    }
}
