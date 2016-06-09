package com.example;

import org.junit.Test;

import javax.inject.Inject;

public class InjectionTest extends AbstractIntegrationTest {

	@Inject
	private MyCtrl myCtrl;

	@Test
	public void testDependencyInjection() {
		notNull(myCtrl); // just showing that dependency injection works
	}

}
