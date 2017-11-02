package org.rapidoid.deploy;

/*-
 * #%L
 * rapidoid-platform
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
import org.rapidoid.annotation.Order;
import org.rapidoid.annotation.Since;
import org.rapidoid.group.*;
import org.rapidoid.gui.GUI;
import org.rapidoid.process.ProcessHandle;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.Date;
import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.4.7")
@ManageableBean(kind = "apps")
public class ManageableApp extends AbstractManageable {

	private final AppDeployment app;

	public ManageableApp(AppDeployment app) {
		this.app = app;
	}

	@Override
	public String id() {
		return app.name();
	}

	@Override
	public List<String> getManageableProperties() {
		return U.list("id", "location", "exists", "empty", "alive", "port", "startedAt",
			"finishedAt", "exitCode", "uptime", "details", "open");
	}

	@Action()
	@Order(100)
	public void deploy() {
		app.deploy();
	}

	@ActionCondition
	public boolean canDeploy() {
		return isStaged();
	}

	@Action
	@Order(10)
	public void start() {
		app.start();
	}

	@ActionCondition
	public boolean canStart() {
		return exists() && !isAlive();
	}

	@Action
	@Order(30)
	public void stop() {
		app.stop();
	}

	@ActionCondition
	public boolean canStop() {
		return exists() && isAlive();
	}

	@Action
	@Order(20)
	public void restart() {
		app.restart();
	}

	@ActionCondition
	public boolean canRestart() {
		return exists() && isAlive();
	}

	@Action(name = "!delete")
	@Order(Integer.MAX_VALUE)
	public void delete() {
		app.delete();
	}

	@ActionCondition(name = "!delete")
	public boolean canDelete() {
		return exists();
	}

	public boolean exists() {
		return app.exists();
	}

	public boolean isEmpty() {
		return app.isEmpty();
	}

	public String location() {
		return app.path();
	}

	@Action
	@Order(1)
	public void create() {
		app.create();
	}

	@ActionCondition
	public boolean canCreate() {
		return !exists();
	}

	public boolean isAlive() {
		return app.isAlive();
	}

	public boolean isStaged() {
		return app.isStaged();
	}

	public int port() {
		return app.port();
	}

	public Date startedAt() {
		ProcessHandle proc = app.process();
		return proc != null ? proc.startedAt() : null;
	}

	public Date finishedAt() {
		ProcessHandle proc = app.process();
		return proc != null ? proc.finishedAt() : null;
	}

	public Integer exitCode() {
		ProcessHandle proc = app.process();
		return proc != null ? proc.exitCode() : null;
	}

	public String uptime() {
		ProcessHandle proc = app.process();
		return proc != null ? proc.uptime() : null;
	}

	public Object details() {
		List<Object> links = U.list();

		if (Apps.processes().exists(id())) {
			String processUri = Msc.specialUri("processes/" + id());

			links.add(GUI.btn("View process")
				.class_("btn btn-default btn-xs")
				.go(processUri));
		}

		return GUI.multi(links.toArray());
	}

	public Object open() {
		if (Apps.processes().exists(id())) {

			String appUri = "/" + id();

			return GUI.btn("Open")
				.class_("btn btn-primary btn-xs")
				.go(appUri);

		} else {
			return null;
		}
	}

	@Override
	public GroupOf<? extends Manageable> group() {
		return Apps.deployments();
	}
}
