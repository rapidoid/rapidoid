package org.rapidoid.util;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

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
@Since("2.0.0")
public interface Constants {

	Object[] EMPTY_ARRAY = {};

	String[] EMPTY_STRING_ARRAY = {};

	int NOT_FOUND = Integer.MIN_VALUE;

	boolean T = true;

	boolean F = false;

	byte BYTE_0 = 0;

	byte SPACE = ' ';

	byte CR = 13;

	byte LF = 10;

	byte[] CR_LF_CR_LF = {CR, LF, CR, LF};

	byte[] CR_LF = {CR, LF};

	byte[] LF_LF = {LF, LF};

	byte[] SPACE_ = {SPACE};

	byte[] CR_ = {CR};

	byte[] LF_ = {LF};

	byte ASTERISK = '?';

	byte EQ = '=';

	byte AMP = '&';

	byte COL = ':';

	byte SEMI_COL = ';';

	String SEPARATOR_LINE = "\n--------------------------------------------------------------\n";

	String ANY = "ANY";

	String GET = "GET";

	String POST = "POST";

	String PUT = "PUT";

	String DELETE = "DELETE";

	String PATCH = "PATCH";

	String OPTIONS = "OPTIONS";

	String HEAD = "HEAD";

	String TRACE = "TRACE";

	String GET_OR_POST = "GET,POST";

	String[] HTTP_VERBS = {GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD, TRACE};

	// https://en.wikipedia.org/wiki/UTF-8

	int UTF8_2_BYTES_LEAD = 0xc0;

	int UTF8_3_BYTES_LEAD = 0xe0;

	int UTF8_4_BYTES_LEAD = 0xf0;

	int UTF8_CONTINUATION = 0x80;

	int LAST_6 = 0x3f;

	byte MALFORMED_CHAR = '?';

}
