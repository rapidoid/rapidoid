/*-
 * #%L
 * rapidoid-http-server
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

package org.rapidoid.http.handler;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ctx.With;
import org.rapidoid.http.*;
import org.rapidoid.http.impl.RouteOptions;
import org.rapidoid.http.impl.lowlevel.HttpIO;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.log.LogLevel;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.u.U;
import org.rapidoid.util.LazyInit;

@Authors("Nikolche Mihajlovski")
@Since("5.5.1")
public class HttpManagedHandlerDecorator extends AbstractHttpHandlerDecorator {

    private static final String CTX_TAG_HANDLER = "handler";

    private final RouteOptions options;

    private final LazyInit<HttpWrapper[]> wrappers;

    HttpManagedHandlerDecorator(AbstractDecoratingHttpHandler handler, final FastHttp http, final RouteOptions options) {
        super(handler, http);

        this.options = options;
        this.wrappers = new LazyInit<>(() -> HttpWrappers.assembleWrappers(http, options));
    }

    @Override
    public final HttpStatus handle(Channel ctx, boolean isKeepAlive, Req req) {
        if (!ctx.isAsync()) {
            // first checks if not async, to avoid exceptions when running the second time from non-IO thread
            ctx.async();
        }

        execHandlerJob(ctx, isKeepAlive, options.contentType(), req);

        return HttpStatus.ASYNC;
    }

    private void execHandlerJob(final Channel channel, final boolean isKeepAlive, final MediaType contentType, final Req req) {

        With.tag(CTX_TAG_HANDLER).exchange(req).run(() -> {
            try {
                req.response().contentType(options.contentType());

                handleWithWrappers(channel, isKeepAlive, contentType, req, wrappers.get());

            } catch (Throwable e) {
                handleError(req, e);
            }
        });
    }

    private void handleWithWrappers(Channel channel, boolean isKeepAlive, MediaType contentType,
                                    Req req, HttpWrapper[] wrappers) {
        Object result;

        try {

            if (!U.isEmpty(wrappers)) {
                result = wrap(channel, isKeepAlive, req, 0, wrappers);
            } else {
                result = handleReqAndPostProcess(channel, isKeepAlive, req);
            }

        } catch (Throwable e) {
            result = e;
        }

        complete(channel, isKeepAlive, contentType, req, result);
    }

    private Object wrap(final Channel channel, final boolean isKeepAlive, final Req req, final int index,
                        final HttpWrapper[] wrappers) throws Exception {

        HttpWrapper wrapper = wrappers[index];

        HandlerInvocation invocation = new HandlerInvocation() {

            @Override
            public Object invoke() throws Exception {
                return invokeNext();
            }

            @Override
            public Object invokeAndTransformResult(Mapper<Object, Object> transformation) throws Exception {
                Object result = invokeNext();

                if (result instanceof Throwable) {
                    return result;

                } else {
                    return transform(transformation, result);
                }
            }

            @Override
            public Object invokeAndTransformResultCatchingErrors(Mapper<Object, Object> transformation) {
                Object resultOrError;

                try {
                    resultOrError = invokeNext();

                } catch (Throwable e) {
                    resultOrError = e;
                }

                try {
                    return transform(transformation, resultOrError);
                } catch (Throwable e) {
                    return e;
                }
            }

            private Object invokeNext() throws Exception {
                int next = index + 1;

                Object result;
                if (next < wrappers.length) {
                    result = wrap(channel, isKeepAlive, req, next, wrappers);
                } else {
                    result = handleReqAndPostProcess(channel, isKeepAlive, req);
                }

                return result;
            }

            private Object transform(Mapper<Object, Object> transformation, Object resultOrError) throws Exception {
                U.notNull(transformation, "transformation");

                if (HttpWrappers.shouldTransform(resultOrError)) {
                    return transformation.map(resultOrError);

                } else {
                    Resp resp = req.response();

                    if (HttpWrappers.shouldTransform(resp.result())) {
                        resp.result(transformation.map(resp.result()));
                    }

                    return resultOrError;
                }
            }
        };

        return wrapper.wrap(req, invocation);
    }

    private void handleError(Req req, Throwable e) {
        req.revert();
        req.async();

        HttpIO.INSTANCE.error(req, e, LogLevel.ERROR);

        // the Req object will do the rendering
        req.done();
    }

    private void complete(Channel ctx, boolean isKeepAlive, MediaType contentType, Req req, Object result) {

        U.must(result != null, "The post-processed result cannot be null!");
        U.must(!(result instanceof Req), "The post-processed result cannot be a Req instance!");
        U.must(!(result instanceof Resp), "The post-processed result cannot be a Resp instance!");

        if (result instanceof Throwable) {
            handleError(req, (Throwable) result);
            return;
        }

        if (result == HttpStatus.ERROR) {
            complete(ctx, isKeepAlive, contentType, req, U.rte("Handler error!"));
            return;
        }

        if (result == HttpStatus.NOT_FOUND) {
            http.notFound(ctx, isKeepAlive, contentType, handler, req);
            return;
        }

        if (result == HttpStatus.ASYNC) {
            return;
        }

        processNormalResult(req, result);
    }

    private void processNormalResult(Req req, Object result) {

        HttpUtils.resultToResponse(req, result);

        // the Req object will do the rendering
        if (!req.isAsync()) {
            req.done();
        }
    }

}
