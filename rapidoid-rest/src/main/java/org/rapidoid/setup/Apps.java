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
import org.rapidoid.http.Self;
import org.rapidoid.job.Jobs;
import org.rapidoid.u.U;
import org.rapidoid.util.Once;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Authors("Nikolche Mihajlovski")
@Since("6.0.0")
public class Apps extends RapidoidInitializer {

    private static final Once boot = new Once();

    public synchronized static void boot() {
        if (boot.go()) {
            for (RapidoidModule module : RapidoidModules.getAll()) {
                module.boot();
            }
        }
    }

    public static synchronized void destroyAll() {
        Setups.destroyAll();
    }

}
