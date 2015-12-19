package org.rapidoid.config;

import java.util.Map;

public interface ConfigParser {

	Map<String, Object> parse(byte[] bytes);

}
