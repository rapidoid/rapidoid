package org.rapidoid;

/*
 * #%L
 * rapidoid-http-server
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.config.Config;
import org.rapidoid.crypto.Crypto;
import org.rapidoid.util.AnsiColor;
import org.rapidoid.util.Msc;

@Authors("Nikolche Mihajlovski")
@Since("5.2.3")
public class AuthBootstrap extends RapidoidThing {

	private static volatile String randomAdminPassword;

	public static synchronized void bootstrapAdminCredentials() {
		Config admin = Conf.USERS.sub("admin");

		if (!admin.has("password") && !admin.has("hash")) {
			String pass = randomAdminPassword();
			admin.set("password", pass);

			String maybePass = "" + Msc.maybeMasked(pass);
			Msc.logSection(AnsiColor.lightBlue("ADMIN CREDENTIALS:") + " username = admin, password = " + maybePass);
		}
	}

	private static synchronized String randomAdminPassword() {
		if (randomAdminPassword == null) {
			randomAdminPassword = Crypto.randomStr(16);
		}

		return randomAdminPassword;
	}

}
