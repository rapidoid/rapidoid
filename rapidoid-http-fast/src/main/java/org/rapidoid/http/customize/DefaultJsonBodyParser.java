package org.rapidoid.http.customize;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class DefaultJsonBodyParser implements JsonBodyParser {

	@Override
	public Map<String, Object> parseJsonBody(byte[] jsonBody) throws Exception {
		return null; // FIXME
	}

}
