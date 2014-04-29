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
var state = [];

function info() {
	var s = '';
	for (var i = 0; i < state.length; i++) {
		s += state[i].name + '=' + state[i].get() + ', ';
	}
	console.log(s);
}

function updateFrom(p) {
	var value = p.getValue();

	for (var i = 0; i < p.bonds.length; i++) {
		var bond = p.bonds[i];
		bond.to.update(bond.index, value);
	}

	for (var i = 0; i < p.outs.length; i++) {
		p.outs[i](value);
	}
}

function def(val) {
	return prop("const", val);
}

function prop(name, value, f) {

	var p = abs_prop(name, f);

	p.setValue = function(val) {
		p.value = val;
	}

	p.getValue = function() {
		return p.value;
	}

	p.set(value);

	return p;
}

function at(holder, pos) {

	var p = abs_prop("at[" + pos + "]");

	p.setValue = function(val) {
		holder.setAt(pos, val);
	}

	p.getValue = function() {
		return holder.getAt(pos);
	}

	return p;
}

function abs_prop(name, f) {

	var p = {
		name : name,
		sources : [],
		deps : [],
		bonds : [],
		outs : [],
		links : [],
		f : f,
		error : false
	}

	state.push(p);

	p.set = function(val) {
		p.setValue(val);
		updateFrom(p);
	}

	p.get = function() {
		return p.getValue();
	}

	p.setAt = function(pos, val) {
		p.getValue()[pos] = val;
		updateFrom(p);
	}

	p.getAt = function(pos) {
		return p.getValue()[pos];
	}

	p.update = function(index, val) {
		p.deps[index] = val;
		p.doUpdate();
	}

	p.doUpdate = function() {
		try {
			var v = p.f(p.deps, p.sources);
			if (typeof v != 'number' || !isNaN(v)) {
				p.set(v);
				p.error = false;
				return;
			}
		} catch (e) {
		}

		p.error = true;
		p.set(undefined);
	}

	p.add = function(item) {
		p.getValue().push(item);
		updateFrom(p);
	}

	p.del = function(index) {
		p.getValue().splice(index, 1);
		updateFrom(p);
	}

	return p;
}

function calc(name, f, sources) {
	var p = prop(name, undefined, f);

	for (var i = 0; i < sources.length; i++) {
		var src = sources[i];

		src.bonds.push({
			index : i,
			to : p
		});

		p.sources.push(src);
		p.deps.push(src.get());
	}

	p.doUpdate();

	return p;
}

/** ********************************************************************** */

function reduce(op) {
	return function(args) {
		var ac = args[0];

		for (var i = 1; i < args.length; i++) {
			ac = op(ac, args[i]);
		}

		return ac;
	}
}

function join(sep) {
	return function(args) {
		return args.join(sep);
	}
}

function sum(x, y) {
	return x + y;
}

function $get(id) {
	return document.getElementById(id);
}

function html(target) {
	return function(value) {
		$(target).html('' + value);
	}
}

function setVal(id) {
	return function(value) {
		$(id).val(value);
	}
}

function val(el) {
	var p = prop("<elem>", $(el).val());

	$(el).keyup(function(ev) {
		p.set($(el).val());
	});

	return p;
}

function bind(el, p) {
	$(el).keyup(function(ev) {
		p.set($(el).val());
	});

	var valSetter = setVal(el);
	p.outs.push(valSetter);
	valSetter(p.get());
}

function timer(counter, delay) {
	function tick() {
		counter.set(counter.get() + 1);
		info();
		setTimeout(tick, delay.get());
	}

	tick();
}

function single(f) {
	return function(args) {
		return f(args[0]);
	}
}

function count(args) {
	return args.length;
}

function slice(vals, sources) {
	var holder = sources[0];

	var subs = [];

	for (var i = vals[1]; i < vals[2]; i++) {
		subs.push(at(holder, i));
	}

	return subs;
}

function foreach(f) {
	return function(args) {
		var items = [];
		for (var i = 0; i < args.length; i++) {
			items.push(f(args[i]));
		}
		return items;
	}
}

function into(target) {
	return function(value) {
		$(target).empty().append(value);
	}
}
