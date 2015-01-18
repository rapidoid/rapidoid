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

	private Object extra;

	private AppCtx() {
	}

	public static String username() {
		return CTXS.get().username;
	}

	@SuppressWarnings("unchecked")
	public static <T> T extra() {
		return (T) CTXS.get().extra;
	}

	public static void set(String username, Object extra) {
		U.must(CTXS.get() == null, "The app context is already set!");

		AppCtx ctx = new AppCtx();

		ctx.username = username;
		ctx.extra = extra;

		CTXS.set(ctx);
	}

	public static void reset() {
		U.must(CTXS.get() != null, "The app context is already empty!");
		CTXS.remove();
	}

}
