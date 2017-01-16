package org.rapidoid.net.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.BufProvider;
import org.rapidoid.data.Data;
import org.rapidoid.data.BufRange;
import org.rapidoid.util.Msc;

/*
 * #%L
 * rapidoid-net
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
public class DecodedData extends RapidoidThing implements Data {

	private final BufProvider src;

	private final BufRange range;

	private String value;

	public DecodedData(BufProvider src, BufRange range) {
		this.src = src;
		this.range = range;
	}

	@Override
	public synchronized String get() {
		if (value == null) {
			value = !range.isEmpty() ? Msc.urlDecode(src.buffer().get(range)) : "";
		}

		return value;
	}

	@Override
	public BufRange range() {
		return range;
	}

	@Override
	public synchronized void reset() {
		value = null;
	}

}
