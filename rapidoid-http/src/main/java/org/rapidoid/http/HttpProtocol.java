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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.apps.Application;
import org.rapidoid.apps.Applications;
import org.rapidoid.bytes.BytesUtil;
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
		super(HttpExchangeImpl.class, true);
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

		if (upgradable()) {
			String upgrade = x.header("Upgrade", null);
			if (!U.isEmpty(upgrade)) {
				processUpgrade(ctx, x, upgrade);
				return;
			}
		}

		// FIXME separate responses from session store and router, per app
		x.init(responses, sessionStore, router);

		String err = validateRequest(x);
		if (err != null) {
			x.startResponse(404).html().write(err);
			x.completeResponse();
			return;
		}

		processRequest(x);
	}

	private String validateRequest(HttpExchangeImpl x) {
		if (x.verb.isEmpty()) {
			return "HTTP verb cannot be empty!";
		}

		if (!BytesUtil.isValidURI(x.input().bytes(), x.uri)) {
			return "Invalid HTTP URI!";
		}

		if (x.isGet.value && !x.body.isEmpty()) {
			return "Body is NOT allowed in HTTP GET requests!";
		}

		return null; // OK, not error
	}

	protected boolean upgradable() {
		return false;
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
		Application app = Applications.main().get(x.host(), x.pathSegment(0));
		U.must(app != null, "The application must be provided!");

		Ctxs.ctx().setApp(app);
		Ctxs.ctx().setUser(x.user());

		try {
			executeRequest(x);

		} finally {
			Ctxs.ctx().setUser(null);
			Ctxs.ctx().setApp(null);
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
			x.error(e);
			x.completeResponse();
		}
	}

	public static void processResponse(HttpExchange xch, Object res) {

		HttpExchangeImpl x = (HttpExchangeImpl) xch;

		if (x.isLowLevelProcessing() || x.isAsync() || res == xch) {
			return;
		}

		if (res != null) {
			x.result(res);
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
