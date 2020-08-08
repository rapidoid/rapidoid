package com.example;

import org.junit.jupiter.api.Test;

import javax.inject.Inject;

public class InjectionTest extends AbstractIntegrationTest {

    @Inject
    private MyCtrl myCtrl;

    @Test
    public void testDependencyInjection() {
        // the components should be injected
        notNull(myCtrl);

        // the initial list of books should be empty
        isTrue(myCtrl.initialBooks().isEmpty());
    }

}
