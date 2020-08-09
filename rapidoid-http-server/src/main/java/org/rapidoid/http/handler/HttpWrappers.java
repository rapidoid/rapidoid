/*-
 * #%L
 * rapidoid-http-server
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

package org.rapidoid.http.handler;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.FastHttp;
import org.rapidoid.http.HttpUtils;
import org.rapidoid.http.HttpWrapper;
import org.rapidoid.http.impl.RouteOptions;
import org.rapidoid.u.U;

import java.util.Collections;
import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.5.1")
public class HttpWrappers extends RapidoidThing {

    static HttpWrapper[] assembleWrappers(FastHttp http, RouteOptions options) {
        List<HttpWrapper> wrappers = U.list();

        wrappers.add(new HttpAuthWrapper(options.roles()));

        Collections.addAll(wrappers, getConfiguredWrappers(http, options));

        return U.arrayOf(HttpWrapper.class, wrappers);
    }

    private static HttpWrapper[] getConfiguredWrappers(FastHttp http, RouteOptions options) {
        return U.or(
                options.wrappers(), // wrappers specific to the route
                http.custom().wrappers(), // or wrappers for the http setup
                new HttpWrapper[0] // or no wrappers
        );
    }

    static boolean shouldTransform(Object result) {
        return !HttpUtils.isSpecialResult(result);
    }

}
