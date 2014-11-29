package org.rapidoid.html.impl;

/*
 * #%L
 * rapidoid-html
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

import org.rapidoid.html.Cmd;
import org.rapidoid.html.TagContext;
import org.rapidoid.util.U;
import org.rapidoid.var.Var;

public class TagContextImpl implements TagContext, Serializable {

	private static final long serialVersionUID = 4007586215607855031L;

	private final Map<Integer, Var<Object>> bindings = U.map();

	private final Map<Integer, Cmd> commands = U.map();

	@Override
	public int newBinding(Var<Object> binding) {
		int hnd;
		do {
			hnd = Math.abs(U.rnd());
		} while (bindings.containsKey(hnd));

		bindings.put(hnd, binding);

		return hnd;
	}

	@Override
	public int newCommand(Cmd cmd) {
		int hnd;
		do {
			hnd = Math.abs(U.rnd());
		} while (commands.containsKey(hnd));

		commands.put(hnd, cmd);

		return hnd;
	}

	@Override
	public void emitValues(final Map<Integer, Object> values) {

		for (Entry<Integer, Object> e : values.entrySet()) {
			Var<Object> var = bindings.get(e.getKey());

			U.must(var != null, "Invalid input handle: h_%s", e.getKey());

			if (var != null) {
				var.set(e.getValue());
			}
		}
	}

	@Override
	public Cmd getEventCmd(int eventId) {
		Cmd cmd = commands.get(eventId);
		return cmd;
	}

	@Override
	public String toString() {
		return "TagContextImpl [bindings=" + bindings + ", commands=" + commands + "]";
	}

}
