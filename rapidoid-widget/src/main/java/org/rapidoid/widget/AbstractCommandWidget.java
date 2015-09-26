package org.rapidoid.widget;

/*
 * #%L
 * rapidoid-widget
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
import java.util.List;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.util.D;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("4.2.0")
public abstract class AbstractCommandWidget<W extends AbstractCommandWidget<?>> extends AbstractWidget {

	private String command;

	private Object[] cmdArgs;

	private Runnable handler;

	private boolean handled;

	@SuppressWarnings("unchecked")
	public W command(String cmd, Object... cmdArgs) {
		this.command = cmd;
		this.cmdArgs = cmdArgs;
		handleEventIfReady();
		return (W) this;
	}

	protected void handleEventIfReady() {
		if (!handled && handler != null && command != null) {
			HttpExchange x = ctx();
			if (ctx().isPostReq()) {
				String event = x.data("_event", null);

				if (!U.isEmpty(event) && U.eq(event, command)) {
					List<Object> argList = x.data("_args", null);
					Object[] args = argList != null ? U.array(argList) : new Object[0];
					D.print(args);
					D.print(cmdArgs);

					if (Arrays.equals(args, cmdArgs)) {
						System.out.println("EQ");
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
