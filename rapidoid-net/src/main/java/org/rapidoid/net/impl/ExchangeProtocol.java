package org.rapidoid.net.impl;

/*
 * #%L
 * rapidoid-net
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

import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.pool.Pool;
import org.rapidoid.util.U;

public abstract class ExchangeProtocol<T extends DefaultExchange<?, ?>> implements Protocol, CtxListener {

	private final Class<T> exchangeType;

	public ExchangeProtocol(final Class<T> exchangeType) {
		this.exchangeType = exchangeType;
	}

	@Override
	public void process(Channel ctx) {
		T exchange = pool(ctx.helper()).get();
		exchange.reset();

		assert U.instanceOf(exchange, exchangeType);

		exchange.setConnection(ctx);
		process(ctx, exchange);
	}

	protected abstract void process(Channel ctx, T exchange);

	@SuppressWarnings("unchecked")
	@Override
	public void onDone(Channel conn, Object tag) {
		assert U.instanceOf(tag, exchangeType);

		pool(conn.helper()).release((T) tag);
	}

	@SuppressWarnings("unchecked")
	protected Pool<T> pool(RapidoidHelper helper) {
		return (Pool<T>) helper.pool;
	}

}
