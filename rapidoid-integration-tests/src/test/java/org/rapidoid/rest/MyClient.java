package org.rapidoid.rest;

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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.OfType;
import org.rapidoid.annotation.Since;
import org.rapidoid.concurrent.Callback;

import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("4.4.0")
public interface MyClient {

	String abc();

	List<Integer> numbers();

	int sizeOf(String text);

	void asyncSizeOf(String text, Callback<Integer> resultCallback);

	MyBean theBean(int a, String b, boolean c);

	void asyncBean(int a, String b, boolean c, @OfType(MyBean.class) Callback<MyBean> resultCallback);

}
