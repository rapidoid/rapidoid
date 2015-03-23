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
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("2.4.0")
public class CardWidget extends AbstractWidget {

	private Object[] contents = {};

	private Object[] controls = {};

	private Object header;

	@Override
	protected Object render() {
		Tag caption = h6(header).class_("rapidoid-card-caption");
		Tag right = !U.isEmpty(controls) ? right(spaced(controls)) : null;
		Tag header = div(right, caption).class_("rapidoid-card-header");

		Tag cont = div(contents).class_("rapidoid-card-content");
		return div(header, cont).class_("rapidoid-card");
	}

	public Object[] contents() {
		return contents;
	}

	public CardWidget contents(Object... contents) {
		this.contents = contents;
		return this;
	}

	public Object[] controls() {
		return controls;
	}

	public CardWidget controls(Object... controls) {
		this.controls = controls;
		return this;
	}

	public Object header() {
		return header;
	}

	public CardWidget header(Object header) {
		this.header = header;
		return this;
	}

}
