package org.rapidoid.process;

import org.rapidoid.RapidoidThing;
import org.rapidoid.activity.RapidoidThread;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.commons.Arr;
import org.rapidoid.group.Manageable;
import org.rapidoid.lambda.Lmbd;
import org.rapidoid.lambda.Operation;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

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
@Since("5.3.0")
public class ProcessHandle extends RapidoidThing implements Manageable {

	private final static Set<ProcessHandle> ALL = Coll.synchronizedSet();

	private final static ProcessCrawlerThread CRAWLER = new ProcessCrawlerThread(ALL);

	private final String id = UUID.randomUUID().toString();

	private volatile Processes group;

	private final BlockingQueue<Object> input = new ArrayBlockingQueue<Object>(100);

	private final BlockingQueue<String> output = new ArrayBlockingQueue<String>(100);

	private final BlockingQueue<String> error = new ArrayBlockingQueue<String>(100);

	private final StringBuffer outBuffer = new StringBuffer();
	private final StringBuffer errBuffer = new StringBuffer();
	private final StringBuffer outAndErrBuffer = new StringBuffer();

	private final ProcessParams params;
	private final Process process;

	private volatile boolean inputDone;
	private volatile boolean outputDone;
	private volatile boolean errorDone;

	private volatile long startedAt;
	private volatile long finishedAt;

	private ProcessHandle(ProcessParams params, final Process process) {
		this.params = params;
		this.process = process;

		Thread inputProcessor = new RapidoidThread() {
			@Override
			public void run() {
				try {
					writeAll(input, process.getOutputStream());
				} finally {
					outputDone = true;
				}
			}
		};

		inputProcessor.setDaemon(true);
		inputProcessor.start();

		Thread errorProcessor = new RapidoidThread() {
			@Override
			public void run() {
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
					long total = readInto(reader, error, errBuffer, outAndErrBuffer);
				} finally {
					errorDone = true;
				}
			}
		};

		errorProcessor.setDaemon(true);
		errorProcessor.start();

		Thread outputProcessor = new RapidoidThread() {
			@Override
			public void run() {
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					long total = readInto(reader, output, outBuffer, outAndErrBuffer);

				} finally {
					outputDone = true;
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

	private static long readInto(BufferedReader reader, BlockingQueue<String> dest, StringBuffer... buffers) {
		long total = 0;

		try {
			String line;
			while ((line = reader.readLine()) != null) {
				try {
					dest.put(line);

					for (StringBuffer buffer : buffers) {
						buffer.append(line + "\n");
					}

					total++;
				} catch (InterruptedException e) {
					throw new CancellationException();
				}
			}
		} catch (IOException e) {
			Log.error("Cannot read!", e);
		}

		return total;
	}

	public BlockingQueue<Object> input() {
		return input;
	}

	public BlockingQueue<String> output() {
		return output;
	}

	public BlockingQueue<String> error() {
		return error;
	}

	public Process process() {
		return process;
	}

	public ProcessParams params() {
		return params;
	}

	public boolean isAlive() {
		return exitCode() == null;
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

		} while (isAlive() || !outputDone || !errorDone || (--grace) >= 0);
	}

	public void print() {
		U.print(outAndError());
	}

	public String out() {
		return outBuffer.toString();
	}

	public String err() {
		return errBuffer.toString();
	}

	public String outAndError() {
		return outAndErrBuffer.toString();
	}

	public static ProcessHandle startProcess(ProcessParams params) {

		ProcessBuilder builder = new ProcessBuilder().command(params.command());

		if (params.in() != null) {
			builder.directory(params.in());
		}

		long startingAt = U.time();

		Process process;
		try {
			process = builder.start();
		} catch (IOException e) {
			throw U.rte("Cannot start process: " + U.join(" ", params.command()));
		}

		ProcessHandle handle = new ProcessHandle(params, process);
		handle.startedAt = startingAt;

		handle.group = params.group();
		handle.group.add(handle);

		ALL.add(handle);

		synchronized (CRAWLER) {
			if (CRAWLER.getState() == Thread.State.NEW) CRAWLER.start();
		}


		return handle;
	}

	public ProcessHandle waitFor() {
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			throw new CancellationException();
		}

		return this;
	}

	public ProcessHandle waitFor(long timeout, TimeUnit unit) {
		try {
			process.waitFor(timeout, unit);
		} catch (InterruptedException e) {
			throw new CancellationException();
		}

		return this;
	}

	public ProcessHandle destroy() {
		process.destroy();
		return this;
	}

	public ProcessHandle destroyForcibly() {
		process.destroyForcibly();
		return this;
	}

	public String cmd() {
		return params.command()[0];
	}

	public String[] args() {
		return Arr.sub(params.command(), 1, params().command().length);
	}

	public Integer exitCode() {
		try {
			return process.exitValue();
		} catch (IllegalThreadStateException e) {
			return null;
		}
	}

	public long duration() {
		if (this.startedAt <= 0) return 0;

		long until = this.finishedAt;
		if (until == 0) until = U.time();

		return until - this.startedAt;
	}

	void onTerminated() {
		ALL.remove(this);
		finishedAt = U.time();
	}

	public long startedAt() {
		return startedAt;
	}

	public long finishedAt() {
		return finishedAt;
	}

	@Override
	public String id() {
		return id;
	}

	public Processes group() {
		return group;
	}
}
