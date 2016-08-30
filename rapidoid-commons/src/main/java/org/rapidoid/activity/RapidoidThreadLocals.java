package org.rapidoid.activity;

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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.io.ByteArrayOutputStream;

@Authors("Nikolche Mihajlovski")
@Since("5.2.0")
public class RapidoidThreadLocals extends RapidoidThing {

	private static final ThreadLocal<RapidoidThreadLocals> THREAD_LOCALS = new ThreadLocal<RapidoidThreadLocals>() {
		@Override
		protected RapidoidThreadLocals initialValue() {
			return new RapidoidThreadLocals();
		}
	};

	public static RapidoidThreadLocals get() {
		Thread thread = Thread.currentThread();

		if (thread instanceof RapidoidThread) {
			return ((RapidoidThread) thread).locals();
		} else {
			return THREAD_LOCALS.get();
		}
	}

	private final ByteArrayOutputStream jsonRenderingStream = new ByteArrayOutputStream();

	private final ByteArrayOutputStream pageRenderingStream = new ByteArrayOutputStream();

	private final ByteArrayOutputStream templateRenderingStream = new ByteArrayOutputStream();

	public Object renderContext;

	public ByteArrayOutputStream jsonRenderingStream() {
		jsonRenderingStream.reset();
		return jsonRenderingStream;
	}

	public ByteArrayOutputStream pageRenderingStream() {
		pageRenderingStream.reset();
		return pageRenderingStream;
	}

	public ByteArrayOutputStream templateRenderingStream() {
		templateRenderingStream.reset();
		return templateRenderingStream;
	}

}
