package org.rapidoid.widget;

/*
 * #%L
 * rapidoid-widget
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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
import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.ButtonTag;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("2.3.0")
public class ButtonWidget extends AbstractWidget {

	private Object[] contents = {};

	private String kind = "default";

	private String linkTo;

	private String class_;

	private String command;

	private Object[] cmdArgs;

	public ButtonWidget() {}

	@Override
	protected Tag render() {
		String cls = U.or(class_, "btn btn-" + kind);
		ButtonTag btn = button(contents).type("button").class_(cls);

		if (command != null) {
			btn = btn.cmd(command, cmdArgs);
		} else if (linkTo != null) {
			btn = btn.navigate(linkTo);
		}

		return btn;
	}

	public Object[] contents() {
		return contents;
	}

	public ButtonWidget contents(Object... contents) {
		this.contents = contents;
		return this;
	}

	public ButtonWidget primary() {
		kind = "primary";
		return this;
	}

	public ButtonWidget success() {
		kind = "success";
		return this;
	}

	public ButtonWidget info() {
		kind = "info";
		return this;
	}

	public ButtonWidget warning() {
		kind = "warning";
		return this;
	}

	public ButtonWidget danger() {
		kind = "danger";
		return this;
	}

	public ButtonWidget command(String cmd, Object... cmdArgs) {
		this.command = cmd;
		this.cmdArgs = cmdArgs;
		return this;
	}

	public String command() {
		return command;
	}

	public Object[] cmdArgs() {
		return cmdArgs;
	}

	public String kind() {
		return kind;
	}

	public ButtonWidget kind(String kind) {
		this.kind = kind;
		return this;
	}

	public String linkTo() {
		return linkTo;
	}

	public ButtonWidget linkTo(String linkTo) {
		this.linkTo = linkTo;
		return this;
	}

	public String class_() {
		return class_;
	}

	public ButtonWidget class_(String class_) {
		this.class_ = class_;
		return this;
	}

}
