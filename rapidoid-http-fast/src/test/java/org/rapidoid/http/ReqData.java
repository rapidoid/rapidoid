package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http-fast
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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

import org.rapidoid.data.KeyValueRanges;
import org.rapidoid.data.BufRange;
import org.rapidoid.data.BufRanges;
import org.rapidoid.wrap.BoolWrap;

public class ReqData {

	final BufRange rUri = new BufRange();
	final BufRange rVerb = new BufRange();
	final BufRange rPath = new BufRange();
	final BufRange rQuery = new BufRange();
	final BufRange rProtocol = new BufRange();

	final BufRanges headers = new BufRanges(50);

	final KeyValueRanges params = new KeyValueRanges(50);
	final KeyValueRanges headersKV = new KeyValueRanges(50);

	final KeyValueRanges cookies = new KeyValueRanges(50);

	final KeyValueRanges posted = new KeyValueRanges(50);
	final KeyValueRanges files = new KeyValueRanges(50);

	final BufRange rBody = new BufRange();

	final BoolWrap isGet = new BoolWrap();
	final BoolWrap isKeepAlive = new BoolWrap();

}
