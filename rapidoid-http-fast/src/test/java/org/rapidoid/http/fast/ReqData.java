package org.rapidoid.http.fast;

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
import org.rapidoid.data.Range;
import org.rapidoid.data.Ranges;
import org.rapidoid.wrap.BoolWrap;

public class ReqData {

	final Range rUri = new Range();
	final Range rVerb = new Range();
	final Range rPath = new Range();
	final Range rQuery = new Range();
	final Range rProtocol = new Range();

	final Ranges headers = new Ranges(50);

	final KeyValueRanges params = new KeyValueRanges(50);
	final KeyValueRanges headersKV = new KeyValueRanges(50);

	final KeyValueRanges cookies = new KeyValueRanges(50);

	final KeyValueRanges posted = new KeyValueRanges(50);
	final KeyValueRanges files = new KeyValueRanges(50);

	final Range rBody = new Range();

	final BoolWrap isGet = new BoolWrap();
	final BoolWrap isKeepAlive = new BoolWrap();

}
