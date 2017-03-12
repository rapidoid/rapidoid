package org.rapidoid.benchmark.common;

/*
 * #%L
 * rapidoid-benchmark
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

import org.rapidoid.u.U;

public class Fortune implements Comparable<Fortune> {

	private int id;
	private String message;

	public Fortune() {
	}

	public Fortune(int id, String message) {
		this.id = id;
		this.message = message;
	}

	public int getId() {
		return id;
	}

	public Fortune setId(int id) {
		this.id = id;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public Fortune setMessage(String message) {
		this.message = message;
		return this;
	}

	@Override
	public String toString() {
		return "Fortune{" +
			"id=" + id +
			", message='" + message + '\'' +
			'}';
	}

	@Override
	public int compareTo(Fortune o) {
		return U.compare(this.message, o.message);
	}
}
