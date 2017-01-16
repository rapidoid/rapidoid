package org.rapidoid.gui;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.base.AbstractWidget;
import org.rapidoid.html.Tag;
import org.rapidoid.u.U;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
@Since("2.0.0")
public class Highlight extends AbstractWidget<Highlight> {

	private String text;
	private String regex;

	public Highlight(String text, String regex) {
		this.text = text;
		this.regex = regex;
	}

	@Override
	protected Tag render() {
		return regex != null ? complexHighlight() : simpleHighlight();
	}

	protected Tag simpleHighlight() {
		return !U.isEmpty(text) ? span(text).class_("highlight") : span(text);
	}

	protected Tag complexHighlight() {
		List<Object> parts = U.list();
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(text);

		int end = 0;
		while (m.find()) {
			String match = m.group();
			parts.add(text.substring(end, m.start()));
			parts.add(GUI.highlight(match));
			end = m.end();
		}

		parts.add(text.substring(end));

		return span(parts);
	}

}
