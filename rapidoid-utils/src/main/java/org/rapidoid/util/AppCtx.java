package org.rapidoid.util;

/*
 * #%L
 * rapidoid-utils
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

public class AppCtx {

	private static final ThreadLocal<AppCtx> CTXS = new ThreadLocal<AppCtx>();

	private String username;

	private Object exchange;

	private AppCtx() {
	}

	private static AppCtx ctx() {
		AppCtx ctx = CTXS.get();
		U.must(ctx != null, "App ctx wasn't set!");
		return ctx;
	}

	private static AppCtx provideCtx() {
		AppCtx ctx = CTXS.get();

		if (ctx == null) {
			ctx = new AppCtx();
			CTXS.set(ctx);
		}

		return ctx;
	}

	public static void reset() {
		CTXS.remove();
	}

	public static void setUsername(String username) {
		AppCtx ctx = provideCtx();
		U.must(ctx.username == null, "The username was already set!");
		ctx.username = username;
	}

	public static String username() {
		return ctx().username;
	}

	public static void delUsername() {
		AppCtx ctx = ctx();
		ctx.username = null;
	}

	public static void setExchange(Object exchange) {
		AppCtx ctx = provideCtx();
		U.must(ctx.exchange == null, "The exchange was already set!");
		ctx.exchange = exchange;
	}

	@SuppressWarnings("unchecked")
	public static <T> T exchange() {
		return (T) ctx().exchange;
	}

	public static void delExchange() {
		AppCtx ctx = ctx();
		ctx.exchange = null;
	}

}
