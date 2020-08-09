/*-
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

package tcpdemo;

import org.rapidoid.insight.Insights;
import org.rapidoid.insight.StatsMeasure;
import org.rapidoid.net.TCP;
import org.rapidoid.net.TCPClient;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.u.U;

public class TCPClientDemo {

    private static final int PIPELINE = 50;

    private static StatsMeasure resp = Insights.stats("resp");

    public static void main(String[] args) {
        new TCPClientDemo().run();
    }

    public void run() {
        TCPClient client = TCP.client()
                .host("127.0.0.1")
                .port(5555)
                .connections(10)
                .protocol(this::client)
                .build();

        client.start();

        Insights.show();
    }

    private void client(Channel ch) {
        for (int i = 0; i < PIPELINE; i++) {
            Serializer.serialize(ch.output(), msg("abc", i));
            ch.send();
        }

        for (int i = 0; i < PIPELINE; i++) {
            Object s = Serializer.deserialize(ch.input());
            U.must(U.eq(msg("abc", i), s));
            resp.tick();
        }
    }

    private Object msg(String s, int n) {
        return U.map("x", n, "s", s);
    }

}
