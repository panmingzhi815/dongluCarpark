package com.donglu.carpark.ui.view.main;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.dongluhitec.card.domain.db.singlecarpark.CarparkPrivilegeEnum;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface CarparkPrivilege {
	CarparkPrivilegeEnum value();
}
