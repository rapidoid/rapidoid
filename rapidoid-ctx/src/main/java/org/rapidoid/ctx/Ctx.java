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

	private volatile UserInfo user;

	private volatile Object exchange;

	private volatile Classes classes;

	private volatile Object persister;

	private volatile int referenceCounter;

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
	public synchronized <P> P persister() {
		if (this.persister == null) {
			this.persister = Ctxs.createPersister();
		}

		return (P) this.persister;
	}

	public void setPersister(Object persister) {
		this.persister = persister;
	}

	synchronized void span() {
		referenceCounter++;
	}

	synchronized void close() {
		referenceCounter--;

		if (referenceCounter == 0) {
			if (persister != null) {
				Ctxs.closePersister(persister);
				persister = null;
			}

		} else if (referenceCounter < 0) {
			throw new IllegalStateException("Reference counter < 0 for context: " + this);
		}
	}

	@Override
	public String toString() {
		return super.toString() + "[user=" + user + ", exchange=" + exchange + ", classes=" + classes + ", persister="
				+ persister + ", referenceCounter=" + referenceCounter + "]";
	}

}
