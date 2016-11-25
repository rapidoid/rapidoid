package org.rapidoid.integration.guice;

import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;
import org.rapidoid.u.U;

import javax.inject.Inject;

@Controller
public class MathCtrl {

	private final MathService math;

	@Inject
	public MathCtrl(MathService math) {
		this.math = math;
	}

	@GET("/add")
	public Object add(int x, int y) {
		return U.map("sum", math.add(x, y));
	}

}
