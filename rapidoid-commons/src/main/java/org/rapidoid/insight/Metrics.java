package org.rapidoid.insight;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.TimeSeries;
import org.rapidoid.job.Jobs;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class Metrics implements Runnable {

	public static final TimeSeries SYSTEM_CPU = new TimeSeries();
	public static final TimeSeries PROCESS_CPU = new TimeSeries();

	public static final TimeSeries MEM_USED = new TimeSeries();
	public static final TimeSeries MEM_TOTAL = new TimeSeries();

	private static volatile OperatingSystemMXBean os;
	private static volatile Method sysCpuM;
	private static volatile Method procCpuM;

	static {
		Log.info("Initializing metrics");

		os = ManagementFactory.getOperatingSystemMXBean();
		sysCpuM = Cls.findMethod(os.getClass(), "getSystemCpuLoad");
		procCpuM = Cls.findMethod(os.getClass(), "getProcessCpuLoad");

		Jobs.every(1, TimeUnit.SECONDS).run(new Metrics());
	}

	@Override
	public void run() {
		Runtime rt = Runtime.getRuntime();

		long totalMem = rt.totalMemory();
		long maxMem = rt.maxMemory();
		long freeMem = rt.freeMemory();
		long usedMem = totalMem - freeMem;

		double megs = 1024.0 * 1024;

		MEM_TOTAL.put(U.time(), totalMem / megs);
		MEM_USED.put(U.time(), usedMem / megs);

		if (sysCpuM != null) {
			SYSTEM_CPU.put(U.time(), (Double) Cls.invoke(sysCpuM, os));
		}

		if (procCpuM != null) {
			PROCESS_CPU.put(U.time(), (Double) Cls.invoke(procCpuM, os));
		}
	}

}
