package org.rapidoid.goodies;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.GUI;
import org.rapidoid.insight.Metrics;

import java.util.concurrent.Callable;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class GraphsHandler extends GUI implements Callable<Object> {

	@Override
	public Object call() throws Exception {
		Object sysCpu = dygraph("System CPU", Metrics.SYSTEM_CPU);
		Object userCpu = dygraph("Process CPU", Metrics.PROCESS_CPU);

		Object memTotal = dygraph("Total JVM memory (MB)", Metrics.MEM_TOTAL);
		Object memUsed = dygraph("Used JVM memory (MB)", Metrics.MEM_USED);

		return multi(row(col6(sysCpu), col6(userCpu)), row(col6(memTotal), col6(memUsed)));
	}

}
