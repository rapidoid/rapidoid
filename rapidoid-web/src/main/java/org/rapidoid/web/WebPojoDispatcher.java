package org.rapidoid.web;

/*
 * #%L
 * rapidoid-web
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import org.rapidoid.pojo.PojoRequest;
import org.rapidoid.pojo.impl.PojoDispatcherImpl;
import org.rapidoid.util.U;

import com.rapidoid.http.HttpExchange;

public class WebPojoDispatcher extends PojoDispatcherImpl {

	public WebPojoDispatcher(Object... services) {
		super(services);
	}

	@Override
	protected boolean isCustomType(Class<?> type) {
		return type.equals(HttpExchange.class) || super.isCustomType(type);
	}

	@Override
	protected Object getCustomArg(PojoRequest request, Class<?> type, String[] parts, int paramsFrom, int paramsSize) {
		if (type.equals(HttpExchange.class)) {
			U.must(request instanceof WebReq);
			WebReq webReq = (WebReq) request;
			return webReq.getExchange();
		} else {
			return super.getCustomArg(request, type, parts, paramsFrom, paramsSize);
		}
	}

}
