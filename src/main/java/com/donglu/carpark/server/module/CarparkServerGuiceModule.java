package com.donglu.carpark.server.module;

import com.donglu.carpark.server.CarparkServerConfig;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkLocalVMServiceProvider;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.common.ui.impl.SWTUIFacility;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

public class CarparkServerGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
		this.bind(CommonUIFacility.class).to(SWTUIFacility.class).in(Singleton.class);

		this.bindConstant().annotatedWith(Names.named("HBM2DDL")).to("update");
		bind(CarparkServerConfig.class).toInstance(CarparkServerConfig.getInstance());
		bind(CarparkDatabaseServiceProvider.class).to(CarparkLocalVMServiceProvider.class);
	}
}
