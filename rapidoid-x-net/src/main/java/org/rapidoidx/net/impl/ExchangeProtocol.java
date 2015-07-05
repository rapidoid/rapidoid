package org.rapidoidx.net.impl;

/*
 * #%L
 * rapidoid-x-net
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.pool.Pool;
import org.rapidoidx.net.Protocol;
import org.rapidoidx.net.abstracts.Channel;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public abstract class ExchangeProtocol<T extends DefaultExchange<?, ?>> implements Protocol, CtxListener {

	private final Class<T> exchangeType;

	public ExchangeProtocol(final Class<T> exchangeType) {
		this.exchangeType = exchangeType;
	}

	@Override
	public void process(Channel ctx) {
		T exchange = pool(ctx.helper()).get();
		assert Cls.instanceOf(exchange, exchangeType);

		exchange.reset();
		exchange.setConnection(ctx);

		Ctx.setExchange(exchange);
		process(ctx, exchange);
		Ctx.delExchange();
	}

	protected abstract void process(Channel ctx, T exchange);

	@SuppressWarnings("unchecked")
	@Override
	public void onDone(Channel conn, Object tag) {
		assert Cls.instanceOf(tag, exchangeType);

		pool(conn.helper()).release((T) tag);
	}

	@SuppressWarnings("unchecked")
	protected Pool<T> pool(RapidoidHelper helper) {
		return (Pool<T>) helper.pool;
	}

}
