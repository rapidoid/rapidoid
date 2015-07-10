package org.rapidoidx.http.client;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.job.Jobs;
import org.rapidoid.lambda.Callback;
import org.rapidoidx.buffer.Buf;
import org.rapidoidx.data.Ranges;

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

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class HttpClientBodyCallback implements HttpClientCallback {

	private final Callback<String> bodyCallback;

	public HttpClientBodyCallback(Callback<String> bodyCallback) {
		this.bodyCallback = bodyCallback;
	}

	@Override
	public void onResult(Buf buffer, Ranges head, Ranges body) {
		Jobs.call(bodyCallback, body.getConcatenated(buffer.bytes(), 0, body.count - 1, ""), null);
	}

	@Override
	public void onError(Throwable error) {
		Jobs.call(bodyCallback, null, error);
	}

}
