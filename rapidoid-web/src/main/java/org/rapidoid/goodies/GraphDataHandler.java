package org.rapidoid.goodies;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.timeseries.TimeSeries;
import org.rapidoid.gui.GUI;
import org.rapidoid.http.Req;
import org.rapidoid.insight.Metrics;
import org.rapidoid.lambda.FourParamLambda;

import java.util.Collections;

/*
 * #%L
 * rapidoid-web
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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class GraphDataHandler extends GUI implements FourParamLambda<Object, Req, Double, Double, String> {

	@Override
	public Object execute(Req req, Double from, Double to, String id) throws Exception {
		TimeSeries metrics = Metrics.get(id);

		if (metrics == null) {
			return Collections.emptyMap();
		}

		long fromT = from.longValue();
		long toT = to.longValue();

		return metrics.overview(fromT, toT);
	}

}
