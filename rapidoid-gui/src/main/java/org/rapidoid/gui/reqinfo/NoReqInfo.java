package org.rapidoid.gui.reqinfo;

/*
 * #%L
 * rapidoid-gui
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

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@SuppressWarnings("unchecked")
@Authors("Nikolche Mihajlovski")
@Since("5.0.5")
public class NoReqInfo extends AbstractReqInfo {

	@SuppressWarnings({ "rawtypes" })
	private static final Map EMPTY = Collections.EMPTY_MAP;

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
		return EMPTY;
	}

	@Override
	public Map<String, String> params() {
		return EMPTY;
	}

	@Override
	public Map<String, Object> posted() {
		return EMPTY;
	}

	@Override
	public Map<String, byte[]> files() {
		return EMPTY;
	}

	@Override
	public Map<String, String> headers() {
		return EMPTY;
	}

	@Override
	public Map<String, String> cookies() {
		return EMPTY;
	}

	@Override
	public Map<String, Object> attrs() {
		return EMPTY;
	}

	@Override
	public String username() {
		return null;
	}

	@Override
	public Set<String> roles() {
		return Collections.EMPTY_SET;
	}

}
