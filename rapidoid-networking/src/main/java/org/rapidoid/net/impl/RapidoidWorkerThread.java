/*-
 * #%L
 * rapidoid-networking
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

package org.rapidoid.net.impl;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.net.NetworkingParams;
import org.rapidoid.net.TLSParams;
import org.rapidoid.thread.RapidoidThread;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class RapidoidWorkerThread extends RapidoidThread {

    private final int workerIndex;
    private final NetworkingParams net;
    private final TLSParams tlsParams;

    private volatile RapidoidWorker worker;

    RapidoidWorkerThread(int workerIndex, NetworkingParams net, TLSParams tlsParams) {
        super("server" + (workerIndex + 1));

        this.workerIndex = workerIndex;
        this.net = net;
        this.tlsParams = tlsParams;
    }

    @Override
    public void run() {
        RapidoidHelper helper = Cls.newInstance(net.helperClass(), net.exchangeClass());
        helper.requestIdGen = workerIndex; // to generate UNIQUE request ID (+= MAX_IO_WORKERS)

        worker = new RapidoidWorker("server" + (workerIndex + 1), helper, net, tlsParams);

        worker.run();
    }

    public RapidoidWorker getWorker() {
        while (worker == null) {
            U.sleep(50);
        }

        return worker;
    }

}
