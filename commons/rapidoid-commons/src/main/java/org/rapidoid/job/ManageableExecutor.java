package org.rapidoid.job;

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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.group.AutoManageable;
import org.rapidoid.group.ManageableBean;
import org.rapidoid.u.U;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
@ManageableBean(kind = "jobs")
public class ManageableExecutor extends AutoManageable<ManageableExecutor> {

	private final ThreadPoolExecutor executor;

	public ManageableExecutor(String id, ThreadPoolExecutor executor) {
		super(id);
		this.executor = executor;
	}

	@Override
	public List<String> getManageableProperties() {
		return U.list("id", "activeCount", "taskCount", "completedTaskCount",
			"maximumPoolSize", "corePoolSize", "largestPoolSize");
	}

	public boolean isShutdown() {
		return executor.isShutdown();
	}

	public int getCorePoolSize() {
		return executor.getCorePoolSize();
	}

	public int getMaximumPoolSize() {
		return executor.getMaximumPoolSize();
	}

	public int getPoolSize() {
		return executor.getPoolSize();
	}

	public int getActiveCount() {
		return executor.getActiveCount();
	}

	public int getLargestPoolSize() {
		return executor.getLargestPoolSize();
	}

	public long getTaskCount() {
		return executor.getTaskCount();
	}

	public long getCompletedTaskCount() {
		return executor.getCompletedTaskCount();
	}
}
