package org.rapidoid.commons;

/*
 * #%L
 * rapidoid-commons
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
import org.rapidoid.lambda.Mapper;
import org.rapidoid.test.AbstractCommonsTest;

import java.util.regex.Pattern;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class StringRewriterTest extends AbstractCommonsTest {

	private Pattern maskRegex = Pattern.compile(StringRewriter.MASK_QUOTES);

	private Pattern regex = Pattern.compile("\\d+");

	@Test
	public void testMask() {
		checkMask("", "");
		checkMask("x", "x");
		checkMask("фк90ф", "фк90ф");
		checkMask("  ", "  ");
		checkMask("a 'b' c", "a \u0000\u0000\u0000 c");
		checkMask("''", "\u0000\u0000");
		checkMask("''-''", "\u0000\u0000-\u0000\u0000");
		checkMask("''''", "\u0000\u0000\u0000\u0000");
		checkMask("a '\u0000' b", "a \u0000\u0000\u0000 b");
	}

	@Test
	public void testReplace() {
		checkRewrite("", "");
		checkRewrite("x", "x");
		checkRewrite("3", "-");
		checkRewrite("55", "-");
		checkRewrite("x123y", "x-y");
		checkRewrite("x-123-y", "x---y");
		checkRewrite("abc 123 xy 3 00", "abc - xy - -");
	}

	@Test
	public void testRewriter() {
		String[] esc = {StringRewriter.MASK_QUOTES, StringRewriter.MASK_DOUBLE_QUOTES};

		StringRewriter rewriter = new StringRewriter(esc, "\\w+");

		Mapper<String[], String> replacer = new Mapper<String[], String>() {
			@Override
			public String map(String[] groups) throws Exception {
				return "<" + groups[0].toUpperCase() + ">";
			}
		};

		eq(rewriter.rewrite("", replacer), "");
		eq(rewriter.rewrite("a", replacer), "<A>");
		eq(rewriter.rewrite("*()", replacer), "*()");
		eq(rewriter.rewrite("'x'", replacer), "'x'");
		eq(rewriter.rewrite("'x", replacer), "'<X>");
		eq(rewriter.rewrite("x'", replacer), "<X>'");

		eq(rewriter.rewrite("aa 'b c dd' xy", replacer), "<AA> 'b c dd' <XY>");
		eq(rewriter.rewrite("aa \"b c dd\" xy", replacer), "<AA> \"b c dd\" <XY>");
		eq(rewriter.rewrite("a-b-c-0-1-2", replacer), "<A>-<B>-<C>-<0>-<1>-<2>");
		eq(rewriter.rewrite("a-'b'-c-'0'-1-'2", replacer), "<A>-'b'-<C>-'0'-<1>-'<2>");
	}

	private void checkMask(String s, String mask) {
		String masked = Str.mask(s, maskRegex);
		eq(masked, mask);
		eq(masked.length(), s.length());
	}

	private void checkRewrite(String s, String repl) {
		String mask = Str.mask(s, maskRegex);

		String s2 = Str.replace(s, mask, regex, new Mapper<String[], String>() {
			@Override
			public String map(String[] src) throws Exception {
				return "-";
			}
		});

		eq(s2, repl);
	}

}
