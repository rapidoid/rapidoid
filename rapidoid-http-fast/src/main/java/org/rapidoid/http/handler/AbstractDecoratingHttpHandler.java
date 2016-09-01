package org.rapidoid.http.handler;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.annotation.TransactionMode;
import org.rapidoid.ctx.With;
import org.rapidoid.http.*;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.impl.HttpIO;
import org.rapidoid.http.impl.RouteOptions;
import org.rapidoid.jpa.JPA;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.log.LogLevel;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.security.Secure;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.Collections;
import java.util.Set;

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

		try {
			result = handleReq(ctx, isKeepAlive, req, extra);

		} catch (Exception e) {
			HttpIO.writeResponse(ctx, isKeepAlive, 500, contentType, "Internal server error!".getBytes());
			return HttpStatus.ERROR;
		}

		if (contentType == MediaType.JSON) {
			HttpIO.writeAsJson(ctx, 200, isKeepAlive, result);
		} else {
			HttpIO.write200(ctx, isKeepAlive, contentType, Msc.toBytes(result));
		}

		return HttpStatus.DONE;
	}

	private HttpStatus handleDecorating(Channel ctx, boolean isKeepAlive, Req req, Object extra) {
		ctx.async();

		execHandlerJob(ctx, isKeepAlive, options.contentType(), req, extra);

		return HttpStatus.ASYNC;
	}

	private String getUser(Req req) {
		if (req.hasToken()) {
			String username = req.token(HttpUtils._USER, null);

			if (username != null) {
				long expiresOn = req.token(HttpUtils._EXPIRES);

				if (expiresOn < U.time()) {
					username = null; // expired
				}
			}

			return username;

		} else {
			return null;
		}
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

		TransactionMode txMode = U.or(options.transactionMode(), TransactionMode.NONE);

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

			@Override
			public void run() {
				try {
					username = getUser(req);

					if (U.isEmpty(username)) {
						HttpUtils.clearUserData(req);
					}

					roles = userRoles(req, username);

					TransactionMode txMode = before(req, username, roles);
					U.notNull(txMode, "txMode");

					HttpWrapper[] wrappers = httpWrappers != null ? httpWrappers : U.or(Customization.of(req).wrappers(), NO_WRAPPERS);

					Runnable handleRequest = handlerWithWrappers(channel, isKeepAlive, contentType, req, extra, wrappers);
					Runnable handleRequestMaybeInTx = txWrap(req, txMode, handleRequest);

					With.tag(CTX_TAG_HANDLER).exchange(req).username(username).roles(roles).run(handleRequestMaybeInTx);

				} catch (Throwable e) {
					// if there was an error in the job scheduling:
					execErrorHandler(req, username, roles, e);
				}
			}
		});
	}

	private HttpStatus execErrorHandler(final Req req, String username, Set<String> roles, final Throwable error) {
		With.tag(CTX_TAG_ERROR).exchange(req).username(username).roles(roles).run(new Runnable() {
			@Override
			public void run() {
				handleError(req, error);
			}
		});

		return HttpStatus.ASYNC;
	}

	private Runnable handlerWithWrappers(final Channel channel, final boolean isKeepAlive, final MediaType contentType,
	                                     final Req req, final Object extra, final HttpWrapper[] wrappers) {

		return new Runnable() {

			@Override
			public void run() {
				Object result;
				try {

					if (!U.isEmpty(wrappers)) {
						result = wrap(channel, isKeepAlive, req, 0, extra, wrappers);
					} else {
						result = handleReq(channel, isKeepAlive, req, extra);
					}

					result = HttpUtils.postprocessResult(req, result);
				} catch (Throwable e) {
					result = e;
				}

				complete(channel, isKeepAlive, contentType, req, result);
			}
		};
	}

	private Runnable txWrap(final Req req, final TransactionMode txMode, final Runnable handleRequest) {
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
	                    final Object extra, final HttpWrapper[] wrappers) throws Exception {

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
						val = wrap(channel, isKeepAlive, req, next, extra, wrappers);
					} else {
						val = handleReq(channel, isKeepAlive, req, extra);
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

		HttpIO.error(req, e, LogLevel.ERROR);
		// the Req object will do the rendering
		req.done();

		return req;
	}

	protected abstract Object handleReq(Channel ctx, boolean isKeepAlive, Req req, Object extra) throws Exception;

	public void complete(Channel ctx, boolean isKeepAlive, MediaType contentType, Req req, Object result) {

		if (result == null || result instanceof NotFound) {
			http.notFound(ctx, isKeepAlive, contentType, this, req);
			return; // not found
		}

		if (result instanceof HttpStatus) {
			complete(ctx, isKeepAlive, contentType, req, U.rte("HttpStatus result is not supported!"));
			return;
		}

		if (result instanceof Throwable) {
			handleError(req, (Throwable) result);
			return;

		} else {
			HttpUtils.resultToResponse(req, result);
		}

		// the Req object will do the rendering
		if (!req.isAsync()) {
			req.done();
		}
	}

}
