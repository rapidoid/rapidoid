package org.rapidoid.gui.base;

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

import java.util.UUID;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.html.TagWidget;
import org.rapidoid.html.impl.TagRenderer;
import org.rapidoid.util.Constants;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public abstract class AbstractWidget extends BootstrapWidgets implements TagWidget<Object>, Constants {

	private Object extra;

	protected abstract Object render();

	@Override
	public final Object render(Object extra) {
		this.extra = extra;
		// TODO ignore the exchange?
		return render();
	}

	public String widgetId() {
		return getClass().getSimpleName() + "-" + UUID.randomUUID();
	}

	@Override
	public String toString() {
		return TagRenderer.get().toHTML(this, extra);
	}

}
