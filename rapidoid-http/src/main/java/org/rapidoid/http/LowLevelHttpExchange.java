package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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
import org.rapidoid.buffer.Buf;
import org.rapidoid.data.BinaryMultiData;
import org.rapidoid.data.Data;
import org.rapidoid.data.MultiData;

@Authors("Nikolche Mihajlovski")
@Since("2.3.0")
public interface LowLevelHttpExchange extends HttpExchange {

	void lowLevelProcessing();

	Buf input();

	Buf output();

	Data verb_();

	Data uri_();

	Data path_();

	Data subpath_();

	Data query_();

	Data protocol_();

	Data body_();

	Data host_();

	MultiData params_();

	MultiData headers_();

	MultiData cookies_();

	MultiData posted_();

	BinaryMultiData files_();

	LowLevelHttpExchange send();

}
