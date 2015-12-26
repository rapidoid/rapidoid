package org.rapidoid.http.fast.handler;

/*
 * #%L
 * rapidoid-web
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.MediaType;
import org.rapidoid.http.Req;
import org.rapidoid.http.Resp;
import org.rapidoid.http.fast.FastHttp;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.pojo.DispatchResult;
import org.rapidoid.pojo.PojoDispatchException;
import org.rapidoid.pojo.PojoDispatcher;
import org.rapidoid.pojo.PojoHandlerNotFoundException;
import org.rapidoid.pojo.PojoRequest;
import org.rapidoid.pojo.impl.DispatchReqKind;
import org.rapidoid.pojo.web.WebReq;
import org.rapidoid.u.U;
import org.rapidoid.util.UTILS;

@Authors("Nikolche Mihajlovski")
@Since("5.0.10")
public class PojoHandler extends FastParamsAwareHttpHandler {

	private final PojoDispatcher dispatcher;

	public PojoHandler(FastHttp http, PojoDispatcher dispatcher) {
		super(http, null, null);
		this.dispatcher = dispatcher;
	}

	@Override
	protected Object doHandle(Channel channel, boolean isKeepAlive, Req req) throws Exception {
		Resp resp = req.response();

		DispatchResult dispatched = doDispatch(dispatcher, new WebReq(req));

		if (dispatched != null) {
			Object result = dispatched.getResult();
			Map<String, Object> config = dispatched.getConfig();

			if (dispatched.getKind() == DispatchReqKind.SERVICE) {
				return resp.contentType(MediaType.JSON_UTF_8).content(result);
			}

			resp.contentType(MediaType.HTML_UTF_8);

			boolean isRaw = config != null && Cls.bool(config.get("raw"));
			return isRaw ? resp.body(UTILS.toBytes(result)) : resp.content(result);
		} else {
			return null;
		}
	}

	private DispatchResult doDispatch(PojoDispatcher dispatcher, PojoRequest req) {
		try {
			return dispatcher.dispatch(req);
		} catch (PojoHandlerNotFoundException e) {
			// / just ignore, will try to dispatch a page next...
			return null;
		} catch (PojoDispatchException e) {
			throw U.rte("Dispatch error!", e);
		}
	}

}
