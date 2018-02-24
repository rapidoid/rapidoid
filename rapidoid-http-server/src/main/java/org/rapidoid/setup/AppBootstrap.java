/*-
 * #%L
 * rapidoid-http-server
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
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

package org.rapidoid.setup;

import org.rapidoid.ModuleBootstrapParams;
import org.rapidoid.RapidoidModules;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.util.MscOpts;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class AppBootstrap extends RapidoidThing {

	private static final ServiceBootstrap jpa = new ServiceBootstrap() {
		@Override
		protected void bootstrap() {
			if (MscOpts.hasJPA()) {
				ModuleBootstrapParams params = new ModuleBootstrapParams().path(App.path());
				RapidoidModules.get("JPA").bootstrap(params);
			}
		}
	};

	private static final ServiceBootstrap auth = new ServiceBootstrap() {
		@Override
		protected void bootstrap() {
			getGoodies().auth(On.setup());
		}
	};

	private static final ServiceBootstrap oauth = new ServiceBootstrap() {
		@Override
		protected void bootstrap() {
			getGoodies().oauth(On.setup());
		}
	};

	private static final ServiceBootstrap entities = new ServiceBootstrap() {
		@Override
		protected void bootstrap() {
			getGoodies().entities(Admin.setup());
		}
	};

	private static final ServiceBootstrap overview = new ServiceBootstrap() {
		@Override
		protected void bootstrap() {
			getGoodies().overview(Admin.setup());
		}
	};

	private static final ServiceBootstrap application = new ServiceBootstrap() {
		@Override
		protected void bootstrap() {
			getGoodies().application(Admin.setup());
		}
	};

	private static final ServiceBootstrap manageables = new ServiceBootstrap() {
		@Override
		protected void bootstrap() {
			getGoodies().manageables(Admin.setup());
		}
	};

	private static final ServiceBootstrap lifecycle = new ServiceBootstrap() {
		@Override
		protected void bootstrap() {
			getGoodies().lifecycle(Admin.setup());
		}
	};

	private static final ServiceBootstrap jmx = new ServiceBootstrap() {
		@Override
		protected void bootstrap() {
			getGoodies().jmx(Admin.setup());
		}
	};

	private static final ServiceBootstrap metrics = new ServiceBootstrap() {
		@Override
		protected void bootstrap() {
			getGoodies().metrics(Admin.setup());
		}
	};

	private static final ServiceBootstrap deploy = new ServiceBootstrap() {
		@Override
		protected void bootstrap() {
			getGoodies().deploy(Admin.setup());
		}
	};

	private static final ServiceBootstrap adminCenter = new ServiceBootstrap() {
		@Override
		protected void bootstrap() {
			getGoodies().adminCenter(Admin.setup());
		}
	};

	private static final ServiceBootstrap beans = new ServiceBootstrap() {
		@Override
		protected void bootstrap() {
			App.scan();
		}
	};

	private static final ServiceBootstrap openapi = new ServiceBootstrap() {
		@Override
		protected void bootstrap() {
			getGoodies().openapi(On.setup());
		}
	};

	public AppBootstrap jpa() {
		jpa.run();
		return this;
	}

	public AppBootstrap overview() {
		overview.run();
		return this;
	}

	public AppBootstrap application() {
		application.run();
		return this;
	}

	public AppBootstrap manageables() {
		manageables.run();
		return this;
	}

	public AppBootstrap lifecycle() {
		lifecycle.run();
		return this;
	}

	public AppBootstrap jmx() {
		jmx.run();
		return this;
	}

	public AppBootstrap metrics() {
		metrics.run();
		return this;
	}

	public AppBootstrap deploy() {
		deploy.run();
		return this;
	}

	public AppBootstrap auth() {
		auth.run();
		return this;
	}

	public AppBootstrap oauth() {
		oauth.run();
		return this;
	}

	public AppBootstrap adminCenter() {
		adminCenter.run();
		return this;
	}

	public AppBootstrap beans() {
		beans.run();
		return this;
	}

	public AppBootstrap openapi() {
		openapi.run();
		return this;
	}

	static IGoodies getGoodies() {
		Class<?> goodiesClass;

		if (Msc.isPlatform()) {
			goodiesClass = Cls.get("org.rapidoid.goodies.RapidoidPlatformGoodies");

		} else {
			goodiesClass = Cls.getClassIfExists("org.rapidoid.goodies.RapidoidGoodies");
			U.must(goodiesClass != null, "Cannot find the Goodies, is module 'rapidoid-web' missing?");
		}

		return (IGoodies) Cls.newInstance(goodiesClass);
	}

	public void full() {
		jpa();
		adminCenter();
		auth();
		oauth();
		openapi();
	}

	static void reset() {
		jpa.reset();
		entities.reset();
		overview.reset();
		application.reset();
		manageables.reset();
		lifecycle.reset();
		jmx.reset();
		metrics.reset();
		deploy.reset();
		auth.reset();
		oauth.reset();
		adminCenter.reset();
		beans.reset();
		openapi.reset();
	}

}
