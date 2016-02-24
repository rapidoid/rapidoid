package org.rapidoid.http.handler;

/*
 * #%L
 * rapidoid-http-fast
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Err;
import org.rapidoid.commons.MediaType;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.ctx.With;
import org.rapidoid.http.*;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.u.U;

import java.util.concurrent.Future;

@Authors("Nikolche Mihajlovski")
@Since("4.3.0")
public abstract class AbstractAsyncHttpHandler extends AbstractFastHttpHandler {

	private static final String CTX_TAG_HANDLER = "handler";

	protected final HttpWrapper[] wrappers;

	public AbstractAsyncHttpHandler(FastHttp http, MediaType contentType, HttpWrapper[] wrappers) {
		super(http, contentType);
		this.wrappers = wrappers;
	}

	@Override
	public HttpStatus handle(Channel ctx, boolean isKeepAlive, Req req, Object extra) {
		try {
			ctx.async();
			execHandlerJob(ctx, isKeepAlive, req, extra);

		} catch (Throwable e) {
			// if there was an error in the job scheduling:
			http.error(ctx, isKeepAlive, req, e);
			return HttpStatus.ERROR;
		}

		return HttpStatus.ASYNC;
	}

	@SuppressWarnings("unchecked")
	protected Object postprocessResult(Req req, Object result) throws Exception {
		if (result instanceof Req || result instanceof Resp || result instanceof HttpStatus) {
			return result;

		} else if (result == null) {
			return result; // not found

		} else if (result instanceof Future<?>) {
			result = ((Future<Object>) result).get();
			return postprocessResult(req, result);

		} else if (result instanceof org.rapidoid.concurrent.Future<?>) {
			result = ((org.rapidoid.concurrent.Future<Object>) result).get();
			return postprocessResult(req, result);

		} else {
			// render the response and process logic while still in context
			if (!(result instanceof byte[]) && !(result instanceof ByteBuffer)
					&& !(result instanceof File) && !(result instanceof Res)
					&& !Cls.isSimple(result) && !Coll.isCollection(result) && !Coll.isMap(result)) {
				result = render(result);
			}

			return result;
		}
	}

	private String render(Object result) {
		// rendering a Widget requires double toString:
		U.str(result); // 1. data binding and event processing
		return U.str(result); // 2. actual rendering
	}

	private void execHandlerJob(final Channel channel, final boolean isKeepAlive, final Req req, final Object extra) {
		With.tag(CTX_TAG_HANDLER).run(new Runnable() {

			@Override
			public void run() {
				Ctx ctx = Ctxs.ctx();
				ctx.setExchange(req);
				ctx.setUser(null); // FIXME set user

				Object result;
				try {

					if (!U.isEmpty(wrappers)) {
						result = wrap(channel, isKeepAlive, req, 0, extra);
					} else {
						result = handleReq(channel, isKeepAlive, req, extra);
					}

					result = postprocessResult(req, result);
				} catch (Throwable e) {
					result = e;
				}

				complete(channel, isKeepAlive, req, result);
			}
		});
	}

	private Object wrap(final Channel channel, final boolean isKeepAlive, final Req req, final int index, final Object extra)
			throws Exception {
		HttpWrapper wrapper = wrappers[index];

		HandlerInvocation invocation = new HandlerInvocation() {

			@Override
			public Object invoke() throws Exception {
				return invokeAndTransformResult(null);
			}

			@Override
			public Object invokeAndTransformResult(Mapper<Object, Object> transformation) throws Exception {
				try {
					int next = index + 1;

					Object val;
					if (next < wrappers.length) {
						val = wrap(channel, isKeepAlive, req, next, extra);
					} else {
						val = handleReq(channel, isKeepAlive, req, extra);
					}

					return transformation != null ? transformation.map(val) : val;

				} catch (Throwable e) {
					return e;
				}
			}
		};

		Object result = wrapper.wrap(req, invocation);

		return result;
	}

	protected abstract Object handleReq(Channel ctx, boolean isKeepAlive, Req req, Object extra) throws Exception;

	public void complete(Channel ctx, boolean isKeepAlive, Req req, Object result) {

		if (result == null) {
			http.notFound(ctx, isKeepAlive, this, req);
			return; // not found
		}

		if (result instanceof Throwable) {
			Throwable error = (Throwable) result;
			http.error(ctx, isKeepAlive, req, error);
			return;
		}

		if (result instanceof HttpStatus) {
			http.error(ctx, isKeepAlive, req, Err.notExpected());
			return;
		}

		if (result instanceof Req) {
			U.must(req == result);

		} else if (result instanceof Resp) {
			U.must(req.response() == result);

		} else {
			req.response().content(result);
		}

		// the Req object will do the rendering
		if (!req.isAsync()) {
			req.done();
		}
	}

}
