package org.rapidoid.restapi;/*-
 * #%L
 * rapidoid-rest
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

import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.data.XML;
import org.rapidoid.http.IsolatedIntegrationTest;
import org.rapidoid.http.ReqHandler;
import org.rapidoid.http.Self;
import org.rapidoid.setup.My;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

import java.util.Map;

@Authors({"Dan Cytermann", "Nikolche Mihajlovski"})
@Since("5.5.0")
public class HttpXmlAPITest extends IsolatedIntegrationTest {

    @Test
    public void testXmlAPI() {
        On.get("/inc/{x}").xml((ReqHandler) req -> U.num(req.param("x")) + 1);

        eq(Self.get("/inc/99").fetch(), "<Integer>100</Integer>");
    }

    @Test
    public void testXmlRequestBody() {
        My.xmlMapper(XML.newMapper());

        On.post("/echo").xml((ReqHandler) req -> {
            Point point = new Point();
            point.coordinates = req.data();
            return point;
        });

        String resp = Self.post("/echo")
                .body("<point><x>12.3</x><y>456</y></point>".getBytes())
                .contentType("application/xml")
                .fetch();

        eq(resp, "<Point><coordinates><x>12.3</x><y>456</y></coordinates></Point>");
    }

}

class Point {
    public Map<String, Object> coordinates;
}
