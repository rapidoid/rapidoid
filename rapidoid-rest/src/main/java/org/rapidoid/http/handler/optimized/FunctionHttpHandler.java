/*-
 * #%L
 * rapidoid-rest
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
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

package org.rapidoid.http.handler.optimized;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.FastHttp;
import org.rapidoid.http.HttpRoutes;
import org.rapidoid.http.Req;
import org.rapidoid.http.handler.AbstractDecoratingHttpHandler;
import org.rapidoid.http.impl.RouteOptions;
import org.rapidoid.net.abstracts.Channel;

import java.util.function.Function;

@Authors("Attila Szabo, Istvan Szukacs")
@Since("6.0.0")
public class FunctionHttpHandler extends AbstractDecoratingHttpHandler {

    private final Function<Req,?> handler;

    public FunctionHttpHandler(FastHttp http, HttpRoutes routes, RouteOptions options, Function<Req,?> handler) {
        super(http, options);
        this.handler = handler;
    }

    @Override
    protected Object handleReq(Channel ctx, boolean isKeepAlive, Req req) throws Exception {
        return handler.apply(req);
    }

    @Override
    public String toString() {
        return contentTypeInfo("() -> ...");
    }

    @Override
    public boolean needsParams() {
        return options.managed();
    }

}
