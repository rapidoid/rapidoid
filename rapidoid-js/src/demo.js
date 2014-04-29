/*
 * #%L
 * rapidoid-js
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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
$(function() {

	var counter = prop("counter", 0);
	var delay = prop("delay", 1000);

	var a = prop("a", 10);
	var b = prop("b", 20);

	var c = calc("c", reduce(sum), [ a, b ]);
	var d = calc("d", join(':'), [ a, b, c ]);

	var x = val('#in');
	var y = val('#num');
	var y2 = calc("y2", single(parseInt), [ y ]);

	var all = prop("all", [ 'a', 'b', 'c', 'dd', 'eee', 'fff', 'G' ]);
	var len = calc("len", single(count), [ all ]);
	var sel = calc("sel", slice, [ all, def(1), y2 ]);

	// var sel = all.slice(prop("c", 1), y2);

	y2.outs.push(setVal('#num'));

	$('#up').click(function() {
		delay.set(delay.get() + y2.get());
	});

	$('#down').click(function() {
		delay.set(delay.get() - y2.get());
	});

	$('#add').click(function() {
		all.add(x.get());
	});

	$('#del').click(function() {
		all.del(0);
	});

	function fff(item) {
		var xx = $('<input type="text" value="abc"/>');

		bind(xx, item);

		return xx;
	}

	var ctrls = calc("ctrls", single(foreach(fff)), [ sel ]);
	ctrls.outs.push(into('#items'));

	var e = calc("e", join('<br>'), [ counter, delay, d, x, y, y2, all, len,
			sel, ctrls ]);
	e.outs.push(html('#outf'));

	timer(counter, delay);
});
