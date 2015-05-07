package org.rapidoidx.demo.taskplanner.gui;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

/*
 * #%L
 * rapidoid-x-demo
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class App {

	public String title = "Task Planer";

	public String theme = "1";

	public boolean search = true;

	public boolean themes = true;

	public boolean fluid = false;

	public boolean settings = true;

	public boolean googleLogin = true;

	public boolean facebookLogin = true;

	public boolean linkedinLogin = true;

	public boolean githubLogin = true;

	public boolean auth = true;

	// public Object[] screens = { HomeScreen.class, "NewTask", "tasksScreen" };

}
