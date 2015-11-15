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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.base.AbstractWidget;
import org.rapidoid.http.fast.Req;

@Authors("Nikolche Mihajlovski")
@Since("2.3.1")
public class Debug extends AbstractWidget {

	@Override
	protected Object render() {
		return multi(sessionPanel(), localPanel());
	}

	protected Panel sessionPanel() {
		Req req = ctx().exchange();
		return panel(grid(req.params())).header("URL parameters");
	}

	protected Panel localPanel() {
		Req req = ctx().exchange();
		return panel(grid(req.posted())).header("Posted data");
	}

}
