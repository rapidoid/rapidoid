package org.rapidoidx.http.client;

/*
 * #%L
 * rapidoid-x-http-client
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.ResultOrError;
import org.rapidoidx.buffer.Buf;
import org.rapidoidx.data.Range;
import org.rapidoidx.data.Ranges;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class BlockingHttpClientCallback implements HttpClientCallback {

	private final ResultOrError<byte[]> resultOrError = new ResultOrError<byte[]>();

	@Override
	public void onResult(Buf buffer, Ranges head, Ranges body) {
		Range whole = new Range();
		whole.start = head.ranges[0].start;
		whole.length = body.last().start + body.last().length;
		resultOrError.setResult(whole.bytes(buffer));
	}

	@Override
	public void onError(Throwable error) {
		resultOrError.setError(error);
	}

	public byte[] getResponse() {
		return resultOrError.get();
	}

}
