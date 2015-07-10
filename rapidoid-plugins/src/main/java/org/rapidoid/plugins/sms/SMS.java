package org.rapidoid.plugins.sms;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.Callback;
import org.rapidoid.plugins.Plugins;
import org.rapidoid.util.U;

/*
 * #%L
 * rapidoid-plugins
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

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class SMS {

	public static void send(String toNumber, String content, Callback<Void> callback) {
		send(U.list(toNumber), content, callback);
	}

	public static void send(Iterable<String> toNumbers, String content, Callback<Void> callback) {
		Plugins.sms().send(toNumbers, content, callback);
	}

}
