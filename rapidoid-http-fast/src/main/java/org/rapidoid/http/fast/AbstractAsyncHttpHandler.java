package org.rapidoid.http.fast;

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

import java.util.Map;
import java.util.concurrent.Future;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.u.U;
import org.rapidoid.util.UTILS;

@Authors("Nikolche Mihajlovski")
@Since("4.3.0")
public abstract class AbstractAsyncHttpHandler extends AbstractFastHttpHandler {

	protected final FastHttp http;

	protected final byte[] contentType;

	protected final HttpWrapper[] wrappers;

	public AbstractAsyncHttpHandler(FastHttp http, byte[] contentType, HttpWrapper[] wrappers) {
		this.http = http;
		this.contentType = contentType;
		this.wrappers = wrappers;
	}

	@Override
	public HttpStatus handle(Channel ctx, boolean isKeepAlive, Map<String, Object> params) {
		try {
			ctx.async();
			execHandlerJob(ctx, isKeepAlive, params);
		} catch (Throwable e) {
			return http.error(ctx, isKeepAlive, e);
		}

		return HttpStatus.ASYNC;
	}

	@SuppressWarnings("unchecked")
	protected Object postprocessResult(Object result) throws Exception {
		if (result instanceof Future<?>) {
			result = ((Future<Object>) result).get();
			return postprocessResult(result);
		} else if (result instanceof org.rapidoid.concurrent.Future<?>) {
			result = ((org.rapidoid.concurrent.Future<Object>) result).get();
			return postprocessResult(result);
		} else {
			// render the response and process logic while still in context
			if (!(result instanceof byte[]) && !Cls.isSimple(result) && !U.isCollection(result) && !U.isMap(result)) {
				result = U.str(result);
			}
			return result;
		}
	}

	private void execHandlerJob(final Channel ctx, final boolean isKeepAlive, final Map<String, Object> params) {
		Ctx.executeInCtx("page", params, new Runnable() {
			@Override
			public void run() {

				Object result;

				try {

					if (!U.isEmpty(wrappers)) {
						result = wrap(ctx, params, 0);
					} else {
						result = handleReq(ctx, params);
					}

					result = postprocessResult(result);
				} catch (Exception e) {
					result = e;
				}

				done(ctx, isKeepAlive, result);
			}
		});
	}

	private Object wrap(final Channel ctx, final Map<String, Object> params, final int index) throws Exception {
		HttpWrapper wrapper = wrappers[index];

		WrappedProcess process = new WrappedProcess() {
			@Override
			public Object invoke(Mapper<Object, Object> transformation) throws Exception {
				try {
					int next = index + 1;

					Object val;
					if (next < wrappers.length) {
						val = wrap(ctx, params, next);
					} else {
						val = handleReq(ctx, params);
					}

					return transformation.map(val);
				} catch (Exception e) {
					return e;
				}
			}
		};

		http.getListener().entering(wrapper, params);

		Object result = wrapper.wrap(params, process);

		http.getListener().leaving(wrapper, contentType, result);

		return result;
	}

	protected abstract Object handleReq(Channel ctx, Map<String, Object> params) throws Exception;

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
		if (result instanceof Throwable) {
			Throwable error = (Throwable) result;
			http.error(ctx, isKeepAlive, error);
		} else {
			writeResult(ctx, isKeepAlive, result);
		}

		http.done(ctx, isKeepAlive);
	}

	private void writeResult(Channel ctx, boolean isKeepAlive, Object result) {
		if (contentType.equals(FastHttp.CONTENT_TYPE_JSON)) {
			if (result instanceof byte[]) {
				http.write200(ctx, isKeepAlive, contentType, (byte[]) result);
			} else {
				http.writeSerializedJson(ctx, isKeepAlive, result);
			}
		} else {
			byte[] response = UTILS.toBytes(result);
			http.write200(ctx, isKeepAlive, contentType, response);
		}
	}

}
