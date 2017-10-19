package org.rapidoid.data;

import org.rapidoid.RapidoidThing;

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

/**
 * @author Nikolche Mihajlovski
 * @since 4.4.0
 */
public class Parse extends RapidoidThing {

	enum DataFormat {
		JSON, XML, YAML
	}

	private static final DataFormat[] XML_FIRST = {DataFormat.XML, DataFormat.JSON, DataFormat.YAML};

	private static final DataFormat[] JSON_FIRST = {DataFormat.JSON, DataFormat.XML, DataFormat.YAML};

	private static final DataFormat[] YAML_FIRST = {DataFormat.YAML, DataFormat.JSON, DataFormat.XML};

	public static <T> T data(String data, Class<T> targetType) {
		return data(data.getBytes(), targetType);
	}

	@SuppressWarnings("unchecked")
	public static <T> T data(byte[] data, Class<T> targetType) {
		if (data == null) {
			return null;
		}

		// don't parse if the target type is byte[]
		if (targetType == byte[].class) {
			return (T) data;
		}

		DataFormat[] formatsOrder = detectDataFormat(data);

		return tryToParseData(data, formatsOrder, targetType);
	}

	private static <T> T tryToParseData(byte[] data, DataFormat[] formatsOrder, Class<T> targetType) {

		Exception firstException = null;

		for (DataFormat dataFormat : formatsOrder) {
			switch (dataFormat) {
				case JSON:
					try {
						return JSON.parse(data, targetType);
					} catch (Exception e) {
						if (firstException == null) {
							firstException = e;
						}
					}
					break;

				case XML:
					try {
						return XML.parse(data, targetType);
					} catch (Exception e) {
						if (firstException == null) {
							firstException = e;
						}
					}
					break;

				case YAML:
					try {
						return YAML.parse(data, targetType);
					} catch (Exception e) {
						if (firstException == null) {
							firstException = e;
						}
					}
					break;

				default:
					break;
			}
		}

		throw new RuntimeException("Not a valid JSON, XML nor YAML format!", firstException);
	}

	static DataFormat[] detectDataFormat(byte[] data) {
		if (data.length == 0) {
			return YAML_FIRST;
		}

		byte start = findFirstNonWhitespaceByte(data);

		if (start == '<') {
			return XML_FIRST;
		} else if (start == ' ' || (start == '-' && dataStartsWithYAMLMark(data))) {
			return YAML_FIRST;
		} else {
			return JSON_FIRST;
		}
	}

	private static byte findFirstNonWhitespaceByte(byte[] data) {
		int i = 0;

		while (Character.isWhitespace(data[i])) {
			i++;
			if (i >= data.length) {
				return ' ';
			}
		}

		return data[i];
	}

	private static boolean dataStartsWithYAMLMark(byte[] data) {
		return data.length >= 3 && data[0] == '-' && data[1] == '-' && data[2] == '-';
	}

}
