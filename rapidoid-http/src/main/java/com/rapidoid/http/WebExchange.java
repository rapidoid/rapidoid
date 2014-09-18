package com.rapidoid.http;

/*
 * #%L
 * rapidoid-http
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

import java.util.Map;

import org.rapidoid.Ctx;
import org.rapidoid.data.BinaryMultiData;
import org.rapidoid.data.Data;
import org.rapidoid.data.MultiData;

public interface WebExchange extends Ctx {

	String verb();

	Data verb_();

	String uri();

	Data uri_();

	String path();

	Data path_();

	String subpath();

	Data subpath_();

	String query();

	Data query_();

	String protocol();

	Data protocol_();

	String body();

	Data body_();

	Map<String, String> params();

	MultiData params_();

	Map<String, String> headers();

	MultiData headers_();

	Map<String, String> cookies();

	MultiData cookies_();

	Map<String, String> data();

	MultiData data_();

	Map<String, byte[]> files();

	BinaryMultiData files_();

	// due to async web handling option, it ain't over till the fat lady sings "done"
	void done();

}
