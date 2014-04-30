package org.rapidoid.util;

/*
 * #%L
 * rapidoid-commons
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

public interface Constants {

	byte SPACE = ' ';

	byte CR = 13;

	byte LF = 10;

	byte[] CR_LF_CR_LF = { CR, LF, CR, LF };

	byte[] CR_LF = { CR, LF };

	byte[] LF_LF = { LF, LF };

	byte[] SPACE_ = { SPACE };

	byte[] CR_ = { CR };

	byte[] LF_ = { LF };

	byte ASTERISK = '?';

	byte EQ = '=';

	byte AMP = '&';

	byte COL = ':';

}
