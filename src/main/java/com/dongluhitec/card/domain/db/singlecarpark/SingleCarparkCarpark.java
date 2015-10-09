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
	
	@NotNull(message = "车位总数不能为空")
	@Min(value = 0, message = "车位总数不能小于0")
	private int totalNumberOfSlot;
	
	@NotNull(message = "剩余车位总数不能为空")
	@Min(value = 0, message = "剩余车位总数不能小于0")
	private int leftNumberOfSlot;
	
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
		SingleCarparkCarpark cp=(SingleCarparkCarpark) obj;
		if (this.name.equals(cp.getName())) {
			return true;
		}
		return false;
	}
	@Override
	public String toString() {
		// TODO 自动生成的方法存根
		return this.getCode()+"-"+this.getName();
	}
	public int getTotalNumberOfSlot() {
		return totalNumberOfSlot;
	}
	public void setTotalNumberOfSlot(int totalNumberOfSlot) {
		this.totalNumberOfSlot = totalNumberOfSlot;
		if (pcs != null)
			pcs.firePropertyChange("totalNumberOfSlot", null, null);
	}
	public int getLeftNumberOfSlot() {
		return leftNumberOfSlot;
	}
	public void setLeftNumberOfSlot(int leftNumberOfSlot) {
		this.leftNumberOfSlot = leftNumberOfSlot;
		if (pcs != null)
			pcs.firePropertyChange("leftNumberOfSlot", null, null);
	}
	
	
	
}
