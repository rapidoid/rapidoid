package org.rapidoid.wire.app;

/*
 * #%L
 * rapidoid-wire
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Autocreate;
import org.rapidoid.annotation.Init;
import org.rapidoid.annotation.Inject;
import org.rapidoid.annotation.Since;
import org.rapidoid.wire.Logger;

@Autocreate
@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class App {

	static boolean READY = false;

	@Inject
	Logger logger;

	@Inject
	PersonService personService;

	@Inject
	PersonServiceImpl personService2;

	@Inject
	BookService bookService;

	@Init
	public void callThisWhenReady() {
		READY = true;
	}

}
