package org.rapidoid.gui;

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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.base.AbstractCommand;
import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.ButtonTag;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("2.3.0")
public class Btn extends AbstractCommand<Btn> {

	private volatile Object[] contents = {};

	private volatile String kind = "default";

	private volatile String go;

	private volatile String class_;

	private volatile String confirm;

	private volatile String size;

	@Override
	protected Tag render() {
		handleEventIfMatching();

		String cls = U.or(class_, "btn btn-" + kind);

		if (size != null) {
			cls += " btn-" + size;
		}

		if (go != null && !hasHandler() && confirm() == null) {
			return a(contents).href(go).class_(cls);
		}

		ButtonTag btn = GUI.button(contents).type("button").class_(cls);

		if (command() != null) {
			btn = btn.cmd(command(), cmdArgs());
		}

		if (confirm() != null) {
			btn = btn.attr("data-confirm", confirm());
		}

		if (go() != null) {
			btn = btn.attr("data-go", go());
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

	public String go() {
		return go;
	}

	public Btn go(String linkTo) {
		this.go = linkTo;
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
		handler(onClickHandler);
		return this;
	}

	public Btn onSuccess(Runnable onSuccessHandler) {
		handlerOnSuccess(onSuccessHandler);
		return this;
	}

	public Btn onError(Runnable onErrorHandler) {
		handlerOnError(onErrorHandler);
		return this;
	}

	public String confirm() {
		return confirm;
	}

	public Btn confirm(String confirm) {
		this.confirm = confirm;
		return this;
	}

	public Btn smallest() {
		this.size = "xs";
		return this;
	}

	public Btn small() {
		this.size = "sm";
		return this;
	}

	public Btn large() {
		this.size = "lg";
		return this;
	}

}
