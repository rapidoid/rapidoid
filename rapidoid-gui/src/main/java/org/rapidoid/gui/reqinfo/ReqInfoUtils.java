package org.rapidoid.gui.reqinfo;

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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;
import org.rapidoid.util.AppInfo;
import org.rapidoid.util.Msc;

@Authors("Nikolche Mihajlovski")
@Since("5.1.2")
public class ReqInfoUtils extends RapidoidThing {

	public static String adminUrl() {
		IReqInfo req = ReqInfo.get();
		int appPort = AppInfo.appPort;
		int adminPort = AppInfo.adminPort;
		boolean appAndAdminOnSamePort = adminPort == appPort;

		if (U.notEmpty(req.host())) {
			String hostname = req.host().split(":")[0];

			if (AppInfo.isAdminServerActive) {
				String path = req.contextPath() + Msc.specialUriPrefix();
				return appAndAdminOnSamePort ? path : "http://" + hostname + ":" + adminPort + path;
			}
		}

		return null;
	}

	public static String appUrl() {
		IReqInfo req = ReqInfo.get();
		int appPort = AppInfo.appPort;
		int adminPort = AppInfo.adminPort;
		boolean appAndAdminOnSamePort = adminPort == appPort;

		if (U.notEmpty(req.host())) {
			String hostname = req.host().split(":")[0];

			if (AppInfo.isAppServerActive) {
				String path = req.contextPath() + "/";
				return appAndAdminOnSamePort ? path : "http://" + hostname + ":" + appPort + path;
			}
		}

		return null;
	}

}
