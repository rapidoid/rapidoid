package org.rapidoid.gui.reqinfo;

/*
 * #%L
 * rapidoid-gui
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
import java.util.Set;

import org.rapidoid.ctx.Ctxs;
import org.rapidoid.ctx.UserInfo;
import org.rapidoid.http.Req;
import org.rapidoid.u.U;

public class RapidoidReqInfo extends AbstractReqInfo {

	private Req req() {
		return Ctxs.ctx().exchange();
	}

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
	public Map<String, byte[]> files() {
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
	public String username() {
		return Ctxs.ctx().username();
	}

	@Override
	public Set<String> roles() {
		UserInfo user = Ctxs.ctx().user();
		return U.set(U.safe(user != null ? user.roles : null));
	}

}
