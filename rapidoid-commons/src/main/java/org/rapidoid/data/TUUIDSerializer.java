package org.rapidoid.data;

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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.TUUID;

import java.io.IOException;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class TUUIDSerializer extends JsonSerializer<TUUID> {

	@Override
	public void serialize(TUUID tuuid, JsonGenerator generator, SerializerProvider serializerProvider) throws IOException {
		generator.writeString(tuuid.toString());
	}

	@Override
	public Class<TUUID> handledType() {
		return TUUID.class;
	}

}
