package org.rapidoid.gui.base;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.GUI;
import org.rapidoid.gui.reqinfo.IReqInfo;
import org.rapidoid.gui.reqinfo.ReqInfo;
import org.rapidoid.u.U;

import java.util.Arrays;

/*
 * #%L
 * rapidoid-gui
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
@Since("4.2.0")
public abstract class AbstractCommand<W extends AbstractCommand<?>> extends AbstractWidget<W> {

	private String command;

	private String[] cmdArgs;

	private Runnable handler;

	private Runnable handlerOnSuccess;

	private Runnable handlerOnError;

	private boolean handled;

	@SuppressWarnings("unchecked")
	public W command(String cmd, Object... cmdArgs) {
		this.command = cmd;
		this.cmdArgs = strArgs(cmdArgs);

		return (W) this;
	}

	private String[] strArgs(Object[] args) {
		String[] strs = new String[args.length];

		for (int i = 0; i < args.length; i++) {
			strs[i] = U.str(args[i]).replace("'", "`");
		}

		return strs;
	}

	public boolean clicked() {
		if (command != null) {
			IReqInfo req = ReqInfo.get();

			if (!req.isGetReq()) {
				String event = GUI.getCommand();

				if (U.notEmpty(event) && U.eq(event, command)) {
					Object[] args = new Object[cmdArgs.length];

					for (int i = 0; i < args.length; i++) {
						args[i] = U.or(req.data().get("_" + i), "");
					}

					return Arrays.equals(args, cmdArgs);
				}
			}
		}

		return false;
	}

	protected void handleEventIfMatching() {
		if (!handled && hasHandler()) {
			if (clicked()) {
				handled = true;
				handleAction();
			}
		}
	}

	protected boolean hasHandler() {
		return handler != null || handlerOnSuccess != null || handlerOnError != null;
	}

	private void handleAction() {
		if (handler != null) {
			handler.run();
		}

		if (!GUI.hasValidationErrors()) {
			if (handlerOnSuccess != null) {
				handlerOnSuccess.run();
			}
		} else {
			if (handlerOnError != null) {
				handlerOnError.run();
			}
		}
	}

	public String command() {
		return command;
	}

	public Object[] cmdArgs() {
		return cmdArgs;
	}

	protected Runnable handler() {
		return handler;
	}

	protected AbstractCommand handler(Runnable handler) {
		this.handler = handler;
		return this;
	}

	protected Runnable handlerOnSuccess() {
		return handlerOnSuccess;
	}

	protected AbstractCommand handlerOnSuccess(Runnable handlerOnSuccess) {
		this.handlerOnSuccess = handlerOnSuccess;
		return this;
	}

	protected Runnable handlerOnError() {
		return handlerOnError;
	}

	protected AbstractCommand handlerOnError(Runnable handlerOnError) {
		this.handlerOnError = handlerOnError;
		return this;
	}
}
