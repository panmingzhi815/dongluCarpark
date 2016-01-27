package com.donglu.carpark.ui.wizard.sn;

import com.dongluhitec.card.domain.db.DomainObject;

public class ImportSNModel extends DomainObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4643813063458318594L;
	private String sn;
	private String companyName;
	private String projectName;
	private String modules;
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
		if (pcs != null)
			pcs.firePropertyChange("sn", null, null);
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
		if (pcs != null)
			pcs.firePropertyChange("companyName", null, null);
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
		if (pcs != null)
			pcs.firePropertyChange("projectName", null, null);
	}
	public String getModules() {
		return modules;
	}
	public void setModules(String modules) {
		this.modules = modules;
		if (pcs != null)
			pcs.firePropertyChange("modules", null, null);
	}
	
}
