package org.rapidoid.render;

/*
 * #%L
 * rapidoid-render
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

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

import java.util.List;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class TemplateFormattingTest extends AbstractRenderTest {

	@Test
	public void testLinesRendering() {
		verify("lines", Render.file("lines.txt").model(model()));
	}

	@Test
	public void testLinesRendering2() {
		verify("extra-lines", Render.file("extra-lines.txt").model(model()));
	}

	@Test
	public void testWordsRendering() {
		verify("words", Render.file("words.txt").model(model()));
	}

	@Test
	public void partialsShouldntAffectLines() {
		verify("partials", Render.file("partials.txt").model(model()));
	}

	private Map<String, List<Integer>> model() {
		return U.map("items", U.list(1, 2, 3));
	}

}
