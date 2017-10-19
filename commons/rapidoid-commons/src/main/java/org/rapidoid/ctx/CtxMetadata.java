package org.rapidoid.ctx;

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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("5.0.0")
public interface CtxMetadata {

	String _PREFIX = "";

	String _SUFFIX = "";

	String APP = _PREFIX + "APP" + _SUFFIX;

	String HOST = _PREFIX + "HOST" + _SUFFIX;

	String VERB = _PREFIX + "VERB" + _SUFFIX;

	String URL = _PREFIX + "URL" + _SUFFIX;

	String URI = _PREFIX + "URI" + _SUFFIX;

	String PATH = _PREFIX + "PATH" + _SUFFIX;

	String CLIENT_ADDRESS = _PREFIX + "CLIENT_ADDRESS" + _SUFFIX;

	String FORWARDED_FOR = _PREFIX + "FORWARDED_FOR" + _SUFFIX;

	String HEADERS = _PREFIX + "HEADERS" + _SUFFIX;

	String COOKIES = _PREFIX + "COOKIES" + _SUFFIX;

	String DATA = _PREFIX + "DATA" + _SUFFIX;

	String SPECIAL = _PREFIX + "SPECIAL" + _SUFFIX;

	String CODE = _PREFIX + "CODE" + _SUFFIX;

	String CONTENT_TYPE = _PREFIX + "CONTENT_TYPE" + _SUFFIX;

	String SET_HEADERS = _PREFIX + "SET_HEADERS" + _SUFFIX;

	String SET_COOKIES = _PREFIX + "SET_COOKIES" + _SUFFIX;

}
