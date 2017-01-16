package org.rapidoid.render;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class TemplateParser extends RapidoidThing {

	private static final String NL = "(\\r?\\n?)";
	private static final String SP = "([\\t ]*)";

	static final String EXPR = "(\\$|@)\\{([^\\}]+?)\\}";
	static final String STAT = NL + SP + "\\{\\{([\\^!#\\?/>][^\\}]+?)\\}\\}" + SP + NL;

	static final String W = "[a-zA-Z0-9_]";
	static final String ID = "(" + W + "+(?:-" + W + "+)*)";
	static final String TAG_NAME = ID + "\\:" + ID;
	static final String TAG = "<" + TAG_NAME + "(\\s+" + W + "+=\"[^\"]*?\")*?(?:\\s?/)?>|</" + TAG_NAME + ">";

	static final Pattern TOKENIZER = Pattern.compile("(?s)(.*?)(?:" + U.join("|", EXPR, STAT) + ")");

	public static XNode parse(String s) {
		U.notNull(s, "template source");

		Stack<XNode> stack = new Stack<XNode>();

		XNode root = new XNode(XNode.OP.OP_ROOT, null);
		stack.push(root);

		Matcher m = TOKENIZER.matcher(s);

		int pos = 0;
		while (m.find()) {

			String whole = m.group(0);
			String g1 = m.group(1);

			if (!U.isEmpty(g1)) {
				addText(stack, g1);
			}

			String g2 = m.group(2);
			String g4 = m.group(6);

			if (!U.isEmpty(g2)) {

				char prefix = g2.charAt(0);
				String txt = m.group(3);
				expression(stack, prefix, txt, whole);

			} else if (!U.isEmpty(g4)) {

				char prefix = g4.charAt(0);
				String txt = g4.substring(1);

				String nl1 = m.group(4);
				assert nl1.matches(NL);

				String sp1 = m.group(5);
				assert sp1.matches(SP);

				String sp2 = m.group(7);
				assert sp2.matches(SP);

				String nl2 = m.group(8);
				assert nl2.matches(NL);

				if (prefix == XNode.OP.OP_INCLUDE.code) {
					addText(stack, nl1);
					addText(stack, sp1);

					expression(stack, prefix, txt, whole);

					addText(stack, sp2);
					addText(stack, nl2);

				} else {

					boolean hasNl1 = !nl1.isEmpty();

					if (!hasNl1) {
						if (m.start() > 0) {
							char ch = s.charAt(m.start() - 1);
							if (ch == 10 || ch == 13) {
								hasNl1 = true;
							}
						} else {
							hasNl1 = true;
						}
					}

					boolean hasNl2 = !nl2.isEmpty();

					if (!hasNl2) {
						if (m.end() + 1 < s.length()) {
							char ch = s.charAt(m.end() + 1);
							if (ch == 10 || ch == 13) {
								hasNl2 = true;
							}
						}
					}

					boolean wholeLine = hasNl1 && hasNl2;

					addText(stack, nl1);

					if (!wholeLine) {
						addText(stack, sp1);
					}

					block(stack, prefix, txt, whole);

					if (!wholeLine) {
						addText(stack, sp2);
					}

					if (!hasNl1 && hasNl2) {
						addText(stack, nl2);
					}
				}
			} else {
				throw U.rte("Unknown tag!");
			}

			pos = m.end();
		}

		addText(stack, s.substring(pos));

		XNode last = stack.pop();
		U.must(last == root, "The tag '%s' was not closed!", last.text);

		return root;
	}

	private static void addText(Stack<XNode> stack, String g1) {
		stack.peek().children.add(new XNode(XNode.OP.OP_TEXT, g1));
	}

	private static void expression(Stack<XNode> stack, char prefix, String expr, String whole) {
		U.must(!expr.isEmpty(), "Empty expression!");

		switch (prefix) {
			case '$':
				stack.peek().children.add(new XNode(XNode.OP.OP_PRINT, expr));
				break;

			case '@':
				stack.peek().children.add(new XNode(XNode.OP.OP_PRINT_RAW, expr));
				break;

			case '>':
				stack.peek().children.add(new XNode(XNode.OP.OP_INCLUDE, expr));
				break;

			default:
				throw U.rte("Invalid expression: %s", expr);
		}
	}

	private static void block(Stack<XNode> stack, char prefix, String stat, String whole) {
		U.must(!stat.isEmpty(), "Empty block!");

		switch (prefix) {
			case '#':
				stack.push(new XNode(XNode.OP.OP_FOREACH, stat));
				break;

			case '?':
				stack.push(new XNode(XNode.OP.OP_IF, stat));
				break;

			case '^':
			case '!':
				stack.push(new XNode(XNode.OP.OP_IF_NOT, stat));
				break;

			case '/':
				close(stack, stat);
				break;

			default:
				throw U.rte("Invalid block: %s", stat);
		}
	}

	private static void close(Stack<XNode> stack, String text) {
		U.must(!stack.isEmpty(), "Empty stack!");

		XNode x = stack.pop();
		U.must(x.op != XNode.OP.OP_ROOT, "Cannot close a tag that wasn't open: %s", text);

		if (!U.eq(x.text, text)) {
			throw U.rte("Expected block: %s, but found: %s", x.text, text);
		}

		stack.peek().children.add(x);
	}

}
