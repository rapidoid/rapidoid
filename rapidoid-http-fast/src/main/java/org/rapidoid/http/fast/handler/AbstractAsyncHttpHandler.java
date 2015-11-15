package org.rapidoid.http.fast.handler;

/*
 * #%L
 * rapidoid-http-fast
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import java.util.concurrent.Future;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.http.fast.FastHttp;
import org.rapidoid.http.fast.HttpStatus;
import org.rapidoid.http.fast.HttpWrapper;
import org.rapidoid.http.fast.Req;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.mime.MediaType;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("4.3.0")
public abstract class AbstractAsyncHttpHandler extends AbstractFastHttpHandler {

	protected final HttpWrapper[] wrappers;

	public AbstractAsyncHttpHandler(FastHttp http, MediaType contentType, HttpWrapper[] wrappers) {
		super(http, contentType);
		this.wrappers = wrappers;
	}

	@Override
	public HttpStatus handle(Channel ctx, boolean isKeepAlive, Req req) {
		try {
			ctx.async();
			execHandlerJob(ctx, isKeepAlive, req);
		} catch (Throwable e) {
			return http.error(ctx, isKeepAlive, e);
		}

		return HttpStatus.ASYNC;
	}

	@SuppressWarnings("unchecked")
	protected Object postprocessResult(Object result) throws Exception {
		if (result instanceof Req) {
			return result; // the Req object will do the rendering

		} else if (result instanceof Future<?>) {
			result = ((Future<Object>) result).get();
			return postprocessResult(result);

		} else if (result instanceof org.rapidoid.concurrent.Future<?>) {
			result = ((org.rapidoid.concurrent.Future<Object>) result).get();
			return postprocessResult(result);

		} else {
			// render the response and process logic while still in context
			if (!(result instanceof byte[]) && !Cls.isSimple(result) && !U.isCollection(result) && !U.isMap(result)) {
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

	private void execHandlerJob(final Channel channel, final boolean isKeepAlive, final Req req) {
		Ctx.executeInCtx("handler", new Runnable() {
			@Override
			public void run() {

				Ctx ctx = Ctxs.get();
				ctx.setApp(null);
				ctx.setExchange(req);
				ctx.setUser(null);

				Object result;

				try {

					if (!U.isEmpty(wrappers)) {
						result = wrap(channel, req, 0);
					} else {
						result = handleReq(channel, req);
					}

					result = postprocessResult(result);
				} catch (Exception e) {
					result = e;
				}

				done(channel, isKeepAlive, result);
			}
		});
	}

	private Object wrap(final Channel channel, final Req req, final int index) throws Exception {
		HttpWrapper wrapper = wrappers[index];

		WrappedProcess process = new WrappedProcess() {
			@Override
			public Object invoke(Mapper<Object, Object> transformation) throws Exception {
				try {
					int next = index + 1;

					Object val;
					if (next < wrappers.length) {
						val = wrap(channel, req, next);
					} else {
						val = handleReq(channel, req);
					}

					return transformation.map(val);
				} catch (Exception e) {
					return e;
				}
			}
		};

		http.getListener().entering(wrapper, req);

		Object result = wrapper.wrap(req, process);

		http.getListener().leaving(wrapper, contentType, result);

		return result;
	}

	protected abstract Object handleReq(Channel ctx, Req req) throws Exception;

	protected Callback<Object> callback(final Channel ctx, final boolean isKeepAlive) {
		return new Callback<Object>() {
			@Override
			public void onDone(Object result, Throwable error) throws Exception {
				Object resultOrError = U.or(result, error);
				done(ctx, isKeepAlive, resultOrError);
			}
		};
	}

	public void done(Channel ctx, boolean isKeepAlive, Object result) {
		if (result instanceof Req) {
			return; // the Req instance will render the result

		} else if (result instanceof Throwable) {
			Throwable error = (Throwable) result;
			http.error(ctx, isKeepAlive, error);

		} else {
			http.writeResult(ctx, isKeepAlive, result, contentType);
		}

		http.done(ctx, isKeepAlive);
	}

}
