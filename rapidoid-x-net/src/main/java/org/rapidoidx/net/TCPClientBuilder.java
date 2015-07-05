package org.rapidoidx.net;

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
import org.rapidoid.util.Builder;
import org.rapidoidx.net.impl.DefaultExchange;
import org.rapidoidx.net.impl.RapidoidHelper;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public interface TCPClientBuilder extends Builder<TCPClient> {

	TCPClientBuilder connections(int connections);

	TCPClientBuilder reconnecting(boolean reconnecting);

	TCPClientBuilder bufSize(int bufSize);

	TCPClientBuilder host(String host);

	TCPClientBuilder port(int port);

	TCPClientBuilder workers(int workers);

	TCPClientBuilder nagle();

	TCPClientBuilder stats();

	TCPClientBuilder micro();

	TCPClientBuilder protocol(Protocol protocol);

	TCPClientBuilder exchange(Class<? extends DefaultExchange<?, ?>> exchangeClass);

	TCPClientBuilder helper(Class<? extends RapidoidHelper> helperClass);

}
