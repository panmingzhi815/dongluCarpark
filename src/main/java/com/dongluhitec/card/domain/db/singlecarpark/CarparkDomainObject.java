package com.dongluhitec.card.domain.db.singlecarpark;

import java.lang.reflect.Method;

import com.dongluhitec.card.domain.db.DomainObject;

public class CarparkDomainObject extends DomainObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public void initUuid() {
		if (uuid==null) {
			super.initUuid();
		}
	}
	/**
	 * copy self to object
	 * @param to
	 * @return
	 */
	public <T> T copy(T to) {
		for (Method method : to.getClass().getMethods()) {
			String name = method.getName();
			if (!name.startsWith("set")||method.getParameterCount()!=1) {
				continue;
			}
			try {
				Method method2 = getClass().getMethod("get"+name.substring(3, 4).toUpperCase()+name.substring(4));
				if (method2==null) {
					continue;
				}
				Object object = method2.invoke(this);
				method.invoke(to, object);
			} catch (Exception e) {
				
			}
		}
		return to;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T copy() throws InstantiationException, IllegalAccessException {
		return (T) copy(getClass().newInstance());
	}
}
