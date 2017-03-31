package com.donglu.carpark.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;

public interface SystemUserServiceI {
	Map<String, String> mapLoginInfo=new HashMap<String, String>();
	
	public List<SingleCarparkSystemUser> findAllSystemUser();
	
	public SingleCarparkSystemUser findByNameAndPassword(String userName,String password);
	
	public Long removeSystemUser(SingleCarparkSystemUser systemUser);
	
	public Long saveSystemUser(SingleCarparkSystemUser systemUser);

	public void initSystemInfo();
	
	public void login(String userName,String password,String ip);
	
	public void loginOut(String userName);
	
	public String loginStatus(String userName);
}
