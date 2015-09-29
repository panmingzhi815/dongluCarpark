package com.donglu.carpark.service;

public abstract class AbstractCarparkDatabaseServiceProvider implements CarparkDatabaseServiceProvider {
	private CarparkService carparkService;
	private CarparkUserService carparkUserService;
	
	
	private boolean started=false;

	@Override
	public void start() throws Exception{
        if(this.started != true){
            initService();
            this.started = true;
        }
	}

    abstract protected void initService() throws Exception;

    abstract protected void stopServices() ;

	@Override
	public void stop() throws Exception {
		 stopServices();
	        this.started = false;
	}

	@Override
	public CarparkService getCarparkService() {
		checkState();
		return this.carparkService;
	}

	@Override
	public CarparkUserService getCarparkUserService() {
		return this.carparkUserService;
	}
	
	 private void checkState() {
			if (!this.started)
				throw new IllegalStateException(
						"The Serivce hasn't been started, cannot get the service.");
	}

	public void setCarparkService(CarparkService carparkService) {
		this.carparkService = carparkService;
	}

	public void setCarparkUserService(CarparkUserService carparkUserService) {
		this.carparkUserService = carparkUserService;
	}

}
