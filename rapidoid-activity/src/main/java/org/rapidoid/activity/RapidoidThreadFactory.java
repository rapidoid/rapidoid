package org.rapidoid.activity;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class RapidoidThreadFactory implements ThreadFactory {

	private final String name;

	private final AtomicLong counter = new AtomicLong();

	public RapidoidThreadFactory(String name) {
		this.name = name;
	}

	@Override
	public Thread newThread(Runnable runnable) {
		RapidoidThread thread = new RapidoidThread(runnable);
		thread.setName(name + counter.incrementAndGet());
		return thread;
	}

}
