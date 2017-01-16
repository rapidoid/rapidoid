package org.rapidoid.gui.reqinfo;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.HttpVerb;
import org.rapidoid.io.Upload;
import org.rapidoid.u.U;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * #%L
 * rapidoid-gui
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

@SuppressWarnings("unchecked")
@Authors("Nikolche Mihajlovski")
@Since("5.0.5")
public class NoReqInfo extends AbstractReqInfo {

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public boolean isGetReq() {
		return true;
	}

	@Override
	public String verb() {
		return null;
	}

	@Override
	public String path() {
		return null;
	}

	@Override
	public String uri() {
		return null;
	}

	@Override
	public String host() {
		return null;
	}

	@Override
	public Map<String, Object> data() {
		return U.map();
	}

	@Override
	public Map<String, String> params() {
		return U.map();
	}

	@Override
	public Map<String, Object> posted() {
		return U.map();
	}

	@Override
	public Map<String, List<Upload>> files() {
		return U.map();
	}

	@Override
	public Map<String, String> headers() {
		return U.map();
	}

	@Override
	public Map<String, String> cookies() {
		return U.map();
	}

	@Override
	public Map<String, Object> attrs() {
		return U.map();
	}

	@Override
	public Map<String, Serializable> token() {
		return U.map();
	}

	@Override
	public String username() {
		return null;
	}

	@Override
	public Set<String> roles() {
		return U.set();
	}

	@Override
	public String zone() {
		return "main";
	}

	@Override
	public String contextPath() {
		return "";
	}

	@Override
	public boolean hasRoute(HttpVerb verb, String uri) {
		return false;
	}

	@Override
	public String view() {
		return null;
	}

	@Override
	public void setHeader(String name, String value) {
	}

}
