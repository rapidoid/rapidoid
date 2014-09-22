package org.rapidoid.net.abstracts;

/*
 * #%L
 * rapidoid-net
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import java.io.File;
import java.nio.ByteBuffer;

public interface CtxWrite<T> {

	T write(String s);

	T write(byte[] bytes);

	T write(byte[] bytes, int offset, int length);

	T write(ByteBuffer buf);

	T write(File file);

	T writeJSON(Object value);

	// due to async() web handling option, it ain't over till the fat lady sings "done"
	T async();

	T done();

}
