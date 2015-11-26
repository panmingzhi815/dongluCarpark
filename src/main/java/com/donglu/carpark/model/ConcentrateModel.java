package com.donglu.carpark.model;

import java.util.Date;

import com.dongluhitec.card.domain.db.DomainObject;

public class ConcentrateModel extends DomainObject {
	String userName;
	String workTime;
	Float totalFact;
	Float totalFree;
	
	String plateNO;
	Date inTime;
	String stillTime;
	Float shouldMoney;
	Float factMoney;
	String freeType;
	String freeInfo;
}
