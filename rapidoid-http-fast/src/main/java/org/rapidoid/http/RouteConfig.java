package org.rapidoid.http;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.annotation.TransactionMode;
import org.rapidoid.http.impl.RouteOptions;

import java.util.Set;

/*
 * #%L
 * rapidoid-http-fast
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
@Since("5.1.0")
public interface RouteConfig {

	MediaType contentType();

	RouteConfig contentType(MediaType contentType);

	String view();

	RouteOptions view(String view);

	boolean mvc();

	RouteOptions mvc(boolean mvc);

	TransactionMode transaction();

	RouteOptions transaction(TransactionMode transactionMode);

	Set<String> roles();

	RouteOptions roles(String... roles);

	HttpWrapper[] wrappers();

	RouteOptions wrappers(HttpWrapper... wrappers);

	String zone();

	RouteOptions zone(String zone);

	boolean managed();

	RouteOptions managed(boolean managed);

	long cacheTTL();

	RouteOptions cacheTTL(long cacheTTL);

	int cacheCapacity();

	RouteOptions cacheCapacity(int cacheCapacity);
}
