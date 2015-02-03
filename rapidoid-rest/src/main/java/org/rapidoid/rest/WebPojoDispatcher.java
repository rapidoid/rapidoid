package org.rapidoid.rest;

/*
 * #%L
 * rapidoid-rest
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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
import org.rapidoid.http.HttpExchange;
import org.rapidoid.pojo.PojoRequest;
import org.rapidoid.pojo.impl.PojoDispatcherImpl;
import org.rapidoid.util.Cls;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class WebPojoDispatcher extends PojoDispatcherImpl {

	public WebPojoDispatcher(Map<String, Class<?>> services) {
		super(services);
	}

	public WebPojoDispatcher(Class<?>... classes) {
		this(Cls.classMap(U.list(classes)));
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
