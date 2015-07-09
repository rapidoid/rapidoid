package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http
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

import java.io.File;
import java.nio.ByteBuffer;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.http.session.SessionStore;
import org.rapidoid.log.Log;
import org.rapidoid.net.Protocol;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.impl.ExchangeProtocol;
import org.rapidoid.net.impl.RapidoidConnection;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;
import org.rapidoid.util.Usage;
import org.rapidoid.wire.Wire;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class HttpProtocol extends ExchangeProtocol<HttpExchangeImpl> {

	private final HttpParser parser = Wire.singleton(HttpParser.class);

	private final Router router;

	private final HttpResponses responses;

	private final HttpUpgrades upgrades = new HttpUpgrades();

	private SessionStore sessionStore;

	private HTTPInterceptor interceptor;

	public HttpProtocol(Router router) {
		super(HttpExchangeImpl.class);
		this.router = router;
		this.responses = new HttpResponses(true, true);
	}

	@Override
	protected void process(Channel ctx, HttpExchangeImpl x) {
		U.notNull(responses, "responses");
		U.notNull(sessionStore, "sessionStore");
		U.notNull(router, "router");

		if (ctx.isInitial()) {
			return;
		}

		Usage.touchLastAppUsedOn();

		parser.parse(x.input(), x.isGet, x.isKeepAlive, x.body, x.verb, x.uri, x.path, x.query, x.protocol, x.headers,
				x.helper());

		String upgrade = x.header("Upgrade", null);
		if (!U.isEmpty(upgrade)) {
			processUpgrade(ctx, x, upgrade);
			return;
		}

		U.rteIf(x.verb.isEmpty() || x.uri.isEmpty(), "Invalid HTTP request!");
		U.rteIf(x.isGet.value && !x.body.isEmpty(), "Body is NOT allowed in HTTP GET requests!");

		processRequest(x);
	}

	private void processUpgrade(Channel ctx, HttpExchangeImpl x, String upgrade) {
		Log.debug("Starting HTTP protocol upgrade", "upgrade", upgrade);

		HttpUpgradeHandler upgradeHandler = upgrades.getUpgrade(upgrade);
		Protocol upgradeTo = upgrades.getProtocol(upgrade);

		U.must(upgradeHandler != null && upgradeTo != null, "Upgrade not supported: %s", upgrade);

		upgradeHandler.doUpgrade(x);

		RapidoidConnection conn = (RapidoidConnection) ctx;
		conn.setProtocol(upgradeTo);

		conn.setInitial(true);
		upgradeTo.process(ctx);
		conn.setInitial(false);
	}

	private void processRequest(HttpExchangeImpl x) {

		x.init(responses, sessionStore, router);

		Ctxs.ctx().setUser(x.user());
		try {
			executeRequest(x);
		} finally {
			Ctxs.ctx().setUser(null);
		}
	}

	private void executeRequest(HttpExchangeImpl x) {
		try {
			if (interceptor != null) {
				interceptor.intercept(x);
			} else {
				x.run();
			}
		} catch (Throwable e) {
			handleError(x, e);
		}

		if (x.hasError()) {
			handleError(x, x.getError());
		} else if (!x.isAsync()) {
			x.completeResponse();
		}
	}

	public static void handleError(HttpExchangeImpl x, Throwable e) {
		Throwable cause = UTILS.rootCause(e);

		if (cause instanceof HttpSuccessException) {
			// redirect
			x.completeResponse();
		} else if (cause instanceof HttpNotFoundException) {
			// notFound
			Log.warn("HTTP resource not found!", "uri", x.uri());
			x.completeResponse();
		} else if (cause instanceof ThreadDeath) {
			Log.error("Thread death, probably timeout!", "request", x, "error", cause);
			x.response(500, "Request timeout!", null);
		} else {
			Log.error("Internal server error!", "request", x, "error", cause);
			x.errorResponse(e);
			x.completeResponse();
		}
	}

	public static void processResponse(HttpExchange xch, Object res) {

		HttpExchangeImpl x = (HttpExchangeImpl) xch;

		if (x.isLowLevelProcessing()) {
			return;
		}

		if (res != null) {
			if (res instanceof byte[]) {
				if (!x.hasContentType()) {
					x.binary();
				}
				x.write((byte[]) res);
			} else if (res instanceof String) {
				if (!x.hasContentType()) {
					x.json();
				}
				x.write((String) res);
			} else if (res instanceof ByteBuffer) {
				if (!x.hasContentType()) {
					x.binary();
				}
				x.write((ByteBuffer) res);
			} else if (res instanceof File) {
				File file = (File) res;
				x.sendFile(file);
			} else if (res.getClass().getSimpleName().endsWith("Page")) {
				x.html().write(res.toString());
			} else if (!(res instanceof HttpExchangeImpl)) {
				if (!x.hasContentType()) {
					x.json();
				}
				x.writeJSON(res);
			}

		} else {
			if (!x.hasContentType()) {
				x.html();
			}
			throw x.notFound();
		}
	}

	public Router getRouter() {
		return router;
	}

	public void setSessionStore(SessionStore sessionStore) {
		this.sessionStore = sessionStore;
	}

	public void setInterceptor(HTTPInterceptor interceptor) {
		this.interceptor = interceptor;
	}

	public HTTPInterceptor getInterceptor() {
		return interceptor;
	}

	public void addUpgrade(String upgradeName, HttpUpgradeHandler upgrade, Protocol protocol) {
		upgrades.add(upgradeName, upgrade, protocol);
	}

}
