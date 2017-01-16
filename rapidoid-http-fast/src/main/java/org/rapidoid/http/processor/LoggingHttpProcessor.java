package org.rapidoid.http.processor;

/*
 * #%L
 * rapidoid-http-fast
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.log.Log;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.impl.RapidoidHelper;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class LoggingHttpProcessor extends AbstractHttpProcessor {

	public LoggingHttpProcessor(HttpProcessor next) {
		super(next);
	}

	@Override
	public void onRequest(Channel channel, RapidoidHelper data) {

		Buf buf = channel.input();
		Log.debug("HTTP request", "verb", buf.get(data.verb), "uri", buf.get(data.uri), "protocol", buf.get(data.protocol));

		next.onRequest(channel, data);
	}

}
