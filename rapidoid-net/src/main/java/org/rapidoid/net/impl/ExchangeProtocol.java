package org.rapidoid.net.impl;

/*
 * #%L
 * rapidoid-net
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
import org.rapidoid.cls.Cls;
import org.rapidoid.net.Protocol;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.pool.Pool;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public abstract class ExchangeProtocol<T extends DefaultExchange<?>> extends RapidoidThing implements Protocol, CtxListener {

	private final Class<T> exchangeType;
	private final boolean useExchangePool;

	public ExchangeProtocol(final Class<T> exchangeType, boolean useExchangePool) {
		this.exchangeType = exchangeType;
		this.useExchangePool = useExchangePool;
	}

	@Override
	public void process(Channel ctx) {
		T exchange = useExchangePool ? pool(ctx.helper()).get() : Cls.newInstance(exchangeType);
		assert Cls.instanceOf(exchange, exchangeType);

		exchange.reset();
		exchange.setConnection(ctx);

		process(ctx, exchange);
	}

	protected abstract void process(Channel ctx, T exchange);

	@SuppressWarnings("unchecked")
	@Override
	public void onDone(Channel conn, Object tag) {
		assert Cls.instanceOf(tag, exchangeType);

		if (useExchangePool) {
			pool(conn.helper()).release((T) tag);
		}
	}

	@SuppressWarnings("unchecked")
	protected Pool<T> pool(RapidoidHelper helper) {
		return (Pool<T>) helper.pool;
	}

}
