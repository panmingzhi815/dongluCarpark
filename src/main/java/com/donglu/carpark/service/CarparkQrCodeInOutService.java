package com.donglu.carpark.service;

public interface CarparkQrCodeInOutService {
	public interface CarparkQrCodeInOutCallback{
		public void call(String ip);
	}
	public void initService(CarparkQrCodeInOutCallback callback) throws Exception;
	public String getQrCodeUrl(String parkId,String ip,int type);
}
