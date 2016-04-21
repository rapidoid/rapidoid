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
import org.rapidoid.gui.base.AbstractWidget;
import org.rapidoid.gui.reqinfo.ReqInfo;

@Authors("Nikolche Mihajlovski")
@Since("2.3.1")
public class Debug extends AbstractWidget<Debug> {

	@Override
	protected Object render() {
		return GUI.multi(sessionPanel(), localPanel());
	}

	protected Panel sessionPanel() {
		return GUI.panel(GUI.grid(ReqInfo.get().params())).header("URL parameters");
	}

	protected Panel localPanel() {
		return GUI.panel(GUI.grid(ReqInfo.get().posted())).header("Posted data");
	}

}
