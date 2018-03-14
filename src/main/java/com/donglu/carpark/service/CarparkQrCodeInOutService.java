package com.donglu.carpark.service;

public interface CarparkQrCodeInOutService {
	public interface CarparkQrCodeInOutCallback{
		public void call(String ip);
	}
	public void initService(String buildId,CarparkQrCodeInOutCallback callback) throws Exception;
	public String getQrCodeUrl(String parkId,String plate,String ip, int type);
}
