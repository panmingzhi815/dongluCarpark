package com.donglu.carpark.wizard;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class FactoryTest {
	
	@Inject
	Factory f;
	
	public static void main(String[] args) {
		Injector createInjector = Guice.createInjector(new AbstractModule() {
			
			@Override
			protected void configure() {
				FactoryModuleBuilder factorybuilder = new FactoryModuleBuilder();
				Module build = factorybuilder.build(Factory.class);
				this.install(build);
				
			}
		});
		FactoryTest instance = createInjector.getInstance(FactoryTest.class);
		instance.factory();
	}
	
	public void factory(){
		AddUserWizard addUserWizard = f.getAddUserWizard(new Object());
		System.out.println(addUserWizard);
	}
}
