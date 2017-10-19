package org.rapidoid.process;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.commons.Arr;
import org.rapidoid.commons.RapidoidInfo;
import org.rapidoid.group.AbstractManageable;
import org.rapidoid.group.ManageableBean;
import org.rapidoid.lambda.Lmbd;
import org.rapidoid.lambda.Operation;
import org.rapidoid.log.GlobalCfg;
import org.rapidoid.log.Log;
import org.rapidoid.log.LogLevel;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.util.SlidingWindowList;
import org.rapidoid.util.Wait;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
@Since("5.3.0")
@ManageableBean(kind = "processes")
public class ProcessHandle extends AbstractManageable {

	private final static Set<ProcessHandle> ALL = Coll.synchronizedSet();

	private final static ProcessCrawlerThread CRAWLER = new ProcessCrawlerThread(ALL);

	private final ProcessParams params;

	private final String id;

	private final BlockingQueue<Object> input = new ArrayBlockingQueue<>(100);
	private final BlockingQueue<String> output = null;
	private final BlockingQueue<String> error = null;

	private final List<String> outBuffer;
	private final List<String> errBuffer;
	private final List<String> outAndErrBuffer;

	private final AtomicBoolean doneReadingOut = new AtomicBoolean();
	private final AtomicBoolean doneReadingErr = new AtomicBoolean();

	private final int terminationTimeout;

	private volatile Process process;

	private volatile Date startedAt;
	private volatile Date finishedAt;

	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				terminateProcesses();
			}
		});
	}

	private static void terminateProcesses() {
		for (ProcessHandle proc : Coll.copyOf(ALL)) {
			proc.terminate();
		}
	}

	ProcessHandle(ProcessParams params) {
		this.params = params;
		this.id = params.id() != null ? params.id() : UUID.randomUUID().toString();

		this.outBuffer = Collections.synchronizedList(new SlidingWindowList<String>(params.maxLogLines()));
		this.errBuffer = Collections.synchronizedList(new SlidingWindowList<String>(params.maxLogLines()));
		this.outAndErrBuffer = Collections.synchronizedList(new SlidingWindowList<String>(params.maxLogLines()));

		this.terminationTimeout = params.terminationTimeout();

		// keep reference to the handle, used by the crawler internally
		ALL.add(this);

		// register to the process group, if configured
		if (params.group() != null) {
			params.group().add(this);
		}

		setupIO();
	}

	private void setupIO() {
		Thread inputProcessor = new ProcessIOThread(this) {
			@Override
			void doIO() {
				writeAll(input, process.getOutputStream());
			}
		};

		inputProcessor.setDaemon(true);
		inputProcessor.start();

		Thread errorProcessor = new ProcessIOThread(this) {
			@Override
			void doIO() {
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
					readInto(reader, error, errBuffer, outAndErrBuffer);
				} finally {
					doneReadingErr.set(true);
				}
			}
		};

		errorProcessor.setDaemon(true);
		errorProcessor.start();

		Thread outputProcessor = new ProcessIOThread(this) {
			@Override
			void doIO() {
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					readInto(reader, output, outBuffer, outAndErrBuffer);
				} finally {
					doneReadingOut.set(true);
				}
			}
		};

		outputProcessor.setDaemon(true);
		outputProcessor.start();
	}

	private static void writeAll(BlockingQueue<Object> input, OutputStream output) {
		while (!Thread.interrupted()) {
			try {
				Object obj = input.take();

				if (obj instanceof String) {
					String s = (String) obj;
					output.write(s.getBytes());

				} else if (obj instanceof byte[]) {
					byte[] b = (byte[]) obj;
					output.write(b);

				} else {
					throw U.rte("Unsupported input object type: " + obj);
				}

				output.flush();
			} catch (Exception e) {
				Log.error("Cannot write!", e);
			}
		}
	}

	private long readInto(BufferedReader reader, BlockingQueue<String> dest, List<String>... buffers) {
		long total = 0;

		try {
			String line;
			while ((line = reader.readLine()) != null) {
				try {
					if (dest != null) {
						dest.put(line);
					}

					if (params.printingOutput()) {
						U.print(params.linePrefix() + line);
					}

					for (List<String> buffer : buffers) {
						buffer.add(line);
					}

					total++;
				} catch (InterruptedException e) {
					throw new CancellationException();
				}
			}
		} catch (IOException e) {
			// can't read anymore (e.g. the stream was closed)
		}

		return total;
	}

	public synchronized BlockingQueue<Object> input() {
		return input;
	}

	public synchronized BlockingQueue<String> output() {
		return output;
	}

	public synchronized BlockingQueue<String> error() {
		return error;
	}

	public synchronized Process process() {
		return process;
	}

	public synchronized ProcessParams params() {
		return params;
	}

	public synchronized boolean isAlive() {
		return process != null && exitCode() == null;
	}

	public void receive(Operation<String> outputProcessor, Operation<String> errorProcessor) {
		String s;

		int grace = 1;

		do {
			if (outputProcessor != null) {
				while ((s = output().poll()) != null) {
					Lmbd.call(outputProcessor, s);
				}
			}

			if (errorProcessor != null) {
				while ((s = error().poll()) != null) {
					Lmbd.call(errorProcessor, s);
				}
			}

			U.sleep(10);

		} while (isAlive() || !doneReadingOut.get() || !doneReadingErr.get() || (--grace) >= 0);
	}

	public void print() {
		for (String line : outAndError()) {
			U.print(line);
		}
	}

	public void log(LogLevel level) {
		for (String line : outAndError()) {
			Log.log("PROCESS", level, line);
		}
	}

	public List<String> out() {
		return outBuffer;
	}

	public List<String> err() {
		return errBuffer;
	}

	public List<String> outAndError() {
		return outAndErrBuffer;
	}

	synchronized void startProcess(ProcessParams params) {

		Log.info("Starting process", "command", params.command());

		ProcessBuilder builder = new ProcessBuilder().command(params.command());

		if (params.in() != null) {
			builder.directory(params.in());
		}

		removeRapidoidConfig(builder.environment());

		addExtraEnvInfo(builder.environment());

		Date startingAt = new Date();

		Process process;
		try {
			process = builder.start();
		} catch (IOException e) {
			throw U.rte("Cannot start process: " + U.join(" ", params.command()));
		}

		this.startedAt = startingAt;
		this.finishedAt = null;
		this.doneReadingErr.set(false);
		this.doneReadingOut.set(false);

		attach(process);

		synchronized (CRAWLER) {
			if (CRAWLER.getState() == Thread.State.NEW) CRAWLER.start();
		}
	}

	private void addExtraEnvInfo(Map<String, String> env) {
		env.put(GlobalCfg.MANAGED_BY, RapidoidInfo.nameAndInfo());
	}

	private void removeRapidoidConfig(Map<String, String> env) {
		Iterator<Map.Entry<String, String>> it = env.entrySet().iterator();
		while (it.hasNext()) {

			Map.Entry<String, String> e = it.next();
			String key = e.getKey().toUpperCase();

			if (key.startsWith("RAPIDOID_") || key.startsWith("RAPIDOID.")) {
				it.remove();
			}
		}
	}

	private void attach(Process process) {
		this.process = process;
	}

	private synchronized Process requireProcess() {
		U.must(process != null, "The handle must have a process attached!");
		return process;
	}

	public ProcessHandle waitFor() {
		try {
			requireProcess().waitFor();
		} catch (InterruptedException e) {
			throw new CancellationException();
		}

		Wait.until(doneReadingOut);
		Wait.until(doneReadingErr);

		return this;
	}

	public ProcessHandle waitFor(long timeout, TimeUnit unit) {
		try {
			requireProcess().waitFor(timeout, unit);
		} catch (InterruptedException e) {
			throw new CancellationException();
		}

		// FIXME timeout
		Wait.until(doneReadingOut);
		Wait.until(doneReadingErr);

		return this;
	}

	public ProcessHandle destroy() {
		if (process != null) process.destroy();
		return this;
	}

	public ProcessHandle destroyForcibly() {
		if (process != null) process.destroyForcibly();
		return this;
	}

	public synchronized String cmd() {
		return params.command()[0];
	}

	public synchronized String[] args() {
		return Arr.sub(params.command(), 1, params().command().length);
	}

	public synchronized Integer exitCode() {
		try {
			return process != null ? process.exitValue() : null;
		} catch (IllegalThreadStateException e) {
			return null;
		}
	}

	public synchronized long duration() {
		if (this.startedAt == null) return 0;

		Date until = this.finishedAt;
		if (until == null) until = new Date();

		return until.getTime() - this.startedAt.getTime();
	}

	synchronized void onTerminated() {
		finishedAt = new Date();
	}

	public synchronized Date startedAt() {
		return startedAt;
	}

	public synchronized Date finishedAt() {
		return finishedAt;
	}

	@Override
	public synchronized String id() {
		return id;
	}

	@Override
	public synchronized List<String> getManageableActions() {
		List<String> actions = U.list("?Restart");

		if (isAlive()) {
			actions.add("!Terminate");
		}

		return actions;
	}

	public synchronized Processes group() {
		return params.group();
	}

	public synchronized ProcessHandle restart() {
		terminate();

		startProcess(params);

		return this;
	}

	public synchronized ProcessHandle terminate() {
		// keep reference to the handle, used by the crawler internally
		ALL.remove(this);

		destroy();

		long t = U.time();
		while (isAlive()) {
			U.sleep(1);

			if (Msc.timedOut(t, terminationTimeout)) {
				destroyForcibly();
				break;
			}
		}

		t = U.time();
		while (isAlive()) {
			U.sleep(1);

			if (Msc.timedOut(t, terminationTimeout)) {
				throw U.rte("Couldn't terminate the process!");
			}
		}

		Log.info("Terminated process", "id", id());

		return this;
	}

}
