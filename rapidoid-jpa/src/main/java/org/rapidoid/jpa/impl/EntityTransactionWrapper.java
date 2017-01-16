package org.rapidoid.jpa.impl;

/*
 * #%L
 * rapidoid-jpa
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import javax.persistence.EntityTransaction;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class EntityTransactionWrapper extends RapidoidThing implements EntityTransaction {

	private final EntityTransaction transaction;

	public EntityTransactionWrapper(EntityTransaction transaction) {
		this.transaction = transaction;
	}

	@Override
	public void begin() {
		transaction.begin();
	}

	@Override
	public void commit() {
		transaction.commit();
	}

	@Override
	public void rollback() {
		transaction.rollback();
	}

	@Override
	public void setRollbackOnly() {
		transaction.setRollbackOnly();
	}

	@Override
	public boolean getRollbackOnly() {
		return transaction.getRollbackOnly();
	}

	@Override
	public boolean isActive() {
		return transaction.isActive();
	}
}
