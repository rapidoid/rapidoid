package org.rapidoid.demo.taskplanner.model;

/*
 * #%L
 * rapidoid-demo
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

public class Task {

	public long id;

	public String title;

	public Priority priority;

	public User author;

	public Task() {
	}

	public Task(String title, Priority priority) {
		this.title = title;
		this.priority = priority;
	}

	public Task(String title, Priority priority, User author) {
		this.title = title;
		this.priority = priority;
		this.author = author;
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", title=" + title + ", priority=" + priority + ", author=" + author + "]";
	}

}
