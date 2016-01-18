package org.rapidoid.http.fast.listener;

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
import org.rapidoid.commons.MediaType;
import org.rapidoid.data.Range;
import org.rapidoid.data.Ranges;
import org.rapidoid.http.Req;
import org.rapidoid.http.fast.FastHttp;
import org.rapidoid.http.fast.HttpWrapper;
import org.rapidoid.http.fast.handler.FastHttpHandler;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.wrap.BoolWrap;

@Authors("Nikolche Mihajlovski")
@Since("5.0.2")
public interface FastHttpListener {

	boolean request(FastHttp http, Channel channel, BoolWrap isGet, BoolWrap isKeepAlive, Range body, Range verb,
			Range uri, Range path, Range query, Range protocol, Ranges hdrs);

	void notFound(FastHttp fastHttp, Channel ctx, BoolWrap isGet, BoolWrap isKeepAlive, Range body, Range verb,
			Range uri, Range path, Range query, Range protocol, Ranges hdrs);

	void state(FastHttpHandler handler, Req req);

	void result(FastHttpHandler handler, MediaType contentType, Object result);

	void entering(HttpWrapper wrapper, Req req);

	void leaving(HttpWrapper wrapper, MediaType contentType, Object result);

	void resultNotFound(FastHttpHandler handler);

	void onOkResponse(MediaType contentType, byte[] content);

	void onErrorResponse(int code, MediaType contentType, byte[] content);

}
