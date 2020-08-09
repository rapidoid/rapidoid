/*-
 * #%L
 * rapidoid-http-server
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

package org.rapidoid.http.customize.defaults;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.*;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.customize.ErrorHandler;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class DefaultErrorHandler extends RapidoidThing implements ErrorHandler {

    @Override
    public Object handleError(Req req, Resp resp, Throwable error) {

        resp.result(null);

        Customization custom = Customization.of(req);

        Object result = handleError(req, resp, error, custom);

        if (result instanceof Throwable) {
            Throwable errResult = (Throwable) result;
            return renderError(req, resp, errResult);

        } else {
            return result;
        }
    }

    private Object renderError(Req req, Resp resp, Throwable error) {

        if (resp.contentType() == MediaType.JSON) {
            return HttpUtils.getErrorInfo(resp, error);

        } else {
            return HttpUtils.getErrorMessageAndSetCode(resp, error);
        }
    }

    protected Object handleError(Req req, Resp resp, Throwable error, Customization custom) {
        // if the handler throws error -> process it
        for (int i = 0; ; i++) {

            resp.result(null);

            ErrorHandler handler = custom.findErrorHandlerByType(error);

            try {

                Object result = null;

                if (handler != null) {
                    result = handler.handleError(req, resp, error);
                }

                return result != null ? result : defaultErrorHandling(req, error);

            } catch (Exception e) {

                if (i >= getMaxReThrowCount(req)) {
                    return U.rte("Too many times an error was re-thrown by the error handler(s)!");
                }

                error = e;
            }
        }
    }

    protected int getMaxReThrowCount(@SuppressWarnings("UnusedParameters") Req req) {
        return 5; // override to customize
    }

    protected Object defaultErrorHandling(Req req, Throwable error) {

        if (error instanceof NotFound) {
            Resp resp = req.response().code(404);

            if (resp.contentType() == MediaType.JSON) {
                return error;
            } else {
                return "";
            }
        }

        return error;
    }


}
