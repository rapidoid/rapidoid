package org.rapidoid.core;

/*
 * #%L
 * rapidoid-core
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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.rapidoid.buffer.BufProvider;
import org.rapidoid.data.Data;
import org.rapidoid.data.Range;
import org.rapidoid.util.U;

public class DecodedData implements Data {

	private final BufProvider src;

	private final Range range;

	public DecodedData(BufProvider src, Range range) {
		this.src = src;
		this.range = range;
	}

	@Override
	public String get() {
		try {
			return !range.isEmpty() ? URLDecoder.decode(src.buffer().get(range), "UTF-8") : "";
		} catch (UnsupportedEncodingException e) {
			throw U.rte(e);
		}
	}

	@Override
	public Range range() {
		return range;
	}

}
