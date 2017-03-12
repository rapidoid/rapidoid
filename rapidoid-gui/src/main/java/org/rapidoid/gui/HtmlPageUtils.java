package org.rapidoid.gui;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.reqinfo.IReqInfo;
import org.rapidoid.gui.reqinfo.ReqInfo;
import org.rapidoid.http.HttpVerb;
import org.rapidoid.render.Getter;
import org.rapidoid.util.Msc;

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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class HtmlPageUtils extends RapidoidThing {

	static final Getter HAS_PAGE = new Getter() {
		@Override
		public Object get(String page) {
			return ReqInfo.get().hasRoute(HttpVerb.GET, strToUri(page));
		}
	};

	static final Getter HAS_SPECIAL_PAGE = new Getter() {
		@Override
		public Object get(String page) {
			return ReqInfo.get().hasRoute(HttpVerb.GET, Msc.specialUri(page));
		}
	};

	static final Getter HAS_ROLE = new Getter() {
		@Override
		public Object get(String role) {
			return HtmlPageUtils.hasRole(role);
		}
	};

	static final Getter HAS_PATH = new Getter() {
		@Override
		public Object get(String path) {
			return HtmlPageUtils.hasPath(path);
		}
	};

	static final Getter HAS_ZONE = new Getter() {
		@Override
		public Object get(String zone) {
			return HtmlPageUtils.hasZone(zone);
		}
	};

	static boolean hasRole(String role) {
		IReqInfo req = ReqInfo.get();
		return req.roles().contains(role);
	}

	static boolean hasPath(String path) {
		IReqInfo req = ReqInfo.get();
		return uriToStr(req.path()).equals(path);
	}

	static boolean hasZone(String zone) {
		IReqInfo req = ReqInfo.get();
		return req.zone().equals(zone);
	}

	private static String uriToStr(String path) {
		return path.replace('/', '$');
	}

	private static String strToUri(String path) {
		return path.replace('$', '/');
	}

}
