package org.rapidoid.http.customize;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.HttpUtils;
import org.rapidoid.http.Req;
import org.rapidoid.http.Resp;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class DefaultErrorHandler implements ErrorHandler {

	@Override
	public Object handleError(Req req, Resp resp, Throwable error) {
		return HttpUtils.getErrorMessage(error);
	}

}
