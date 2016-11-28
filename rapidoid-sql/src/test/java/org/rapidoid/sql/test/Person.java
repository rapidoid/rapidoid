package org.rapidoid.sql.test;

/*
 * #%L
 * rapidoid-sql
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

import org.rapidoid.orm.*;

@DbTable("persons")
public class Person extends DbEntity {

	@DbId
	public long id;

	@DbColumn("first_name")
	public String firstName;

	public String lastName; // lastname

	@DbMappedBy("author")
	public Refs<Book> books;

	@DbTransient
	public int foo;

}
