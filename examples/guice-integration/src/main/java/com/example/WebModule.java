package com.example;

import com.google.inject.AbstractModule;

public class WebModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(MyCtrl.class).asEagerSingleton();
	}

}