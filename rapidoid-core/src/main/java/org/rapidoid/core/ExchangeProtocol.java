package org.rapidoid.core;

/*
 * #%L
 * rapidoid-core
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import org.rapidoid.Connection;
import org.rapidoid.Ctx;
import org.rapidoid.Protocol;
import org.rapidoid.pool.Pool;

public abstract class ExchangeProtocol<T extends Exchange> implements Protocol, ConnectionListener {

	private final Class<T> exchangeType;

	public ExchangeProtocol(final Class<T> exchangeType) {
		this.exchangeType = exchangeType;
	}

	@Override
	public void process(Ctx ctx) {
		T exchange = pool(ctx.helper()).get();
		exchange.reset();

		assert exchange != null;
		assert exchangeType.isAssignableFrom(exchange.getClass());

		exchange.setConnection(ctx.connection());
		process(ctx, exchange);
	}

	protected abstract void process(Ctx ctx, T exchange);

	@SuppressWarnings("unchecked")
	@Override
	public final void beforeWriting(Connection conn, Object tag, int kind) {
		assert tag != null;
		assert exchangeType.isAssignableFrom(tag.getClass());

		before(conn, (T) tag, kind);
	}

	protected void before(Connection conn, T exchange, int kind) {
	}

	@SuppressWarnings("unchecked")
	@Override
	public final void afterWriting(Connection conn, Object tag, int kind) {
		assert tag != null;
		assert exchangeType.isAssignableFrom(tag.getClass());

		after(conn, (T) tag, kind);
	}

	protected void after(Connection conn, T exchange, int kind) {
	}

	@SuppressWarnings("unchecked")
	@Override
	public final void onComplete(RapidoidConnection conn, Object tag) {
		assert tag != null;
		assert exchangeType.isAssignableFrom(tag.getClass());

		complete(conn, (T) tag);
	}

	protected void complete(Connection conn, T exchange) {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onDone(Connection conn, Object tag) {
		assert tag != null;
		assert exchangeType.isAssignableFrom(tag.getClass());

		pool(conn.helper()).release((T) tag);
	}

	@SuppressWarnings("unchecked")
	protected Pool<T> pool(RapidoidHelper helper) {
		return (Pool<T>) helper.pool();
	}

}
