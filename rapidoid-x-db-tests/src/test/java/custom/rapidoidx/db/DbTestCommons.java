package custom.rapidoidx.db;

/*
 * #%L
 * rapidoid-x-db-tests
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.log.Log;
import org.rapidoid.log.LogLevel;
import org.rapidoid.scan.Scan;
import org.rapidoid.test.TestCommons;
import org.rapidoidx.db.DBs;
import org.rapidoidx.db.XDB;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public abstract class DbTestCommons extends TestCommons {

	@BeforeMethod(alwaysRun = true)
	@AfterMethod(alwaysRun = true)
	public void initDB() {
		Scan.classes();
		Log.warn("Destroying all databases: " + DBs.instances());
		DBs.destroyAll();
		XDB.destroy();
		XDB.start();
		Log.setLogLevel(LogLevel.INFO);
		Ctx.reset();
	}

}
