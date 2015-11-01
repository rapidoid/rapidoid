package org.rapidoid.gui;

/*
 * #%L
 * rapidoid-gui
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

import java.util.Arrays;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("4.2.0")
public abstract class AbstractCommandWidget<W extends AbstractCommandWidget<?>> extends AbstractWidget {

	private String command;

	private String[] cmdArgs;

	private Runnable handler;

	private boolean handled;

	@SuppressWarnings("unchecked")
	public W command(String cmd, Object... cmdArgs) {
		this.command = cmd;
		this.cmdArgs = strArgs(cmdArgs);

		handleEventIfReady();

		return (W) this;
	}

	private String[] strArgs(Object[] args) {
		String[] strs = new String[args.length];

		for (int i = 0; i < args.length; i++) {
			strs[i] = U.str(args[i]).replace("'", "`");
		}

		return strs;
	}

	protected void handleEventIfReady() {
		if (!handled && handler != null && command != null) {
			Ctx ctx = ctx();
			Map<String, Object> data = ctx.data();

			if ("POST".equalsIgnoreCase(ctx.verb())) {
				String event = U.str(data.get("_cmd"));

				if (!U.isEmpty(event) && U.eq(event, command)) {

					Object[] args = new Object[cmdArgs.length];
					for (int i = 0; i < args.length; i++) {
						args[i] = U.get(data, "_" + i, "");
					}

					if (Arrays.equals(args, cmdArgs)) {
						handled = true;
						handler.run();
					}
				}
			}
		}
	}

	public String command() {
		return command;
	}

	public Object[] cmdArgs() {
		return cmdArgs;
	}

	protected void setHandler(Runnable handler) {
		this.handler = handler;
		handleEventIfReady();
	}

}
