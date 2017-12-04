/*-
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

package org.rapidoid.net.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.net.Protocol;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.abstracts.ChannelHolder;
import org.rapidoid.u.U;


@Authors("Nikolche Mihajlovski")
@Since("5.5.0")
public abstract class ProtocolBridge extends RapidoidThing {

	protected abstract void peer1(Channel ctx);

	protected abstract void peer2(Channel ctx);

	public Protocol peer1() {
		return new Protocol() {
			@Override
			public void process(Channel ctx) {
				peer1(ctx);
			}
		};
	}

	public Protocol peer2() {
		return new Protocol() {
			@Override
			public void process(Channel ctx) {
				peer2(ctx);
			}
		};
	}

	protected Channel peer(Channel ctx) {
		ChannelHolder holder = (ChannelHolder) ctx.state().obj;
		U.notNull(holder, "channel holder");

		if (holder.channel() == null) {
			// client not connected, so maybe later...
			throw Buf.INCOMPLETE_READ;
		}

		return holder.channel();
	}

}
