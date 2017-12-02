package org.rapidoid.html;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.var.Var;

import java.io.Serializable;
import java.util.Arrays;

/*
 * #%L
 * rapidoid-html
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
@Since("2.0.0")
public class Cmd extends RapidoidThing implements Serializable {

	private static final long serialVersionUID = -8114841206759431125L;

	public final String name;

	public final Object[] args;

	public Cmd(String cmd, Object[] args) {
		this.name = cmd;
		this.args = args;

		for (int i = 0; i < args.length; i++) {
			if (args[i] instanceof Var<?>) {
				args[i] = ((Var<?>) args[i]).name();
			}
		}
	}

	@Override
	public String toString() {
		return "Cmd [name=" + name + ", args=" + Arrays.toString(args) + "]";
	}

}
