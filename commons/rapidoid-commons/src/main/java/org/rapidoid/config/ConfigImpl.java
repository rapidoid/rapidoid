package org.rapidoid.config;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.cls.Cls;
import org.rapidoid.collection.Coll;
import org.rapidoid.commons.Arr;
import org.rapidoid.commons.RapidoidInfo;
import org.rapidoid.commons.Str;
import org.rapidoid.env.Env;
import org.rapidoid.env.RapidoidEnv;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.lambda.Operation;
import org.rapidoid.log.GlobalCfg;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.value.Value;
import org.rapidoid.value.Values;
import org.rapidoid.wrap.BoolWrap;

import java.io.File;
import java.util.*;

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

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class ConfigImpl extends RapidoidThing implements Config {

	private final List<String> baseKeys;

	private final ConfigImpl root;

	private final ConfigBase base;

	private final boolean isRoot;

	public ConfigImpl() {
		this(null, false);
	}

	public ConfigImpl(Map<String, Object> entries) {
		this();
		update(entries);
	}

	public ConfigImpl(String defaultFilenameBase) {
		this(defaultFilenameBase, false);
	}

	public ConfigImpl(String defaultFilenameBase, boolean useBuiltInDefaults) {
		this.base = new ConfigBase(defaultFilenameBase, useBuiltInDefaults);
		this.root = this;
		this.baseKeys = U.list();
		this.isRoot = true;
	}

	private ConfigImpl(ConfigBase base, List<String> baseKeys, ConfigImpl root) {
		this.base = base;
		this.root = root;
		this.baseKeys = Collections.unmodifiableList(U.list(baseKeys));
		this.isRoot = false;
	}

	@Override
	public synchronized void reset() {
		clear();
		base.reset();
	}

	@Override
	public synchronized void invalidate() {
		clear();
		base.invalidate();
	}

	@Override
	public Value<Object> entry(String key) {
		return Values.wrap(new ConfigValueStore<Object>(this, key));
	}

	private List<String> keyChain(Iterator<String> keys) {
		List<String> keyChain = U.list(this.baseKeys);

		while (keys.hasNext()) {
			String key = keys.next();
			U.notNull(key, "config key");
			Collections.addAll(keyChain, key.split("\\."));
		}

		return keyChain;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Config sub(String... keys) {
		U.must(U.notEmpty(keys), "Keys must be specified!");
		return new ConfigImpl(base, keyChain(U.iterator(keys)), root());
	}

	@Override
	public Config sub(List<String> keys) {
		U.must(U.notEmpty(keys), "Keys must be specified!");
		return new ConfigImpl(base, keyChain(keys.iterator()), root());
	}

	@Override
	public synchronized Object get(String key) {
		makeSureIsInitialized();

		Object value;

		synchronized (base.properties) {
			value = asMap().get(key);
		}

		// if it's in the config, it's already overriden by the Env. If not, check manually:
		if (value == null) {
			value = globalOrArgConfigByRelativeKey(key);
		}

		return value;
	}

	private Object globalOrArgConfigByRelativeKey(String relKey) {
		String key = fullKey(relKey, ".");
		return globalOrArgConfig(key);
	}

	private String fullKey(String key, String separator) {
		return U.join(separator, Arr.concat(U.array(baseKeys), key));
	}

	@Override
	public synchronized boolean has(String key) {
		makeSureIsInitialized();

		return get(key) != null;
	}

	@Override
	public synchronized boolean is(String key) {
		makeSureIsInitialized();

		Object value = get(key);

		try {
			return Boolean.TRUE.equals(Cls.convert(value, Boolean.class));
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public synchronized Map<String, Object> toMap() {
		makeSureIsInitialized();

		return Collections.unmodifiableMap(asMap());
	}

	@Override
	public synchronized <T> Map<String, T> toMap(Class<T> type) {
		return Coll.toBeanMap(toMap(), type);
	}

	private Map<String, Object> asMap() {
		makeSureIsInitialized();

		if (isRoot) {
			return base.properties;

		} else {
			synchronized (base.properties) {
				Map<String, Object> props = base.properties;

				for (String key : baseKeys) {
					Object value = props.get(key);

					if (value == null) {
						value = Coll.synchronizedMap();
						props.put(key, value);
					}

					if (value instanceof Map<?, ?>) {
						props = (Map<String, Object>) value;
					} else {
						throw U.rte("Expected a Map for configuration section '%s', but found value of type: %s",
							sectionTo(key), value.getClass().getSimpleName());
					}
				}

				return props;
			}
		}
	}

	private String sectionTo(String toKey) {
		String section = "";

		for (String key : baseKeys) {
			if (!section.isEmpty()) {
				section += ".";
			}

			section += key;

			if (key.equals(toKey)) {
				break;
			}
		}

		return section;
	}

	@Override
	public synchronized void clear() {
		if (isRoot) {
			base.properties.clear();
		} else {
			synchronized (base.properties) {
				parent().remove(lastBaseKey());
			}
		}
	}

	private String lastBaseKey() {
		return baseKeys.get(baseKeys.size() - 1);
	}

	@Override
	public synchronized void remove(String key) {
		makeSureIsInitialized();

		synchronized (base.properties) {
			asMap().remove(key);
		}
	}

	@Override
	public synchronized void assign(Map<String, Object> entries) {
		makeSureIsInitialized();

		synchronized (base.properties) {
			clear();
			update(entries);
		}
	}

	@Override
	public synchronized boolean isEmpty() {
		makeSureIsInitialized();

		synchronized (base.properties) {
			return asMap().isEmpty() && !Env.properties().hasPrefix(U.join("_", baseKeys) + "_");
		}
	}

	@Override
	public synchronized void update(Map<String, ?> entries) {
		makeSureIsInitialized();

		update(entries, true);
	}

	@Override
	@SuppressWarnings("unchecked")
	public synchronized void update(Map<String, ?> entries, boolean overridenByEnv) {
		makeSureIsInitialized();

		synchronized (base.properties) {
			for (Map.Entry<String, ?> e : entries.entrySet()) {

				String name = e.getKey();
				Object value = e.getValue();

				if (value instanceof Map<?, ?>) {
					sub(name).update((Map<String, ?>) value, overridenByEnv);

				} else {
					set(name, value, overridenByEnv);
				}
			}
		}
	}

	@Override
	public synchronized void set(String key, Object value) {
		set(key, value, true);
	}

	@Override
	public synchronized void set(String key, Object value, boolean overridenByEnv) {
		makeSureIsInitialized();

		String[] keys = key.split("\\.");
		if (keys.length > 1) {
			Config cfg = sub(Arr.sub(keys, 0, -1));
			cfg.set(U.last(keys), value, overridenByEnv);
			return;
		}

		if (overridenByEnv) {
			value = U.or(globalOrArgConfigByRelativeKey(key), value);
		}

		synchronized (base.properties) {
			asMap().put(key, value);
		}
	}

	@Override
	public synchronized String toString() {
		makeSureIsInitialized();

		synchronized (base.properties) {
			return asMap().toString();
		}
	}

	@Override
	@Deprecated
	public synchronized void args(List<String> args) {
		mustBeRoot();
		base.initial.putAll(Msc.parseArgs(args));
	}

	private Object globalOrArgConfig(String key) {
		return U.or(base.initial.get(key), Env.properties().get(key));
	}

	private void mustBeRoot() {
		RapidoidEnv.touch();
		U.must(isRoot, "Must be Config's root!");
	}

	@Override
	public ConfigImpl root() {
		return root;
	}

	@Override
	public synchronized Config parent() {
		if (isRoot) return null;

		List<String> parentsKeys = baseKeys.subList(0, baseKeys.size() - 1);
		return parentsKeys.isEmpty() ? root : root.sub(parentsKeys);
	}

	@Override
	public List<String> keys() {
		return baseKeys;
	}

	@Override
	public synchronized Map<String, String> toFlatMap() {
		makeSureIsInitialized();

		Map<String, String> flatMap = U.map();
		Map<String, Object> map = toMap();

		traverseToFlat(map, U.list(keys()), flatMap);

		return flatMap;
	}

	private static void traverseToFlat(Map<String, Object> map, List<String> keys, Map<String, String> flatMap) {
		for (Map.Entry<String, Object> e : map.entrySet()) {
			String key = e.getKey();
			Object val = e.getValue();

			if (val instanceof Map<?, ?>) {
				Map<String, Object> mapVal = (Map<String, Object>) val;
				List<String> keys2 = U.list(keys);
				keys2.add(key);
				traverseToFlat(mapVal, keys2, flatMap);

			} else {
				flatMap.put(U.join(".", keys) + "." + key, String.valueOf(val));
			}
		}
	}

	@Override
	public synchronized Properties toProperties() {
		makeSureIsInitialized();

		Properties props = new Properties();
		props.putAll(toFlatMap());
		return props;
	}

	@Override
	public ConfigAlternatives or(BasicConfig alternative) {
		return new ConfigAlternatives(this, alternative);
	}

	@Override
	public String getFilenameBase() {
		return base.getFilenameBase();
	}

	@Override
	public synchronized Config setFilenameBase(String filenameBase) {
		mustBeRoot();

		if (base.setFilenameBase(filenameBase)) {
			invalidate(); // clear to apply changes
		}

		return this;
	}

	@Override
	public String getPath() {
		return base.getPath();
	}

	@Override
	public synchronized Config setPath(String path) {
		mustBeRoot();

		if (base.setPath(path)) {
			invalidate(); // clear to apply changes
		}

		if (new File(path).isAbsolute()) {
			Msc.watchForChanges(path, new Operation<String>() {
				@Override
				public void execute(String filename) throws Exception {
					onFileSystemChange(filename);
				}
			});
		}

		return this;
	}

	@Override
	public synchronized void applyTo(Object target) {
		makeSureIsInitialized();
		Beany.update(target, toMap());
	}

	private void makeSureIsInitialized() {
		if (base.initializing) {
			return;
		}

		if (!base.initialized) {
			synchronized (this) {
				if (!base.initialized) {
					base.initializing = true;
					root.initialize();
					base.initialized = true;
				}
			}
		}
	}

	protected synchronized void initialize() {
		mustBeRoot();

		List<String> loaded = U.list();

		base.initial.putAll(Env.argsAsMap());

		overrideByEnv();

		if (useBuiltInDefaults()) {
			ConfigLoaderUtil.loadBuiltInConfig(this, loaded);
		}

		boolean useConfigFiles = U.notEmpty(getFilenameBase());

		if (useConfigFiles) {
			ConfigLoaderUtil.loadConfig(this, loaded);
		}

		overrideByEnv();

		substitutePlaceholders();

		Conf.applyConfig(this);

		if (!loaded.isEmpty()) {
			Log.info("Loaded configuration", "namespace", getFilenameBase(), "!files", loaded);
		} else {
			if (useConfigFiles) {
				Log.warn("Didn't find any configuration files", "path", getPath());
			}
		}
	}

	private void substitutePlaceholders() {
		Map<String, String> flat = toFlatMap();
		Set<String> changedKeys = U.set();

		for (int i = 0; i < 1000; i++) {
			if (!substitutePlaceholders(flat, changedKeys)) {
				break;
			}
		}

		for (String key : changedKeys) {
			set(key, flat.get(key));
		}
	}

	private boolean substitutePlaceholders(final Map<String, String> flat, final Set<String> changedKeys) {
		final BoolWrap changed = new BoolWrap();

		for (Map.Entry<String, String> e : U.map(flat).entrySet()) {
			final String cfgKey = e.getKey();
			String val = e.getValue();

			if (val.contains("${")) {

				val = Str.replace(val, "\\$\\{([^\\}]+)\\}", new Mapper<String[], String>() {
					@Override
					public String map(String[] src) throws Exception {

						String name = src[1];
						Object value = flat.get(name);

						if (value == null) {
							value = GlobalCfg.get(name);
						}

						if (value == null) {
							value = getSpecialValue(name);
						}

						U.must(value != null, "Cannot find configuration entry '%s' for key '%s'!", name, cfgKey);

						changed.value = true;
						changedKeys.add(cfgKey);

						return value.toString();
					}
				});

				flat.put(cfgKey, val);
			}
		}

		return changed.value;
	}

	private Object getSpecialValue(String name) {

		switch (name.toLowerCase()) {

			case "system.cpus":
				return Runtime.getRuntime().availableProcessors();

			case "rapidoid.snapshot":
				return RapidoidInfo.isSnapshot();

			case "rapidoid.builton":
				return RapidoidInfo.builtOn();

			case "rapidoid.version":
				return RapidoidInfo.version();

			case "rapidoid.info":
				return RapidoidInfo.nameAndInfo();

			default:
				return null;
		}
	}

	private void overrideByEnv() {
		base.applyInitialConfig(this);
	}

	@Override
	public synchronized boolean useBuiltInDefaults() {
		return base.useBuiltInDefaults();
	}

	@Override
	public synchronized boolean isInitialized() {
		RapidoidEnv.touch();
		return base.initialized;
	}

	@Override
	public synchronized ConfigChanges getChangesSince(Config previousConfig) {

		Map<String, Object> prevMap;
		boolean initial = previousConfig == null;

		if (!initial) {
			prevMap = isRoot ? previousConfig.toMap() : previousConfig.sub(keys()).toMap();
		} else {
			prevMap = U.map();
		}

		return ConfigChanges.from(keys(), prevMap, toMap(), initial);
	}

	@Override
	public synchronized void addChangeListener(Operation<ConfigChanges> configChangeListener) {
		ConfigChangeListener listener = new ConfigChangeListener(keys(), configChangeListener);

		base.configChangesListeners.add(listener);

		// for the first time, call the listener with the current configuration as initial
		root().notifyChangeListener(listener, null);
	}

	@Override
	public synchronized void removeChangeListener(Operation<ConfigChanges> configChangeListener) {
		base.configChangesListeners.remove(new ConfigChangeListener(keys(), configChangeListener));
	}

	private synchronized void onFileSystemChange(String filename) {
		mustBeRoot();

		if (filename.endsWith(".yaml")
			|| filename.endsWith(".yml")
			|| filename.endsWith(".json")) {

			Log.info("Detected configuration file changes", "file", filename);

			reloadAndProcessChanges();
		}
	}

	private void reloadAndProcessChanges() {
		mustBeRoot();

		Set<ConfigChangeListener> listeners = U.set(base.configChangesListeners);

		if (listeners.isEmpty()) return;

		ConfigImpl previousConfig = new ConfigImpl(Coll.deepCopyOf(toMap()));

		// reload the configuration
		invalidate();
		makeSureIsInitialized();

		// notify listeners
		for (ConfigChangeListener listener : listeners) {
			notifyChangeListener(listener, previousConfig);
		}
	}

	private void notifyChangeListener(ConfigChangeListener listener, Config previousConfig) {
		mustBeRoot();

		Config cfg = U.notEmpty(listener.keys) ? sub(listener.keys) : this;
		ConfigChanges changes = cfg.getChangesSince(previousConfig);

		if (changes.count() > 0) {
			try {
				listener.operation.execute(changes);
			} catch (Exception e) {
				Log.error("Error occurred in a configuration changes listener!", e);
			}
		}
	}

	@Override
	public synchronized Config defaultOrCustom(String name) {
		U.must(U.notEmpty(name), "The configuration name cannot be empty! Use name 'default' for the default configuration.");

		if (name.equalsIgnoreCase("default")) return this;

		String configKey = U.last(keys()) + "-" + name;
		return parent().sub(configKey);
	}
}
