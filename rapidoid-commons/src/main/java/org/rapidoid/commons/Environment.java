package org.rapidoid.commons;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.config.RapidoidInitializer;
import org.rapidoid.log.Log;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * #%L
 * rapidoid-commons
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

@Authors("Nikolche Mihajlovski")
@Since("5.2.0")
public class Environment extends RapidoidInitializer {

	private volatile Set<String> profiles;

	private volatile Set<String> profilesView;

	private volatile EnvMode mode;

	private volatile List<String> args;

	private volatile List<String> argsView;

	private final EnvProperties properties = new EnvProperties();

	public Environment() {
		reset();
	}

	public Set<String> profiles() {
		makeSureIsInitialized();
		return profilesView;
	}

	public EnvMode mode() {
		makeSureIsInitialized();
		return mode;
	}

	private void makeSureIsInitialized() {
		if (profiles == null || mode == null) {
			synchronized (this) {
				if (profiles == null || mode == null) {
					initModeAndProfiles();
				}
			}
		}
	}

	public synchronized void reset() {
		profiles = null;
		mode = null;
		setArgs(); // no args
	}

	@SuppressWarnings("ConstantConditions")
	private void initModeAndProfiles() {

		if (U.isEmpty(profiles)) {
			profiles = Coll.synchronizedSet();
			profiles.addAll(retrieveProfiles());
			profilesView = Collections.unmodifiableSet(profiles);
		}

		boolean production = Env.hasInitial("mode", "production") || profiles.contains("production");
		boolean test = Env.hasInitial("mode", "test") || profiles.contains("test");
		boolean dev = Env.hasInitial("mode", "dev") || profiles.contains("dev");

		if (!production && !test && !dev) {
			mode = inferMode();
			Log.info("No production/dev/test mode was configured, inferring mode", "!mode", mode);

		} else {
			boolean onlyOne = (!production || !dev) && (!dev || !test) && (!production || !test);
			U.must(onlyOne, "Only one of the ('production', 'dev', 'test') profiles can be specified!");

			if (production) {
				mode = EnvMode.PRODUCTION;

			} else if (dev) {
				mode = EnvMode.DEV;

			} else if (test) {
				mode = EnvMode.TEST;

			} else {
				throw Err.notExpected();
			}
		}

		String modeProfile = mode.name().toLowerCase();
		Log.info("Automatically activating mode-specific profile", "!profile", modeProfile);
		profiles.add(modeProfile);

		Log.info("Initialized environment", "!mode", mode, "!profiles", profiles);
	}

	private static EnvMode inferMode() {
		if (Msc.isInsideTest()) {
			return EnvMode.TEST;

		} else {
			if (Msc.dockerized()) return EnvMode.PRODUCTION;

			return ClasspathUtil.getClasspathFolders().isEmpty() ? EnvMode.PRODUCTION : EnvMode.DEV;
		}
	}

	private static List<String> retrieveProfiles() {
		String profilesLst = Env.initial("profiles");

		if (U.notEmpty(profilesLst)) {
			List<String> profiles = U.list(profilesLst.split("\\s*\\,\\s*"));
			Log.info("Configuring active profiles", "!profiles", profiles);
			return profiles;

		} else {
			Log.info("No profiles were specified, activating 'default' profile");
			return U.list("default");
		}
	}

	public void setArgs(String... args) {
		this.args = Coll.synchronizedList(args);
		this.argsView = Collections.unmodifiableList(this.args);
	}

	public List<String> args() {
		return argsView;
	}

	public boolean hasProfile(String profileName) {
		return profiles().contains(profileName);
	}

	public boolean hasAnyProfile(String... profileNames) {
		for (String profileName : profileNames) {
			if (hasProfile(profileName)) {
				return true;
			}
		}
		return false;
	}

	public synchronized void setProfiles(String... activeProfiles) {

		if (U.isEmpty(profiles)) {
			profiles = Coll.synchronizedSet();
			profilesView = Collections.unmodifiableSet(profiles);
		}

		Collections.addAll(this.profiles, activeProfiles);

		this.mode = null;
		initModeAndProfiles();

		Log.info("Activating custom profiles", "!activating", activeProfiles, "!resulting profiles", this.profiles, "!resulting mode", this.mode);
	}

	public boolean isInitialized() {
		return mode != null;
	}

	public EnvProperties properties() {
		return properties;
	}

	public Map<String, Object> argsAsMap() {
		U.notNull(args, "environment args");
		return Msc.parseArgs(args);
	}

}
