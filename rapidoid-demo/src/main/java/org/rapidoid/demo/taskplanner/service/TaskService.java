package org.rapidoid.demo.taskplanner.service;

/*
 * #%L
 * rapidoid-demo
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import java.util.List;

import org.rapidoid.db.DAO;
import org.rapidoid.demo.taskplanner.model.Task;
import org.rapidoid.util.U;

public class TaskService extends DAO<Task> {

	public List<Task> add(Task task) {
		U.info("Inserting task", "task", task);
		insert(task);
		return getAll();
	}

}
