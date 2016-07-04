package com.donglu.carpark.service;

import com.caucho.hessian.client.HessianProxyFactory;
import com.donglu.carpark.ui.CarparkClientConfig;
import com.donglu.carpark.ui.ClientConfigUI;
import com.donglu.carpark.util.CarparkFileUtils;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.*;
import com.google.inject.persist.PersistService;

import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *客户端的service服务配置
 */
public class CarparkHessianServiceProvider extends AbstractCarparkDatabaseServiceProvider{
	static final Logger logger = LoggerFactory.getLogger(CarparkHessianServiceProvider.class);
    private PersistService persistService;
   
    @Override
	protected void initService() {
        try{
        	Injector injector = Guice.createInjector(new Model());
        	 setCarparkService(injector.getInstance(CarparkService.class));
             setCarparkUserService(injector.getInstance(CarparkUserService.class));
             setSystemUserService(injector.getInstance(SystemUserServiceI.class));
             setCarparkInOutService(injector.getInstance(CarparkInOutServiceI.class));
             setSystemOperaLogService(injector.getInstance(SystemOperaLogServiceI.class));
             setStoreService(injector.getInstance(StoreServiceI.class));
             setPlateSubmitService(injector.getInstance(PlateSubmitServiceI.class));
             setPositionUpdateService(injector.getInstance(PositionUpdateServiceI.class));
             setImageService(injector.getInstance(ImageServiceI.class));
             setSettingService(injector.getInstance(SettingService.class));
        }catch(Exception e){
        	e.printStackTrace();
        }
    }

    @Override
	protected void stopServices() {
        persistService.stop();
    }

    public class Model extends AbstractModule {
        @Override
        protected void configure() {
            CarparkClientConfig cf=(CarparkClientConfig) CarparkFileUtils.readObject(ClientConfigUI.CARPARK_CLIENT_CONFIG);
            if (cf==null) {
				return;
			}
            String dbServerIp = CarparkClientConfig.getInstance().getServerIp();
            if (dbServerIp.equals(StrUtil.getHostIp())) {
				
			}
			String url = "http://"+dbServerIp+":8899/";
			logger.info("客户端远程数据底层地址:{}",url);

            HessianProxyFactory factory = new HessianProxyFactory();
            factory.setOverloadEnabled(true);
            
            try {
            	this.bind(SettingService.class).toInstance((SettingService) factory.create(SettingService.class, url+"user/"));
            	this.bind(CarparkUserService.class).toInstance((CarparkUserService) factory.create(CarparkUserService.class, url+"user/"));
            	this.bind(SystemUserServiceI.class).toInstance((SystemUserServiceI) factory.create(SystemUserServiceI.class, url+"user/"));
				this.bind(CarparkService.class).toInstance((CarparkService) factory.create(CarparkService.class, url+"carpark/"));
				this.bind(SystemOperaLogServiceI.class).toInstance((SystemOperaLogServiceI) factory.create(SystemOperaLogServiceI.class, url+"user/"));
				this.bind(CarparkInOutServiceI.class).toInstance((CarparkInOutServiceI) factory.create(CarparkInOutServiceI.class, url+"inout/"));
				this.bind(StoreServiceI.class).toInstance((StoreServiceI) factory.create(StoreServiceI.class, url+"storeservice/"));
				this.bind(PlateSubmitServiceI.class).toInstance((PlateSubmitServiceI) factory.create(PlateSubmitServiceI.class, url+"plateSubmit/"));
				this.bind(PositionUpdateServiceI.class).toInstance((PositionUpdateServiceI) factory.create(PositionUpdateServiceI.class, url+"positionUpdate/"));
				this.bind(ImageServiceI.class).toInstance((ImageServiceI) factory.create(ImageServiceI.class, url+"carparkImage/"));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
        }
    }


}
