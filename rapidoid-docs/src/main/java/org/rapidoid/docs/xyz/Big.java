package org.rapidoid.docs.xyz;

import java.util.Date;
import java.util.Map;

import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.On;
import org.rapidoid.annotation.Page;
import org.rapidoid.annotation.Web;
import org.rapidoid.app.GUI;
import org.rapidoid.util.U;

/*
 * #%L
 * rapidoid-docs
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

@Web
public class Big extends GUI {

	@Page(search = true, title = "GUI", profile = false)
	public Object gui(Map<String, Object> params) {
		System.out.println(">>> params: " + params);
		Book book = new Book();
		return multi(create(book), ADD, DELETE, book, new Date());
	}

	@On(event = "add", page = "/gui")
	public void guiOnAdd(Book book) {
		System.out.println("::: " + book);
		// DB.insert(book);
	}

	@On(event = "bind", page = "/gui2")
	public void dataBind(Map<String, Object> params) {
		System.out.println("::: " + params);
		// DB.insert(book);
	}

	@Page
	public Object frm(Map<String, Object> params) {
		return U.map("frm", create(U.map("Aaa", "a", "Bbb", "bre more")), "hl", highlight("ab-cd-efg", "\\w+"));
	}

}