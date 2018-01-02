/*-
 * #%L
 * rapidoid-integration-tests
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
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

package tcpdemo;

import org.rapidoid.buffer.Buf;
import org.rapidoid.serialize.Serialize;
import org.rapidoid.u.U;

public class Serializer {

	static void serialize(Buf out, Object value) {
		byte[] bytes = new byte[200];
		int n = Serialize.serialize(bytes, value);
		U.must(n > 0 && n < bytes.length - 1);

		out.append(n + "\n");
		out.append(bytes, 0, n);
	}

	static Object deserialize(Buf in) {
		int n = U.num(in.readLn());
		U.must(n > 0 && n < 200);

		byte[] bytes = in.readNbytes(n);
		return Serialize.deserialize(bytes);
	}

}
