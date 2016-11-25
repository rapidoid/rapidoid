package org.rapidoid.integration.guice;

import com.google.inject.AbstractModule;

public class MathModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(MathCtrl.class).asEagerSingleton();
	}

}