package org.rapidoid.gui;

/*
 * #%L
 * rapidoid-gui
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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
import org.rapidoid.gui.base.AbstractCommand;
import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.ButtonTag;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("2.3.0")
public class Btn extends AbstractCommand<Btn> {

	private Object[] contents = {};

	private String kind = "default";

	private String href;

	private String class_;

	@Override
	protected Tag render() {
		handleEventIfMatching();

		String cls = U.or(class_, "btn btn-" + kind);

		if (href != null) {
			return a(contents).href(href).class_(cls);
		}

		ButtonTag btn = button(contents).type("button").class_(cls);

		if (command() != null) {
			btn = btn.cmd(command(), cmdArgs());
		}

		return btn;
	}

	public Object[] contents() {
		return contents;
	}

	public Btn contents(Object... contents) {
		this.contents = contents;
		return this;
	}

	public Btn primary() {
		kind = "primary";
		return this;
	}

	public Btn success() {
		kind = "success";
		return this;
	}

	public Btn info() {
		kind = "info";
		return this;
	}

	public Btn warning() {
		kind = "warning";
		return this;
	}

	public Btn danger() {
		kind = "danger";
		return this;
	}

	public String kind() {
		return kind;
	}

	public Btn kind(String kind) {
		this.kind = kind;
		return this;
	}

	public String href() {
		return href;
	}

	public Btn href(String linkTo) {
		this.href = linkTo;
		return this;
	}

	public String class_() {
		return class_;
	}

	public Btn class_(String class_) {
		this.class_ = class_;
		return this;
	}

	public Btn onClick(Runnable onClickHandler) {
		setHandler(onClickHandler);
		return this;
	}

}
