package org.rapidoidx.net;

import org.rapidoid.activity.Activity;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoidx.net.abstracts.ChannelHolder;
import org.rapidoidx.net.impl.ConnState;

/*
 * #%L
 * rapidoid-x-net
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public interface TCPClient extends Activity<TCPClient> {

	ChannelHolder connect(String serverHost, int serverPort, Protocol clientProtocol, boolean autoreconnecting,
			ConnState state);

	ChannelHolder[] connect(String serverHost, int serverPort, Protocol clientProtocol, int connections,
			boolean autoreconnecting, ConnState state);

	TCPClientInfo info();

}
