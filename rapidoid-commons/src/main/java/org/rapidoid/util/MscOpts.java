/*-
 * #%L
 * rapidoid-commons
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

package org.rapidoid.util;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.config.Conf;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class MscOpts extends RapidoidThing {

    private static final boolean hasDockerEnv = U.eq(System.getenv("RAPIDOID_JAR"), "/opt/rapidoid.jar")
            && U.eq(System.getenv("RAPIDOID_TMP"), "/tmp/rapidoid")
            && U.notEmpty(System.getenv("RAPIDOID_VERSION"));

    private static final boolean hasLogback = Cls.exists("ch.qos.logback.classic.Logger");

    public static boolean hasDockerEnv() {
        return hasDockerEnv;
    }

    public static boolean hasLogback() {
        return hasLogback;
    }

    public static boolean isTLSEnabled() {
        return Conf.TLS.is("enabled");
    }
}
