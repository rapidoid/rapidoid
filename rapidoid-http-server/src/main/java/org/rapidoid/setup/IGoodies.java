package org.rapidoid.setup;

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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public interface IGoodies {

	void overview(Setup setup);

	void application(Setup setup);

	void lifecycle(Setup setup);

	void processes(Setup setup);

	void dbAdmin(Setup setup);

	void manageables(Setup setup);

	void jmx(Setup setup);

	void metrics(Setup setup);

	void deploy(Setup setup);

	void ping(Setup setup);

	void auth(Setup setup);

	void oauth(Setup setup);

	void adminCenter(Setup setup);

	void entities(Setup setup);

	void welcome(Setup setup);

	void status(Setup setup);

	void discovery(Setup setup);

	void echo(Setup setup);

}
