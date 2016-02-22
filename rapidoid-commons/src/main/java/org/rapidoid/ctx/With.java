package org.rapidoid.ctx;

/*
 * #%L
 * rapidoid-commons
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

import java.util.Map;
import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("2.5.0")
public class With {

	public static CtxData username(String username) {
		return new CtxData().username(username);
	}

	public static CtxData roles(Set<String> roles) {
		return new CtxData().roles(roles);
	}

	public static CtxData listener(org.rapidoid.ctx.JobStatusListener listener) {
		return new CtxData().listener(listener);
	}

	public static CtxData persister(Object persister) {
		return new CtxData().persister(persister);
	}

	public static CtxData host(String host) {
		return new CtxData().host(host);
	}

	public static CtxData uri(String uri) {
		return new CtxData().uri(uri);
	}

	public static CtxData verb(String verb) {
		return new CtxData().verb(verb);
	}

	public static CtxData data(Map<String, Object> data) {
		return new CtxData().data(data);
	}

	public static CtxData session(Map<String, java.io.Serializable> session) {
		return new CtxData().session(session);
	}

	public static CtxData extras(Map<String, Object> extras) {
		return new CtxData().extras(extras);
	}

}
