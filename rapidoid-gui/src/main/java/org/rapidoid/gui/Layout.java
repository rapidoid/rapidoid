package org.rapidoid.gui;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.base.AbstractWidget;
import org.rapidoid.html.Tag;
import org.rapidoid.u.U;

import java.util.List;

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
@Since("2.3.0")
public class Layout extends AbstractWidget<Layout> {

	private Object[] contents = {};

	private int cols = 1;

	@Override
	protected Object render() {
		List<Tag> rows = U.list();

		Tag row = GUI.row().class_("row row-separated");

		int n = 0;
		int colSize = 12 / cols;

		for (Object item : contents) {
			n++;
			if (n == cols + 1) {
				n = 1;
				rows.add(row);
				row = GUI.row().class_("row row-separated");
			}
			row = row.append(GUI.col_(colSize, item));
		}

		if (!row.isEmpty()) {
			rows.add(row);
		}

		return U.arrayOf(Tag.class, rows);
	}

	public Object[] contents() {
		return contents;
	}

	public Layout contents(Object... contents) {
		this.contents = contents;
		return this;
	}

	public int cols() {
		return cols;
	}

	public Layout cols(int cols) {
		this.cols = cols;
		return this;
	}

}
