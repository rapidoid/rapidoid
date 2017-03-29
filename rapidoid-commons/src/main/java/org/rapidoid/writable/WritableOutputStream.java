package org.rapidoid.writable;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

import java.io.IOException;
import java.io.OutputStream;

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
@Since("5.3.4")
public class WritableOutputStream extends RapidoidThing implements Writable {

	private final OutputStream outputStream;

	public WritableOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	@Override
	public void writeByte(byte byteValue) {
		try {
			outputStream.write(byteValue);
		} catch (IOException e) {
			throw U.rte(e);
		}
	}

	@Override
	public void writeBytes(byte[] src) {
		try {
			outputStream.write(src);
		} catch (IOException e) {
			throw U.rte(e);
		}
	}

	@Override
	public void writeBytes(byte[] src, int offset, int length) {
		try {
			outputStream.write(src, offset, length);
		} catch (IOException e) {
			throw U.rte(e);
		}
	}
}
