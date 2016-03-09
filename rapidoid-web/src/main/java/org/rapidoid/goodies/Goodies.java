package org.rapidoid.goodies;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.lang.management.ManagementFactory;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class Goodies {

	public static MultiDetailsHandler memoryPool() {
		return new MultiDetailsHandler("Memory pool", ManagementFactory.getMemoryPoolMXBeans(), "name", "type", "memoryManagerNames", "usage", "peakUsage", "collectionUsage");
	}

	public static DetailsHandler classes() {
		return new DetailsHandler("Classes", ManagementFactory.getClassLoadingMXBean(), "-objectName");
	}

	public static DetailsHandler os() {
		return new DetailsHandler("Operating system", ManagementFactory.getOperatingSystemMXBean(), "-objectName");
	}

	public static DetailsHandler threads() {
		return new DetailsHandler("Threads", ManagementFactory.getThreadMXBean(), "-objectName", "-allThreadIds");
	}

	public static DetailsHandler compilation() {
		return new DetailsHandler("Compilation", ManagementFactory.getCompilationMXBean(), "-objectName");
	}

	public static DetailsHandler runtime() {
		return new DetailsHandler("Runtime", ManagementFactory.getRuntimeMXBean(), "-objectName", "-classPath", "-bootClassPath", "-systemProperties");
	}

	public static MultiDetailsHandler gc() {
		return new MultiDetailsHandler("Garbage collection", ManagementFactory.getGarbageCollectorMXBeans(), "-objectName", "-memoryPools", "-lastGcInfo");
	}

	public static DetailsHandler memory() {
		return new DetailsHandler("Memory", ManagementFactory.getMemoryMXBean(), "-objectName");
	}

	public static GraphsHandler graphs() {
		return new GraphsHandler();
	}

}
