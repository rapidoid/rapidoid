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
import org.rapidoid.gui.base.AbstractWidget;
import org.rapidoid.gui.reqinfo.ReqInfo;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("2.3.1")
public class Debug extends AbstractWidget<Debug> {

	public static final Debug INSTANCE = new Debug();

	@Override
	protected Object render() {
		return GUI.row(GUI.col4(params()), GUI.col4(posted()), GUI.col4(data()));
	}

	protected Panel params() {
		return GUI.panel(GUI.grid(ReqInfo.get().params())).header(U.list("URL parameters ", GUI.code("(Req#params)")));
	}

	protected Panel posted() {
		return GUI.panel(GUI.grid(ReqInfo.get().posted())).header(U.list("Posted data ", GUI.code("(Req#posted)")));
	}

	protected Panel data() {
		return GUI.panel(GUI.grid(ReqInfo.get().data())).header(U.list("All data ", GUI.code("(Req#data)")));
	}

}
