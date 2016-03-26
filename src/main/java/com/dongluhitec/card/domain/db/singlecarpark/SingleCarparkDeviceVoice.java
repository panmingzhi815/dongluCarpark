package com.dongluhitec.card.domain.db.singlecarpark;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.dongluhitec.card.domain.db.DomainObject;
@Entity
public class SingleCarparkDeviceVoice extends DomainObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3702764686205582814L;
	@Enumerated(EnumType.STRING)
	private DeviceVoiceTypeEnum type;
	@Column(length=39)
	private String content;
	private int volume=1;
	
	public DeviceVoiceTypeEnum getType() {
		return type;
	}
	public void setType(DeviceVoiceTypeEnum type) {
		this.type = type;
		firePropertyChange("type", null, null);
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
		firePropertyChange("content", null, null);
	}
	public int getVolume() {
		return volume;
	}
	public void setVolume(int volume) {
		this.volume = volume;
		firePropertyChange("volume", null, null);
	}

}
