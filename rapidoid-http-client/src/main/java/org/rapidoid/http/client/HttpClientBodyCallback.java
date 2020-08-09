/*-
 * #%L
 * rapidoid-http-client
 * %%
 * Copyright (C) 2014 - 2020 Nikolche Mihajlovski and contributors
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

package org.rapidoid.http.client;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.data.BufRanges;
import org.rapidoid.job.Jobs;

@Authors("Nikolche Mihajlovski")
@Since("5.5.0")
public class HttpClientBodyCallback extends RapidoidThing implements HttpClientCallback {

    private final Callback<String> bodyCallback;

    public HttpClientBodyCallback(Callback<String> bodyCallback) {
        this.bodyCallback = bodyCallback;
    }

    @Override
    public void onResult(Buf buffer, BufRanges head, BufRanges body) {
        Jobs.call(bodyCallback, body.getConcatenated(buffer.bytes(), 0, body.count - 1, ""), null);
    }

    @Override
    public void onError(Throwable error) {
        Jobs.call(bodyCallback, null, error);
    }

}
