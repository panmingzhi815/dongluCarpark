package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.dongluhitec.card.domain.db.CardUserGroup;
import com.dongluhitec.card.domain.db.DomainObject;

@Entity
public class SingleCarparkCarpark extends DomainObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7489256851092412913L;
	
	private String code;
	private String name;
	private boolean tempCarIsIn;
	@ManyToOne
	@JoinColumn(name = "parent", nullable = true, insertable = false, updatable = false)
	private SingleCarparkCarpark parent;
	
	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE,fetch=FetchType.EAGER)
	private List<SingleCarparkCarpark> childs=new ArrayList<SingleCarparkCarpark>();
	
	@Column(name = "parent", nullable = true)
	private Long parentId;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
		if (pcs != null)
			pcs.firePropertyChange("code", null, null);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		if (pcs != null)
			pcs.firePropertyChange("name", null, null);
	}
	public SingleCarparkCarpark getParent() {
		return parent;
	}
	public void setParent(SingleCarparkCarpark parent) {
		this.parent = parent;
		if (parent!=null) {
			this.parentId=parent.getId();
		}
		if (pcs != null)
			pcs.firePropertyChange("parent", null, null);
	}
	public void setTempCarIsIn(boolean tempCarIsIn) {
		this.tempCarIsIn = tempCarIsIn;
		if (pcs != null)
			pcs.firePropertyChange("tempCarIsIn", null, null);
	}
	public List<SingleCarparkCarpark> getChilds() {
		return childs;
	}
	public void setChilds(List<SingleCarparkCarpark> childs) {
		this.childs = childs;
		if (pcs != null)
			pcs.firePropertyChange("childs", null, null);
	}
	public boolean isTempCarIsIn() {
		return tempCarIsIn;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
		if (pcs != null)
			pcs.firePropertyChange("parentId", null, null);
	}
	
	
}
