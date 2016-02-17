package org.rapidoid.sql;

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
import org.rapidoid.config.Conf;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class JDBCConfig {

	public static String url() {
		return Conf.nested("jdbc", "url");
	}

	public static String driver() {
		String driver = Conf.nested("jdbc", "driver");

		if (driver == null && !U.isEmpty(url())) {
			driver = inferDriverFromUrl(url());
		}

		return driver;
	}

	public static String username() {
		return Conf.nested("jdbc", "username");
	}

	public static String password() {
		return Conf.nested("jdbc", "password");
	}

	public static String inferDriverFromUrl(String url) {
		if (url.startsWith("jdbc:mysql:")) {
			return "com.mysql.jdbc.Driver";
		} else if (url.startsWith("jdbc:h2:")) {
			return "org.hibernate.dialect.H2Dialect";
		} else if (url.startsWith("jdbc:hsqldb:")) {
			return "org.hsqldb.jdbc.JDBCDriver";
		}

		return null;
	}

}
