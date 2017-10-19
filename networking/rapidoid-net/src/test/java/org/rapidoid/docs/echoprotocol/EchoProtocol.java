package org.rapidoid.docs.echoprotocol;

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

import org.rapidoid.net.Protocol;
import org.rapidoid.net.abstracts.Channel;

public class EchoProtocol implements Protocol {

	@Override
	public void process(Channel ctx) {
		if (ctx.isInitial()) return;

		String line = ctx.readln().toUpperCase();

		synchronized (ctx.output()) {
			ctx.write(line).write(CR_LF);
			ctx.closeIf(line.equals("BYE"));
		}
	}

}
