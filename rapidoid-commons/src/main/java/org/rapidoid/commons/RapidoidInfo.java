package org.rapidoid.commons;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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

@Authors("Nikolche Mihajlovski")
@Since("5.0.4")
public class RapidoidInfo extends RapidoidThing {

	private static final Properties PROPS = new Properties();

	private static final String VERSION;

	static {
		try {
			InputStream res = Thread.currentThread().getContextClassLoader().getResourceAsStream("rapidoid.properties");
			if (res != null) {
				PROPS.load(res);
			}
		} catch (IOException e) {
			throw U.rte(e);
		}

		VERSION = PROPS.getProperty("version");
	}

	public static String version() {
		return VERSION;
	}

}
