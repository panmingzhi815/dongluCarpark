package com.donglu.carpark.service;

import com.donglu.carpark.server.CarparkServerConfig;
import com.donglu.carpark.service.impl.CarparkServiceImpl;
import com.donglu.carpark.service.impl.CarparkUserServiceImpl;
import com.dongluhitec.card.blservice.*;
import com.dongluhitec.card.domain.db.setting.SystemSetting;
import com.dongluhitec.card.service.DbServiceConfigurator;
import com.dongluhitec.card.service.MapperConfig;
import com.dongluhitec.card.service.impl.AbstractServiceProvider;
import com.dongluhitec.card.service.impl.SettingServiceImpl;
import com.google.inject.*;
import com.google.inject.name.Named;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;

import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: wudong
 * Date: 05/08/13
 * Time: 22:35
 * To change this template use File | Settings | File Templates.
 */
public class CarparkLocalVMServiceProvider extends AbstractCarparkDatabaseServiceProvider{

    private PersistService persistService;
    final private CarparkServerConfig config;
    //必须给定一个hbm2ddl值 ,如果是新建数据库,则为create,如果不为create, table上的一些约束条件将无法自动生成.但在运行时,应将该值设置为update
    final private String HBM2DDL;

    @Inject
    public CarparkLocalVMServiceProvider(CarparkServerConfig configurator,@Named(value = "HBM2DDL")String HBM2DDL){
        this.config = configurator;
        this.HBM2DDL = HBM2DDL;
    }

    protected void initService() {
        Injector injector = Guice.createInjector(new Model());
        this.persistService = injector.getInstance(PersistService.class);
        persistService.start();
        setCarparkService(injector.getInstance(CarparkService.class));
        setCarparkUserService(injector.getInstance(CarparkUserService.class));
    }

    protected void stopServices() {
        persistService.stop();
    }

    public class Model extends AbstractModule {

        @Override
        protected void configure() {
            final JpaPersistModule jpaPersistModule =
                    new JpaPersistModule("SQLSERVER2008");

            final Properties properties = new Properties();
            String dbServerDriver = config.getDbServerDriver();
            String dbServerURL = config.getDbServerURL();
            String dbServerUsername = config.getDbServerUsername();
            String dbServerPassword = config.getDbServerPassword();
            Object object = properties.get("javax.persistence.jdbc.driver");
            System.out.println(dbServerDriver+"==="+dbServerURL+"==="+dbServerUsername+"==="+dbServerPassword+"=="+object);
            
            
                properties.setProperty("javax.persistence.jdbc.driver",
                		dbServerDriver);

                properties.setProperty("javax.persistence.jdbc.url",
                		dbServerURL);

                properties.setProperty("javax.persistence.jdbc.user",
                		dbServerUsername);

                properties.setProperty("javax.persistence.jdbc.password",
                		dbServerPassword);
            

            properties.setProperty("hibernate.hbm2ddl.auto",HBM2DDL);
            jpaPersistModule.properties(properties);
            this.install(jpaPersistModule);

            this.bind(MapperConfig.class).in(Singleton.class);
            
            this.bind(CarparkService.class).to(CarparkServiceImpl.class).in(Singleton.class);
            this.bind(CarparkUserService.class).to(CarparkUserServiceImpl.class).in(Singleton.class);
        }
    }


}