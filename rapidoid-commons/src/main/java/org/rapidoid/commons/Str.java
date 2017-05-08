package org.rapidoid.commons;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.Lmbd;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.u.U;

import javax.xml.bind.DatatypeConverter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
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
@Since("5.0.4")
public class Str extends RapidoidThing {

	// regex taken from
	// http://stackoverflow.com/questions/2559759/how-do-i-convert-camelcase-into-human-readable-names-in-java
	public static final String CAMEL_REGEX = "(?<=[A-Z])(?=[A-Z][a-z])|(?<=[^A-Z])(?=[A-Z])|(?<=[A-Za-z])(?=[^A-Za-z])";

	private static final Pattern CAMEL_SPLITTER_PATTERN = Pattern.compile(CAMEL_REGEX);

	private static final String[][] XML_ESCAPE = {
		{"&", "&amp;"},
		{"\"", "&quot;"},
		{"<", "&lt;"},
		{">", "&gt;"},
	};

	private static final String[][] JAVA_ESCAPE = {
		{"\n", "\\\\n"},
		{"\r", "\\\\r"},
		{"\t", "\\\\t"},
		{"\"", "\\\\\""},
	};

	private static final Mapper<String[], String> MASK_REPLACER = new Mapper<String[], String>() {
		@Override
		public String map(String[] src) throws Exception {
			return Str.mul("\u0000", src[0].length());
		}
	};

	private Str() {
	}

	public static String[] camelSplit(String s) {
		return CAMEL_SPLITTER_PATTERN.split(s);
	}

	public static String phrase(String s) {
		String[] parts = s.contains("_") ? s.split("_") : camelSplit(s);
		return capitalized(U.join(" ", parts).toLowerCase());
	}

	public static String replace(String s, String[][] repls) {
		for (String[] repl : repls) {
			U.must(repl.length == 2, "Expected pairs of [search, replacement] strings!");
			s = s.replaceAll(Pattern.quote(repl[0]), repl[1]);
		}

		return s;
	}

	public static String mask(String target, Pattern regex) {
		return Str.replace(target, regex, MASK_REPLACER);
	}

	public static String replace(String target, String regex, Mapper<String[], String> replacer) {
		return replace(target, Pattern.compile(regex), replacer);
	}

	public static String replace(String target, Pattern regex, Mapper<String[], String> replacer) {
		return replace(target, target, regex, replacer);
	}

	public static String replace(String target, String mask, Pattern regex, Mapper<String[], String> replacer) {
		U.must(target.length() == mask.length());

		StringBuffer output = new StringBuffer();
		Matcher m = regex.matcher(mask);
		int pos = 0;

		while (m.find()) {

			int len = m.groupCount() + 1;
			String[] groups = new String[len];

			for (int i = 0; i < groups.length; i++) {
				groups[i] = m.group(i);
			}

			String replacement = Lmbd.eval(replacer, groups);

			output.append(target.substring(pos, m.start()));
			output.append(replacement);

			pos = m.end();
		}

		output.append(target.substring(pos));

		return output.toString();
	}

	public static String render(Object[] items, String itemFormat, String sep) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < items.length; i++) {
			if (i > 0) {
				sb.append(sep);
			}
			sb.append(U.frmt(itemFormat, items[i]));
		}

		return sb.toString();
	}

	public static String render(Iterable<?> items, String itemFormat, String sep) {
		StringBuilder sb = new StringBuilder();

		int i = 0;
		Iterator<?> it = items.iterator();
		while (it.hasNext()) {
			Object item = it.next();
			if (i > 0) {
				sb.append(sep);
			}

			sb.append(U.frmt(itemFormat, item));
			i++;
		}

		return sb.toString();
	}

	public static String render(Map<?, ?> items, String itemFormat, String sep) {
		StringBuilder sb = new StringBuilder();

		boolean first = true;
		int index = 0;
		for (Map.Entry<?, ?> e : items.entrySet()) {

			if (!first) {
				sb.append(sep);
			}

			String s = itemFormat
				.replaceAll(Pattern.quote("${key}"), String.valueOf(e.getKey()))
				.replaceAll(Pattern.quote("${value}"), String.valueOf(e.getValue()))
				.replaceAll(Pattern.quote("${index}"), String.valueOf(index));

			sb.append(s);

			first = false;
			index++;
		}

		return sb.toString();
	}

	public static String capitalized(String s) {
		return s.isEmpty() ? s : s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	public static String uncapitalized(String s) {
		return s.isEmpty() ? s : s.substring(0, 1).toLowerCase() + s.substring(1);
	}

	public static String mul(String s, int n) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < n; i++) {
			sb.append(s);
		}

		return sb.toString();
	}

	public static String sub(String s, int beginIndex, int endIndex) {
		if (endIndex < 0) {
			endIndex = s.length() + endIndex;
		}
		return s.substring(beginIndex, endIndex);
	}

	public static String trimr(String s, char suffix) {
		return (!s.isEmpty() && s.charAt(s.length() - 1) == suffix) ? sub(s, 0, -1) : s;
	}

	public static String trimr(String s, String suffix) {
		return s.endsWith(suffix) ? sub(s, 0, -suffix.length()) : s;
	}

	public static String triml(String s, char prefix) {
		return (!s.isEmpty() && s.charAt(0) == prefix) ? s.substring(1) : s;
	}

	public static String triml(String s, String prefix) {
		return s.startsWith(prefix) ? s.substring(prefix.length()) : s;
	}

	public static String insert(String target, int atIndex, String insertion) {
		return target.substring(0, atIndex) + insertion + target.substring(atIndex);
	}

	public static String cutToFirst(String s, String delimiter) {
		int pos = s.indexOf(delimiter);
		return pos >= 0 ? s.substring(0, pos) : null;
	}

	public static String cutToLast(String s, String delimiter) {
		int pos = s.lastIndexOf(delimiter);
		return pos >= 0 ? s.substring(0, pos) : null;
	}

	public static String cutFromFirst(String s, String delimiter) {
		int pos = s.indexOf(delimiter);
		return pos >= 0 ? s.substring(pos + delimiter.length()) : null;
	}

	public static String cutFromLast(String s, String delimiter) {
		int pos = s.lastIndexOf(delimiter);
		return pos >= 0 ? s.substring(pos + delimiter.length()) : null;
	}

	public static String xmlEscape(String s) {
		return Str.replace(s, XML_ESCAPE);
	}

	public static String javaEscape(String s) {
		return Str.replace(s, JAVA_ESCAPE);
	}

	public static String camelToSnake(String s) {
		return U.join("_", Str.camelSplit(s)).toLowerCase();
	}

	public static String toHex(byte[] data) {
		return DatatypeConverter.printHexBinary(data);
	}

	public static byte[] fromHex(String hex) {
		return DatatypeConverter.parseHexBinary(hex);
	}

	public static String toBase64(byte[] data) {
		return DatatypeConverter.printBase64Binary(data);
	}

	public static String toBase64(byte[] data, char plusReplacement, char slashReplacement) {
		return toBase64(data).replace('+', plusReplacement).replace('/', slashReplacement);
	}

	public static String toWebSafeBase64(byte[] data) {
		return toBase64(data, '-', '_');
	}

	public static String toWebSafeBinary(byte[] data) {
		String s = toWebSafeBase64(data);
		StringBuilder sb = new StringBuilder();

		int k = 7;
		int i = 0;
		for (; i < s.length(); i += k) {
			if (i > 0) sb.append(":");
			sb.append(s.substring(i, Math.min(i + k, s.length())));
		}

		return sb.toString();
	}

	public static boolean isWebSafeBinary(String s) {
		boolean foundMarks = false;
		int k = 7;

		for (int i = k; i < s.length(); i += k + 1) {
			if (s.charAt(i) != ':') return false;
			foundMarks = true;
		}

		return foundMarks;
	}

	public static byte[] fromWebSafeBinary(String binary) {
		return fromWebSafeBase64(binary.replaceAll(":", ""));
	}

	public static byte[] fromBase64(String base64) {
		return DatatypeConverter.parseBase64Binary(base64);
	}

	public static byte[] fromBase64(String base64, char plusReplacement, char slashReplacement) {
		return fromBase64(base64.replace(plusReplacement, '+').replace(slashReplacement, '/'));
	}

	public static byte[] fromWebSafeBase64(String base64) {
		return fromBase64(base64, '-', '_');
	}

	public static String wildcardToRegex(String pattern) {

		if (pattern.isEmpty()) return "";
		if (pattern.equals("*")) return "(.*)";

		String[] nameParts = pattern.split("\\*", Integer.MAX_VALUE);
		StringBuilder sb = new StringBuilder();

		for (String part : nameParts) {
			String s = part.isEmpty() ? "(.*)" : Pattern.quote(part);
			sb.append(s);
		}

		return sb.toString();
	}

	public static String wildcardsToRegex(String... patterns) {

		if (U.isEmpty(patterns)) return "";
		if (patterns.length == 1) return wildcardToRegex(patterns[0]);

		StringBuilder sb = new StringBuilder();

		for (Iterator<String> it = U.iterator(patterns); it.hasNext(); ) {
			String pattern = it.next();
			sb.append(wildcardToRegex(pattern));

			if (it.hasNext()) sb.append("|");
		}

		return "(?:" + sb + ")";
	}

	public static List<String> linesOf(String s) {
		return U.list(s.split("\\n"));
	}

	public static List<String> grep(String regex, Iterable<String> lines) {
		List<String> matching = U.list();
		Pattern p = Pattern.compile(regex);

		for (String line : lines) {
			if (p.matcher(line).find()) {
				matching.add(line);
			}
		}

		return matching;
	}
}
