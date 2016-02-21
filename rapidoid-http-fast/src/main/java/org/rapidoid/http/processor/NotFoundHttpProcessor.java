package org.rapidoid.http.processor;

/*
 * #%L
 * rapidoid-http-fast
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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
import org.rapidoid.data.Range;
import org.rapidoid.data.Ranges;
import org.rapidoid.net.abstracts.Channel;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class NotFoundHttpProcessor extends AbstractHttpProcessor {

	private static final byte[] HTTP_404_NOT_FOUND = "HTTP/1.1 404 Not Found\r\nContent-Length: 10\r\n\r\nNot found!"
			.getBytes();

	public NotFoundHttpProcessor() {
		super(null);
	}

	@Override
	public void request(Channel channel, boolean isGet, boolean isKeepAlive, Range body,
	                    Range verb, Range uri, Range path, Range query, Range protocol, Ranges headers) {

		channel.write(HTTP_404_NOT_FOUND);
		channel.done();
		channel.closeIf(!isKeepAlive);
	}

}
