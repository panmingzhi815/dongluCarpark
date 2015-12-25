package com.donglu.carpark.server;

import com.dongluhitec.card.hardware.hanwangAllInOne.LicencePlatesAllInOne;
import com.dongluhitec.card.hardware.hanwangAllInOne.impl.LicencePlatesAllInOneImpl;
import com.dongluhitec.card.hardware.hanwangAllInOne.impl.MacDummyLicencePlatesAllInOneImpl;
import com.dongluhitec.card.hardware.idcardreader.IDCodeReader;
import com.dongluhitec.card.hardware.idcardreader.impl.IDCodeReaderJNAImpl;
import com.dongluhitec.card.hardware.idcardreader.impl.MacDummyIDCodeReaderImpl;
import com.dongluhitec.card.hardware.licenceplates.LicencePlates;
import com.dongluhitec.card.hardware.licenceplates.impl.LicencePlatesJNAImpl;
import com.dongluhitec.card.hardware.licenceplates.impl.MacDummyLicencePlatesImpl;
import com.dongluhitec.card.hardware.message.MessageRegistry;
import com.dongluhitec.card.hardware.message.cardwriter.CardWriterRegisterImpl;
import com.dongluhitec.card.hardware.message.newcarpark.NewCarparkMessageRegistryImpl;
import com.dongluhitec.card.hardware.message.service.CardReaderMessageRegistryImpl;
import com.dongluhitec.card.hardware.message.service.CarparkMessageRegistryImpl;
import com.dongluhitec.card.hardware.message.service.ConsumptionMessageRegistryImpl;
import com.dongluhitec.card.hardware.message.service.MessageRegistryImpl;
import com.dongluhitec.card.hardware.service.BasicHardwareService;
import com.dongluhitec.card.hardware.service.impl.BasicHardwareServiceSyncImpl;
import com.dongluhitec.card.hardware.xinluwei.MacDummyXinlutongImpl;
import com.dongluhitec.card.hardware.xinluwei.XinlutongJNA;
import com.dongluhitec.card.hardware.xinluwei.XinlutongJNAImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

public class CarparkHardwareGuiceModule extends AbstractModule {

    public static final String MESSAGE_TIMEOUT_ID = "MESSAGE_TIMEOUT";
    private int message_wait_timeout = 100;

    public CarparkHardwareGuiceModule() {
    }

    @Override
    protected void configure() {
        this.bind(Integer.class)
                .annotatedWith(
                        Names.named(CarparkHardwareGuiceModule.MESSAGE_TIMEOUT_ID))
                .toInstance(this.message_wait_timeout);

        String osname = System.getProperty("os.name");

        if (osname.toLowerCase().contains("win")) {
            this.bind(IDCodeReader.class).to(IDCodeReaderJNAImpl.class);
            this.bind(LicencePlates.class).to(LicencePlatesJNAImpl.class);
            this.bind(XinlutongJNA.class).to(XinlutongJNAImpl.class);
            this.bind(LicencePlatesAllInOne.class).to(LicencePlatesAllInOneImpl.class);
        } else {
            this.bind(IDCodeReader.class).to(MacDummyIDCodeReaderImpl.class);
            this.bind(LicencePlates.class).to(MacDummyLicencePlatesImpl.class);
            this.bind(XinlutongJNA.class).to(MacDummyXinlutongImpl.class);
            this.bind(LicencePlatesAllInOne.class).to(MacDummyLicencePlatesAllInOneImpl.class);
        }


        this.bind(BasicHardwareService.class).to(BasicHardwareServiceSyncImpl.class).in(Singleton.class);
//        this.bind(HardwareService.class).to(HardwareServiceSyncImpl.class).in(Singleton.class);

        this.bind(MessageRegistry.class).annotatedWith(Names.named("DefaultMessageRegistry")).to(MessageRegistryImpl.class);
        this.bind(MessageRegistry.class).annotatedWith(Names.named("CarparkMessageRegistry")).to(CarparkMessageRegistryImpl.class);
        this.bind(MessageRegistry.class).annotatedWith(Names.named("ConsumptionMessageRegistry")).to(ConsumptionMessageRegistryImpl.class);

        //multibinding.
        Multibinder<MessageRegistry> uriBinder = Multibinder.newSetBinder(binder(), MessageRegistry.class);
        uriBinder.addBinding().to(MessageRegistryImpl.class);
        uriBinder.addBinding().to(CarparkMessageRegistryImpl.class);
        uriBinder.addBinding().to(ConsumptionMessageRegistryImpl.class);
        uriBinder.addBinding().to(CardReaderMessageRegistryImpl.class);
        uriBinder.addBinding().to(CardWriterRegisterImpl.class);
        uriBinder.addBinding().to(NewCarparkMessageRegistryImpl.class);
//        this.install(new ServiceFactoryGuiceModule());

    }
}
