package org.rapidoid.commons;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.u.U;

import java.util.regex.Pattern;

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

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class StringRewriter extends RapidoidThing {

	public static final String MASK_QUOTES = "'[^']*'";

	public static final String MASK_DOUBLE_QUOTES = "\"[^\"]*\"";

	public static final String MASK_BACKTICKS = "`[^`]*`";

	public static final String[] ALL_QUOTES = {MASK_QUOTES, MASK_DOUBLE_QUOTES, MASK_BACKTICKS};

	private final Pattern escape;

	private final Pattern regex;

	public StringRewriter(String[] escape, String regex) {
		this.escape = Pattern.compile("(?:" + U.join("|", escape) + ")");
		this.regex = Pattern.compile(regex);
	}

	public String rewrite(String target, Mapper<String[], String> replacer) {
		String mask = Str.mask(target, escape);
		return Str.replace(target, mask, regex, replacer);
	}

}
