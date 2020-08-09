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
import org.rapidoid.commons.RapidoidInitializer;
import org.rapidoid.config.Conf;
import org.rapidoid.config.Config;
import org.rapidoid.http.FastHttp;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.impl.HttpRoutesImpl;
import org.rapidoid.util.Msc;

@Authors("Nikolche Mihajlovski")
@Since("5.3.2")
public class DefaultSetup extends RapidoidInitializer {

    private static final String MAIN_ZONE = Msc.isPlatform() ? "platform" : "main";

    private static final Config MAIN_CFG = Msc.isPlatform() ? Conf.RAPIDOID : Conf.ON;

    final Setup main;

    DefaultSetup() {
        Customization customization = new Customization("main", My.custom(), Conf.ROOT);
        HttpRoutesImpl routes = new HttpRoutesImpl("main", customization);

        FastHttp http = new FastHttp(routes, MAIN_CFG);

        main = new SetupImpl("main", MAIN_ZONE, http, MAIN_CFG, customization, routes, true);
        Setups.register(main);

        initDefaults();
    }

    void initDefaults() {
    }

}
