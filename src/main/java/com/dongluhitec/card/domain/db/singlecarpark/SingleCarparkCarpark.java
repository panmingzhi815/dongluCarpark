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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.dongluhitec.card.domain.db.CardUserGroup;
import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.util.StrUtil;

@Entity
public class SingleCarparkCarpark extends DomainObject {
	public enum Property{
		totalNumberOfSlot,fixNumberOfSlot,tempNumberOfSlot
	}

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
	
	@NotNull(message = "车位总数不能为空")
	@Min(value = 0, message = "车位总数不能小于0")
	private Integer totalNumberOfSlot;
	
	@NotNull(message = "剩余车位总数不能为空")
	@Min(value = 0, message = "剩余车位总数不能小于0")
	private Integer leftNumberOfSlot;
	
	@Column(name = "fixNumberOfSlot")
	@Min(value = 0, message = "固定车位总数不能小于0")
	private Integer fixNumberOfSlot;

	@Column(name = "tempNumberOfSlot")
	@Min(value = 0, message = "临时车位总数不能小于0")
	private Integer tempNumberOfSlot;
	
	private Boolean fixCarOneIn;
	private Boolean isCharge=true;
	
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
	@Override
	public boolean equals(Object obj) {
		if (!obj.getClass().equals(SingleCarparkCarpark.class)) {
			return false;
		}
		if (StrUtil.isEmpty(obj)) {
			return false;
		}
		SingleCarparkCarpark cp=(SingleCarparkCarpark) obj;
		if (this.id==null||cp.getId()==null) {
			return false;
		}
		if (this.id.equals(cp.getId())) {
			return true;
		}
		return false;
	}
	@Override
	public String toString() {
		return this.getCode()+"-"+this.getName();
	}
	public Integer getTotalNumberOfSlot() {
		return totalNumberOfSlot;
	}
	public void setTotalNumberOfSlot(int totalNumberOfSlot) {
		this.totalNumberOfSlot = totalNumberOfSlot;
		if (pcs != null)
			pcs.firePropertyChange("totalNumberOfSlot", null, null);
	}
	public Integer getLeftNumberOfSlot() {
		return leftNumberOfSlot;
	}
	public void setLeftNumberOfSlot(int leftNumberOfSlot) {
		this.leftNumberOfSlot = leftNumberOfSlot;
		if (pcs != null)
			pcs.firePropertyChange("leftNumberOfSlot", null, null);
	}
	public Integer getFixNumberOfSlot() {
		return fixNumberOfSlot;
	}
	public void setFixNumberOfSlot(Integer fixNumberOfSlot) {
		this.fixNumberOfSlot = fixNumberOfSlot;
		if (pcs != null)
			pcs.firePropertyChange("fixNumberOfSlot", null, null);
	}
	public Integer getTempNumberOfSlot() {
		return tempNumberOfSlot;
	}
	public void setTempNumberOfSlot(Integer tempNumberOfSlot) {
		this.tempNumberOfSlot = tempNumberOfSlot;
		if (pcs != null)
			pcs.firePropertyChange("tempNumberOfSlot", null, null);
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public void setTotalNumberOfSlot(Integer totalNumberOfSlot) {
		this.totalNumberOfSlot = totalNumberOfSlot;
		if (pcs != null)
			pcs.firePropertyChange("totalNumberOfSlot", null, null);
	}
	public void setLeftNumberOfSlot(Integer leftNumberOfSlot) {
		this.leftNumberOfSlot = leftNumberOfSlot;
		if (pcs != null)
			pcs.firePropertyChange("leftNumberOfSlot", null, null);
	}
	@Override
	public String getLabelString() {
		return "("+code+")"+name;
	}
	public Boolean getFixCarOneIn() {
		return fixCarOneIn;
	}
	public void setFixCarOneIn(Boolean fixCarOneIn) {
		this.fixCarOneIn = fixCarOneIn;
		if (pcs != null)
			pcs.firePropertyChange("fixCarOneIn", null, null);
	}
	public Boolean getIsCharge() {
		return isCharge;
	}
	public void setIsCharge(Boolean isCharge) {
		this.isCharge = isCharge;
		if (pcs != null)
			pcs.firePropertyChange("isCharge", null, null);
	}
	
	
	
}
