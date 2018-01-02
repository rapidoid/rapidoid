/*-
 * #%L
 * rapidoid-http-fast
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
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

package org.rapidoid.http.impl;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ctx.Contextual;
import org.rapidoid.gui.reqinfo.AbstractReqInfo;
import org.rapidoid.http.HttpVerb;
import org.rapidoid.http.Req;
import org.rapidoid.io.Upload;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Authors("Nikolche Mihajlovski")
@Since("5.0.4")
public class RapidoidReqInfo extends AbstractReqInfo {

	@Override
	public String verb() {
		return req().verb();
	}

	@Override
	public String path() {
		return req().path();
	}

	@Override
	public String uri() {
		return req().uri();
	}

	@Override
	public String host() {
		return req().host();
	}

	@Override
	public Map<String, Object> data() {
		return req().data();
	}

	@Override
	public Map<String, String> params() {
		return req().params();
	}

	@Override
	public Map<String, Object> posted() {
		return req().posted();
	}

	@Override
	public Map<String, List<Upload>> files() {
		return req().files();
	}

	@Override
	public Map<String, String> headers() {
		return req().headers();
	}

	@Override
	public Map<String, String> cookies() {
		return req().cookies();
	}

	@Override
	public Map<String, Object> attrs() {
		return req().attrs();
	}

	@Override
	public Map<String, Serializable> token() {
		return req().token();
	}

	@Override
	public boolean exists() {
		return Contextual.hasContext();
	}

	@Override
	public String username() {
		return Contextual.username();
	}

	@Override
	public Set<String> roles() {
		return Contextual.roles();
	}

	@Override
	public String zone() {
		return req().zone();
	}

	@Override
	public String contextPath() {
		return req().contextPath();
	}

	@Override
	public String view() {
		return req().response().view();
	}

	@Override
	public void setHeader(String name, String value) {
		req().response().header(name, value);
	}

	@Override
	public boolean hasRoute(HttpVerb verb, String uri) {
		ReqImpl reqq = (ReqImpl) req();
		return reqq.http().hasRouteOrResource(verb, uri);
	}

	private Req req() {
		return Contextual.request();
	}

}
