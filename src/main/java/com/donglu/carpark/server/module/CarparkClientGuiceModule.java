package com.donglu.carpark.server.module;

import com.donglu.carpark.service.CarparkClientLocalVMServiceProvider;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.dongluhitec.card.blservice.HardwareFacility;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.common.ui.impl.SWTUIFacility;
import com.dongluhitec.card.hardware.util.HardwareFacilityImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

public class CarparkClientGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
    	this.bindConstant().annotatedWith(Names.named("HBM2DDL")).to("update");
		this.bind(HardwareFacility.class).to(HardwareFacilityImpl.class);
		this.bind(CarparkDatabaseServiceProvider.class).to(CarparkClientLocalVMServiceProvider.class).in(Singleton.class);
		this.bind(CommonUIFacility.class).to(SWTUIFacility.class)
		.in(Singleton.class);
		this.install(new CarparkHardwareGuiceModule());
    }
}
