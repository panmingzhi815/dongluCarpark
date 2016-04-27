package com.donglu.carpark.service;

import com.donglu.carpark.model.storemodel.Info;

public interface StoreHessianServiceI {
	public void send(Info info);
	public Info keepLink();
}
