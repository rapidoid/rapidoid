package org.rapidoidx.net;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.Classes;
import org.rapidoid.util.U;
import org.rapidoid.util.UserInfo;

/*
 * #%L
 * rapidoid-x-net
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

@Authors("Nikolche Mihajlovski")
@Since("3.1.0")
public class Ctx {

	private static final ThreadLocal<Ctx> CTXS = new ThreadLocal<Ctx>();

	private UserInfo user;

	private Object exchange;

	private Classes classes;

	private Ctx() {}

	private static Ctx ctx() {
		Ctx ctx = CTXS.get();
		U.must(ctx != null, "App ctx wasn't set!");
		return ctx;
	}

	public static boolean hasContext() {
		return CTXS.get() != null;
	}

	private static Ctx provideCtx() {
		Ctx ctx = CTXS.get();

		if (ctx == null) {
			ctx = new Ctx();
			CTXS.set(ctx);
		}

		return ctx;
	}

	public static void reset() {
		CTXS.remove();
	}

	public static void setUser(UserInfo user) {
		Ctx ctx = provideCtx();
		U.must(ctx.user == null, "The username was already set!");
		ctx.user = user;
	}

	public static UserInfo user() {
		Ctx ctx = CTXS.get();
		return ctx != null ? ctx.user : null;
	}

	public static void delUser() {
		Ctx ctx = ctx();
		ctx.user = null;
	}

	public static void setExchange(Object exchange) {
		Ctx ctx = provideCtx();
		U.must(ctx.exchange == null, "The exchange was already set!");
		ctx.exchange = exchange;
	}

	@SuppressWarnings("unchecked")
	public static <T> T exchange() {
		Ctx ctx = CTXS.get();
		return ctx != null ? (T) ctx.exchange : null;
	}

	public static void delExchange() {
		Ctx ctx = ctx();
		ctx.exchange = null;
	}

	public static void setClasses(Classes classes) {
		Ctx ctx = provideCtx();
		U.must(ctx.classes == null, "The classes were already set!");
		ctx.classes = classes;
	}

	public static Classes classes() {
		Ctx ctx = CTXS.get();
		return ctx != null ? ctx.classes : null;
	}

	public static void delClasses() {
		Ctx ctx = ctx();
		ctx.classes = null;
	}

}
