package com.dongluhitec.card.domain.db.singlecarpark;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.util.StrUtil;

@Entity
public class SingleCarparkBlackUser extends DomainObject {
	public enum Property {
		plateNO, remark, timeLabel, hoursStartLabel, hoursEndLabel, minuteStartLabel, minuteEndLabel
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2546271381567702939L;
	@Column(unique = true)
	private String plateNO;
	private String remark;
	private int hoursStart = 00;
	private int hoursEnd = 23;
	private int minuteStart = 0;
	private int minuteEnd = 59;
	private Boolean timeIn=false;
	private Boolean weekDayIn=false;
	private Boolean holidayIn=false;
	private Boolean canOut=true;
	
	@Transient
	private String hoursStartLabel;
	@Transient
	private String hoursEndLabel;
	@Transient
	private String minuteStartLabel;
	@Transient
	private String minuteEndLabel;

	public String getHoursStartLabel() {
		if (StrUtil.isEmpty(hoursStartLabel)) {
			if (hoursStart >= 0 && hoursStart <= 9) {
				return 0 + "" + hoursStart;
			} else {
				return "" + hoursStart;
			}
		} else {
			return hoursStartLabel;
		}

	}

	public String getHoursEndLabel() {
		if (StrUtil.isEmpty(hoursEndLabel)) {
			if (hoursEnd >= 0 && hoursEnd <= 9) {
				return 0 + "" + hoursEnd;
			} else
				return "" + hoursEnd;
		} else {
			return hoursEndLabel;
		}
	}

	public String getMinuteStartLabel() {
		if (StrUtil.isEmpty(minuteStartLabel)) {
			if (minuteStart >= 0 && minuteStart <= 9) {
				return 0 + "" + minuteStart;
			} else
				return "" + minuteStart;
		} else {
			return minuteStartLabel;
		}
	}

	public String getMinuteEndLabel() {
		if (StrUtil.isEmpty(minuteEndLabel)) {

			if (minuteEnd >= 0 && minuteEnd <= 9) {
				return 0 + "" + minuteEnd;
			} else
				return "" + minuteEnd;
		} else {
			return minuteEndLabel;
		}
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getPlateNO() {
		return plateNO;
	}

	public void setPlateNO(String plateNO) {
		this.plateNO = plateNO;
		if (pcs != null)
			pcs.firePropertyChange("plateNO", null, null);
	}

	public String getRemark() {
		return remark;
	}

	public String getTimeLabel() {
		return getHoursStartLabel() + ":" + getMinuteStartLabel() + "--" + getHoursEndLabel() + ":" + getMinuteEndLabel();
	}

	public void setRemark(String remark) {
		this.remark = remark;
		if (pcs != null)
			pcs.firePropertyChange("remark", null, null);
	}

	public int getHoursStart() {
		return hoursStart;
	}

	public void setHoursStart(int hoursStart) {
		this.hoursStart = hoursStart;
		if (pcs != null)
			pcs.firePropertyChange("hoursStart", null, null);
	}

	public int getHoursEnd() {
		return hoursEnd;
	}

	public void setHoursEnd(int hoursEnd) {
		this.hoursEnd = hoursEnd;
		if (pcs != null)
			pcs.firePropertyChange("hoursEnd", null, null);
	}

	public int getMinuteStart() {
		return minuteStart;
	}

	public void setMinuteStart(int minuteStart) {
		this.minuteStart = minuteStart;
		if (pcs != null)
			pcs.firePropertyChange("minuteStart", null, null);
	}

	public int getMinuteEnd() {
		return minuteEnd;
	}

	public void setMinuteEnd(int minuteEnd) {
		this.minuteEnd = minuteEnd;
		if (pcs != null)
			pcs.firePropertyChange("minuteEnd", null, null);
	}

	public void setHoursStartLabel(String hoursStartLabel) {
		this.hoursStartLabel = hoursStartLabel;
		if (pcs != null)
			pcs.firePropertyChange("hoursStartLabel", null, null);
	}

	public void setHoursEndLabel(String hoursEndLabel) {
		this.hoursEndLabel = hoursEndLabel;
		if (pcs != null)
			pcs.firePropertyChange("hoursEndLabel", null, null);
	}

	public void setMinuteStartLabel(String minuteStartLabel) {
		this.minuteStartLabel = minuteStartLabel;
		if (pcs != null)
			pcs.firePropertyChange("minuteStartLabel", null, null);
	}

	public void setMinuteEndLabel(String minuteEndLabel) {
		this.minuteEndLabel = minuteEndLabel;
		if (pcs != null)
			pcs.firePropertyChange("minuteEndLabel", null, null);
	}

	public Boolean getTimeIn() {
		return timeIn;
	}

	public void setTimeIn(Boolean timeIn) {
		this.timeIn = timeIn;
		if (pcs != null)
			pcs.firePropertyChange("timeIn", null, null);
	}

	public Boolean getWeekDayIn() {
		return weekDayIn;
	}

	public void setWeekDayIn(Boolean weekDayIn) {
		this.weekDayIn = weekDayIn;
		if (pcs != null)
			pcs.firePropertyChange("weekDayIn", null, null);
	}

	public Boolean getHolidayIn() {
		return holidayIn;
	}

	public void setHolidayIn(Boolean holidayIn) {
		this.holidayIn = holidayIn;
		if (pcs != null)
			pcs.firePropertyChange("holidayIn", null, null);
	}

	public Boolean getCanOut() {
		if (canOut==null) {
			return true;
		}
		return canOut;
	}

	public void setCanOut(Boolean canOut) {
		this.canOut = canOut;
		firePropertyChange("canOut", null, null);
	}

}
