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
 * @since 4.1.0
 */
public class Ctxs {

	private static final ThreadLocal<Ctx> CTXS = new ThreadLocal<Ctx>();

	private static volatile PersistorFactory persistorFactory = null;

	private Ctxs() {}

	public static Ctx ctx() {
		Ctx ctx = CTXS.get();

		if (ctx == null) {
			throw new IllegalStateException("App ctx wasn't set!");
		}

		return ctx;
	}

	public static boolean hasContext() {
		return CTXS.get() != null;
	}

	private static Ctx provide() {
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

	public static void open() {
		provide().clear();
	}

	public static void close() {
		CTXS.remove();
	}

	public static PersistorFactory getPersistorFactory() {
		return persistorFactory;
	}

	public static void setPersistorFactory(PersistorFactory persistorFactory) {
		Ctxs.persistorFactory = persistorFactory;
	}

	public static Object createPersistor() {
		return persistorFactory.createPersistor();
	}

}
