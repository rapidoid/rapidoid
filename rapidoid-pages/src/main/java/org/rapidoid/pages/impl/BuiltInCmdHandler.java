package org.rapidoid.pages.impl;

import org.rapidoid.reactive.Var;

public class BuiltInCmdHandler {

	public void on_set(Var<Integer> var, Integer value) {
		var.set(value);
	}

	public void on_inc(Var<Integer> var, Integer value) {
		var.set(var.get() + value);
	}

	public void on_dec(Var<Integer> var, Integer value) {
		var.set(var.get() - value);
	}

}
