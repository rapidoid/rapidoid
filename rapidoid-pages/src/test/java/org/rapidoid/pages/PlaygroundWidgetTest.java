package org.rapidoid.pages;

/*
 * #%L
 * rapidoid-pages
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import org.rapidoid.html.Tag;
import org.rapidoid.html.TagContext;
import org.rapidoid.html.Tags;
import org.testng.annotations.Test;

public class PlaygroundWidgetTest extends PagesTestCommons {

	private static final String ATTRS = "[^>]*?";

	@Test
	public void testPlaygroundWidget() {
		TagContext ctx = Tags.context();

		Tag<?> play = PlaygroundWidget.pageContent(null);
		print(ctx, play);

		hasRegex(ctx, play, "<table class=\"table" + ATTRS + ">");

		hasRegex(ctx, play, "<button[^>]*?>\\-</button>");
		hasRegex(ctx, play, "<span[^>]*?>10</span>");
		hasRegex(ctx, play, "<button[^>]*?>\\+</button>");

		hasRegex(ctx, play, "<input [^>]*?css=\"border: 1px;\">");
	}

}
