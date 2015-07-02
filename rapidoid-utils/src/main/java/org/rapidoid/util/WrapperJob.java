package org.rapidoid.util;

import org.rapidoid.ctx.Ctx;

public class WrapperJob implements Runnable {

	private final Runnable job;

	public WrapperJob(Runnable job) {
		this.job = job;
	}

	@Override
	public void run() {
		try {
			job.run();
		} finally {
			Ctx.clear();
		}
	}

}
