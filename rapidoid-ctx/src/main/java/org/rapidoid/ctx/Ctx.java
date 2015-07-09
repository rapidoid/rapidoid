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

	private UserInfo user;

	private Object exchange;

	private Classes classes;

	private Object persistor;

	Ctx() {}

	public UserInfo user() {
		return user;
	}

	public void setUser(UserInfo user) {
		this.user = user;
	}

	@SuppressWarnings("unchecked")
	public <T> T exchange() {
		return (T) exchange;
	}

	public void setExchange(Object exchange) {
		this.exchange = exchange;
	}

	public Classes classes() {
		return classes;
	}

	public void setClasses(Classes classes) {
		this.classes = classes;
	}

	@SuppressWarnings("unchecked")
	public <P> P persistor() {
		if (this.persistor == null) {
			this.persistor = Ctxs.createPersistor();
		}

		return (P) this.persistor;
	}

	public void setPersistor(Object persistor) {
		this.persistor = persistor;
	}

	public void clear() {
		setClasses(null);
		setExchange(null);
		setUser(null);
		setPersistor(null);
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
