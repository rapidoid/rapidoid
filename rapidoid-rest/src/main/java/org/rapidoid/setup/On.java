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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.http.ReqHandler;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.http.handler.HttpHandler;

@Authors("Nikolche Mihajlovski")
@Since("4.3.0")
public class On extends RapidoidThing {

    public static synchronized Setup setup() {
        return Setups.main();
    }

    public static synchronized OnRoute route(String verb, String path) {
        return setup().on(verb, path);
    }

    public static synchronized OnRoute any(String path) {
        return setup().any(path);
    }

    public static synchronized OnRoute get(String path) {
        return setup().get(path);
    }

    public static synchronized OnRoute post(String path) {
        return setup().post(path);
    }

    public static synchronized OnRoute put(String path) {
        return setup().put(path);
    }

    public static synchronized OnRoute delete(String path) {
        return setup().delete(path);
    }

    public static synchronized OnRoute patch(String path) {
        return setup().patch(path);
    }

    public static synchronized OnRoute options(String path) {
        return setup().options(path);
    }

    public static synchronized OnRoute head(String path) {
        return setup().head(path);
    }

    public static synchronized OnRoute trace(String path) {
        return setup().trace(path);
    }

    public static synchronized OnRoute page(String path) {
        return setup().page(path);
    }

    public static synchronized Setup req(ReqHandler handler) {
        return setup().req(handler);
    }

    public static synchronized Setup req(ReqRespHandler handler) {
        return setup().req(handler);
    }

    public static synchronized Setup req(HttpHandler handler) {
        return setup().req(handler);
    }

    public static synchronized ServerSetup port(int port) {
        return new ServerSetup(Conf.APP).port(port);
    }

    public static synchronized ServerSetup address(String address) {
        return new ServerSetup(Conf.APP).address(address);
    }

    public static synchronized OnError error(Class<? extends Throwable> error) {
        return setup().error(error);
    }

}
