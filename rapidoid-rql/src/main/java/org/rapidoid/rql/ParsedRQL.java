package org.rapidoid.rql;

/*
 * #%L
 * rapidoid-rql
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
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("4.0.0")
public class ParsedRQL {

	public final String command;

	public final String target;

	public final Object[] args;

	public ParsedRQL(String command, String target, Object... args) {
		this.command = command;
		this.target = target;
		this.args = args;
	}

}
