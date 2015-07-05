package org.rapidoid.ctx;

/*
 * #%L
 * rapidoid-ctx
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

/**
 * @author Nikolche Mihajlovski
 * @since 2.0.0
 */
public class Ctx {

	private static final ThreadLocal<Ctx> CTXS = new ThreadLocal<Ctx>();

	private static volatile PersistorFactory persistorFactory = null;

	private UserInfo user;

	private Object exchange;

	private Classes classes;

	private Object persistor;

	private Ctx() {}

	private static Ctx ctx() {
		Ctx ctx = CTXS.get();

		if (ctx == null) {
			throw new IllegalStateException("App ctx wasn't set!");
		}

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

		if (ctx.user != null) {
			throw new IllegalStateException("The user was already set!");
		}

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

		if (ctx.exchange != null) {
			throw new IllegalStateException("The exchange was already set!");
		}

		ctx.exchange = exchange;
	}

	@SuppressWarnings("unchecked")
	public static <T extends AppExchange> T exchange() {
		Ctx ctx = CTXS.get();
		return ctx != null ? (T) ctx.exchange : null;
	}

	public static void delExchange() {
		Ctx ctx = ctx();
		ctx.exchange = null;
	}

	public static void setClasses(Classes classes) {
		Ctx ctx = provideCtx();

		if (ctx.classes != null) {
			throw new IllegalStateException("The classes were already set!");
		}

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

	public static void setPersistor(Object persistor) {
		Ctx ctx = provideCtx();

		if (ctx.persistor != null) {
			throw new IllegalStateException("The persistor was already set!");
		}

		ctx.persistor = persistor;
	}

	@SuppressWarnings("unchecked")
	public static <P> P persistor() {

		AppExchange x = Ctx.exchange();
		if (x != null) {
			return x.persistor();
		}

		Ctx ctx = provideCtx();

		if (ctx.persistor == null) {
			ctx.persistor = Ctx.persistorFactory.createPersistor();
		}

		return (P) ctx.persistor;
	}

	public static void delPersistor() {
		Ctx ctx = ctx();
		ctx.persistor = null;
	}

	public static PersistorFactory getPersistorFactory() {
		return persistorFactory;
	}

	public static void setPersistorFactory(PersistorFactory persistorFactory) {
		Ctx.persistorFactory = persistorFactory;
	}

	public static void clear() {
		delClasses();
		delExchange();
		delUser();
		delPersistor();
	}

	public Ctx copy() {
		Ctx ctx = new Ctx();

		ctx.classes = this.classes;
		ctx.exchange = this.exchange;
		ctx.user = this.user;
		ctx.persistor = this.persistor;

		return ctx;
	}

}
