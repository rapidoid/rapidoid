package org.rapidoid.http.handler;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.annotation.TransactionMode;
import org.rapidoid.ctx.With;
import org.rapidoid.http.*;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.impl.MaybeReq;
import org.rapidoid.http.impl.ReqImpl;
import org.rapidoid.http.impl.RouteOptions;
import org.rapidoid.http.impl.lowlevel.HttpIO;
import org.rapidoid.jpa.JPA;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.log.Log;
import org.rapidoid.log.LogLevel;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.security.Secure;
import org.rapidoid.u.U;
import org.rapidoid.util.TokenAuthData;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/*
 * #%L
 * rapidoid-http-fast
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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

@Authors("Nikolche Mihajlovski")
@Since("4.3.0")
public abstract class AbstractDecoratingHttpHandler extends AbstractHttpHandler {

	private static final String CTX_TAG_INIT = "init";
	private static final String CTX_TAG_HANDLER = "handler";
	private static final String CTX_TAG_ERROR = "error";

	private static final HttpWrapper[] NO_WRAPPERS = {};

	private final FastHttp http;

	@SuppressWarnings("UnusedParameters")
	public AbstractDecoratingHttpHandler(FastHttp http, HttpRoutes routes, RouteOptions options) {
		super(options);
		this.http = http;
	}

	@Override
	public boolean needsParams() {
		return true;
	}

	@Override
	public final HttpStatus handle(Channel ctx, boolean isKeepAlive, Req req, Object extra) {
		return options.managed()
			? handleDecorating(ctx, isKeepAlive, req, extra)
			: handleNonDecorating(ctx, isKeepAlive, req, extra);
	}

	private HttpStatus handleNonDecorating(Channel ctx, boolean isKeepAlive, Req req, Object extra) {
		Object result;
		MaybeReq maybeReq = HttpUtils.maybe(req);

		ReqImpl reqq = (ReqImpl) req;

		// handle & post-process
		result = handleReqAndPostProcess(ctx, isKeepAlive, req, extra);

		// the response properties might be overwritten
		int code = -1;
		MediaType ctype = contentType;
		if (req != null && reqq.hasResponseAttached()) {
			Resp resp = req.response();
			ctype = resp.contentType();
			code = resp.code();
		}

		if (result == HttpStatus.NOT_FOUND) {
			http.notFound(ctx, isKeepAlive, ctype, this, req);
			return HttpStatus.NOT_FOUND;
		}

		if (result == HttpStatus.ASYNC) {
			return HttpStatus.ASYNC;
		}

		if (result instanceof Throwable) {
			Throwable err = (Throwable) result;
			HttpIO.INSTANCE.writeHttpResp(maybeReq, ctx, isKeepAlive, 500, MediaType.PLAIN_TEXT_UTF_8, "Internal server error!".getBytes());

			Log.error("Error occurred during unmanaged request processing", "request", req, "error", err);
			return HttpStatus.DONE;
		}

		int respCode = code > 0 ? code : 200; // default code is 200
		HttpIO.INSTANCE.writeHttpResp(maybeReq, ctx, isKeepAlive, respCode, ctype, result);

		return HttpStatus.DONE;
	}

	private HttpStatus handleDecorating(Channel ctx, boolean isKeepAlive, Req req, Object extra) {

		if (!ctx.isAsync()) {
			// first checks if not async, to avoid exceptions when running the second time from non-IO thread
			ctx.async();
		}

		execHandlerJob(ctx, isKeepAlive, options.contentType(), req, extra);

		return HttpStatus.ASYNC;
	}

	private Set<String> userRoles(Req req, String username) {
		if (username != null) {
			try {
				return Customization.of(req).rolesProvider().getRolesForUser(req, username);
			} catch (Exception e) {
				throw U.rte(e);
			}
		} else {
			return Collections.emptySet();
		}
	}

	private TransactionMode before(final Req req, String username, Set<String> roles) {

		if (U.notEmpty(options.roles()) && !Secure.hasAnyRole(username, roles, options.roles())) {
			throw new SecurityException("The user doesn't have the required roles!");
		}

		req.response().view(options.view()).contentType(options.contentType()).mvc(options.mvc());

		TransactionMode txMode = U.or(options.transaction(), TransactionMode.NONE);

		if (txMode == TransactionMode.AUTO) {
			txMode = HttpUtils.isGetReq(req) ? TransactionMode.READ_ONLY : TransactionMode.READ_WRITE;
		}

		return txMode;
	}

	private void execHandlerJob(final Channel channel, final boolean isKeepAlive, final MediaType contentType,
	                            final Req req, final Object extra) {

		With.tag(CTX_TAG_INIT).exchange(req).run(new Runnable() {

			volatile String username = null;
			volatile Set<String> roles = null;
			volatile Set<String> scope = null;

			@Override
			public void run() {
				try {

					TokenAuthData auth = HttpUtils.getAuth(req);

					if (auth != null) username = auth.user;

					if (U.isEmpty(username)) {
						HttpUtils.clearUserData(req);
					}

					roles = userRoles(req, username);
					scope = auth != null ? auth.scope : null;

					TransactionMode txMode = before(req, username, roles);
					U.notNull(txMode, "txMode");

					HttpWrapper[] wrappers = httpWrappers != null ? httpWrappers : U.or(Customization.of(req).wrappers(), NO_WRAPPERS);

					Runnable handleRequest = handlerWithWrappers(channel, isKeepAlive, contentType, req, extra, wrappers, txMode);

					With.tag(CTX_TAG_HANDLER).exchange(req).username(username).roles(roles).scope(scope).run(handleRequest);

				} catch (Throwable e) {
					// if there was an error in the job scheduling:
					execErrorHandler(req, username, roles, scope, e);
				}
			}
		});
	}

	private HttpStatus execErrorHandler(final Req req, String username, Set<String> roles, Set<String> scope, final Throwable error) {
		With.tag(CTX_TAG_ERROR).exchange(req).username(username).roles(roles).scope(scope).run(new Runnable() {
			@Override
			public void run() {
				handleError(req, error);
			}
		});

		return HttpStatus.ASYNC;
	}

	private Runnable handlerWithWrappers(final Channel channel, final boolean isKeepAlive, final MediaType contentType,
	                                     final Req req, final Object extra, final HttpWrapper[] wrappers, final TransactionMode txMode) {

		return new Runnable() {

			@Override
			public void run() {
				Object result;
				try {

					if (!U.isEmpty(wrappers)) {
						result = wrap(channel, isKeepAlive, req, 0, extra, wrappers, txMode);
					} else {
						result = handleReqMaybeInTx(channel, isKeepAlive, req, extra, txMode);
					}

				} catch (Throwable e) {
					result = e;
				}

				complete(channel, isKeepAlive, contentType, req, result);
			}
		};
	}

	private Object handleReqMaybeInTx(final Channel channel, final boolean isKeepAlive, final Req req,
	                                  final Object extra, TransactionMode txMode) throws Throwable {

		if (txMode != null && txMode != TransactionMode.NONE) {

			final AtomicReference<Object> result = new AtomicReference<>();

			try {
				JPA.transaction(new Runnable() {
					@Override
					public void run() {
						Object res = handleReqAndPostProcess(channel, isKeepAlive, req, extra);

						if (res instanceof Throwable) {
							// throw to rollback
							Throwable err = (Throwable) res;
							throw U.rte("Error occurred inside the transactional web handler!", err);
						}

						result.set(res);
					}
				}, txMode == TransactionMode.READ_ONLY);

			} catch (Throwable e) {
				result.set(e);
			}

			return result.get();

		} else {
			return handleReqAndPostProcess(channel, isKeepAlive, req, extra);
		}
	}

	private Runnable txWrap(final TransactionMode txMode, final Runnable handleRequest) {
		if (txMode != null && txMode != TransactionMode.NONE) {

			return new Runnable() {
				@Override
				public void run() {
					JPA.transaction(handleRequest, txMode == TransactionMode.READ_ONLY);
				}
			};

		} else {
			return handleRequest;
		}
	}

	private Object wrap(final Channel channel, final boolean isKeepAlive, final Req req, final int index,
	                    final Object extra, final HttpWrapper[] wrappers, final TransactionMode txMode) throws Exception {

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
						val = wrap(channel, isKeepAlive, req, next, extra, wrappers, txMode);
					} else {
						val = handleReqMaybeInTx(channel, isKeepAlive, req, extra, txMode);
					}

					return transformation != null ? transformation.map(val) : val;

				} catch (Throwable e) {
					return e;
				}
			}
		};

		return wrapper.wrap(req, invocation);
	}

	private Object handleError(Req req, Throwable e) {
		req.revert();
		req.async();

		HttpIO.INSTANCE.error(req, e, LogLevel.ERROR);
		// the Req object will do the rendering
		req.done();

		return req;
	}

	protected abstract Object handleReq(Channel ctx, boolean isKeepAlive, Req req, Object extra) throws Throwable;

	private Object handleReqAndPostProcess(Channel ctx, boolean isKeepAlive, Req req, Object extra) {
		Object result;

		try {
			result = handleReq(ctx, isKeepAlive, req, extra);

		} catch (Throwable e) {
			result = e;
		}

		return HandlerResultProcessor.INSTANCE.postProcessResult(req, result);
	}

	public void complete(Channel ctx, boolean isKeepAlive, MediaType contentType, Req req, Object result) {

		U.must(result != null, "The post-processed result cannot be null!");
		U.must(!(result instanceof Req), "The post-processed result cannot be a Req instance!");
		U.must(!(result instanceof Resp), "The post-processed result cannot be a Resp instance!");

		if (result instanceof Throwable) {
			handleError(req, (Throwable) result);
			return;
		}

		if (result == HttpStatus.NOT_FOUND) {
			http.notFound(ctx, isKeepAlive, contentType, this, req);
			return;
		}

		if (result == HttpStatus.ERROR) {
			complete(ctx, isKeepAlive, contentType, req, U.rte("Handler error!"));
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
