package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http
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

import java.util.ArrayList;
import java.util.List;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("4.0.0")
public class PageStack {

	public static final String ATTR_PAGE_STACK = "_page_stack_";

	public static HttpSuccessException goBack(HttpExchangeImpl x, int steps) {
		String dest = "/";
		List<String> stack = x.cookiepack(ATTR_PAGE_STACK, null);

		if (stack != null) {
			if (!stack.isEmpty()) {
				dest = stack.get(stack.size() - 1);
			}

			for (int i = 0; i < steps; i++) {
				if (!stack.isEmpty()) {
					stack.remove(stack.size() - 1);
					if (!stack.isEmpty()) {
						dest = stack.remove(stack.size() - 1);
					}
				}
			}
		}

		throw x.redirect(dest);
	}

	@SuppressWarnings("unchecked")
	public static void addToPageStack(HttpExchangeImpl x) {
		List<String> stack = x.cookiepackGetOrCreate(ATTR_PAGE_STACK, ArrayList.class);

		String last = !stack.isEmpty() ? stack.get(stack.size() - 1) : null;
		String current = x.uri();

		if (!U.eq(current, last)) {
			stack.add(current);
			if (stack.size() > 7) {
				stack.remove(0);
			}
		}
	}

}
