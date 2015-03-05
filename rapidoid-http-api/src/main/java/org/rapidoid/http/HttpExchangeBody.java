package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http-api
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.mime.MediaType;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public interface HttpExchangeBody {

	HttpExchangeBody sendFile(File file);

	HttpExchangeBody sendFile(MediaType mediaType, byte[] bytes);

	HttpSuccessException redirect(String url);

	HttpSuccessException goBack(int steps);

	HttpExchangeBody addToPageStack();

	OutputStream outputStream();

	HttpExchangeBody write(String s);

	HttpExchangeBody writeln(String s);

	HttpExchangeBody write(byte[] bytes);

	HttpExchangeBody write(byte[] bytes, int offset, int length);

	HttpExchangeBody write(ByteBuffer buf);

	HttpExchangeBody write(File file);

	HttpExchangeBody writeJSON(Object value);

	HttpExchangeBody send();

	// due to async() web handling option, it ain't over till the fat lady sings "done"
	HttpExchangeBody async();

	HttpExchangeBody done();

}
