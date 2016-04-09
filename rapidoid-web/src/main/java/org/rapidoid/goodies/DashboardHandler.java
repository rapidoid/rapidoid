package org.rapidoid.goodies;

import org.rapidoid.u.U;

import java.util.concurrent.Callable;

public class DashboardHandler implements Callable<Object> {

	@Override
	public Object call() throws Exception {
		return U.map();
	}

}
