package org.rapidoid.ioc;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class IoCContextChanges {

	private final List<Object> loadedInstances;

	private final List<Object> removedInstances;

	public IoCContextChanges(List<Object> loadedInstances, List<Object> removedInstances) {
		this.loadedInstances = loadedInstances;
		this.removedInstances = removedInstances;
	}

	public List<Object> getLoadedInstances() {
		return loadedInstances;
	}

	public List<Object> getRemovedInstances() {
		return removedInstances;
	}

}
