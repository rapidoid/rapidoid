package com.example;

import org.junit.Test;

import javax.inject.Inject;

public class InjectionTest extends AbstractIntegrationTest {

	@Inject
	private MyCtrl myCtrl;

	@Inject
	private MyService myService;

	@Test
	public void testDependencyInjection() {
		// the components should be injected
		notNull(myCtrl);
		notNull(myService);

		// the initial list of books should be empty
		isTrue(myService.getInitialBooks().isEmpty());
		isTrue(myCtrl.initialBooks().isEmpty());
	}

}
