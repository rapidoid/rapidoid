package org.rapidoid.jpa.impl;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import javax.persistence.EntityTransaction;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class EntityTransactionWrapper implements EntityTransaction {

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
