package org.rapidoid.data;

/*
 * #%L
 * rapidoid-commons
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

import java.util.List;
import java.util.Set;

@SuppressWarnings("unchecked")
/**
 * @author Nikolche Mihajlovski
 * @since 2.0.0
 */
public class User {

	public long id;

	public String name;

	public int age;

	public String[] tags = {"aa", "bbb"};

	public Set<?> ss = U.set(1, "bn", false);

	public List<?> lst = U.list(1, "bn", false);

	public User() {
	}

	public User(String name, int age) {
		this.name = name;
		this.age = age;
	}

}
