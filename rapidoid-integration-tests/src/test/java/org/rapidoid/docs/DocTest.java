package org.rapidoid.docs;

/*
 * #%L
 * rapidoid-integration-tests
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

import org.junit.Test;
import org.rapidoid.http.IsolatedIntegrationTest;
import org.rapidoid.u.U;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base class used as entry point, to execute an example and generate the docs.
 * <p>
 * This test will execute the main class specified in the annotation.
 */
public abstract class DocTest extends IsolatedIntegrationTest {

	final AtomicInteger order = new AtomicInteger();

	@Test
	public void docs() throws Exception {
		order.set(0);
		exercise();
	}

	protected void exercise() {
		// by default do nothing
	}

	private String order() {
		return "#" + order.incrementAndGet();
	}

	protected void GET(String uri) {
		getReq(uri + order());
	}

	protected void POST(String uri) {
		postJson(uri + order(), U.map());
	}

	protected void POST(String uri, Map<String, ?> data) {
		postJson(uri + order(), data);
	}

	protected void PUT(String uri) {
		putData(uri + order(), U.map());
	}

	protected void PUT(String uri, Map<String, ?> data) {
		putData(uri + order(), data);
	}

	protected void DELETE(String uri) {
		deleteReq(uri + order());
	}

}
