package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.dongluhitec.card.domain.db.DomainObject;

@Entity
@Table(name="carpark_car_type")
public class CarparkCarType extends DomainObject{

	private static final long serialVersionUID = -4521169608146500548L;
	
	@Column(name="name",unique=true)
	private String name;
	
	@OneToMany(mappedBy="carparkCarType")
	private List<CarparkChargeStandard> carparkChargeStandardList;
	private Long tid;
	private String displayName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<CarparkChargeStandard> getCarparkChargeStandardList() {
		return carparkChargeStandardList;
	}

	public void setCarparkChargeStandardList(
			List<CarparkChargeStandard> carparkChargeStandardList) {
		this.carparkChargeStandardList = carparkChargeStandardList;
	}

	@Override
	public String toString() {
		return getId() +"-"+ getName();
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CarparkCarType that = (CarparkCarType) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

	public Long getTid() {
		if (tid==null) {
			return id;
		}
		return tid;
	}

	public void setTid(Long tid) {
		this.tid = tid;
	}

	public String getDisplayName() {
		if (displayName==null) {
			return name;
		}
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
		firePropertyChange("displayName", null, null);
	}
}
