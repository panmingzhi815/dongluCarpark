package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Index;

import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.util.StrUtil;
/**
 * 保存车辆缴费记录
 * @author Administrator
 *
 */
@Entity
public class CarPayHistory extends DomainObject{
	public enum PayTypeEnum {
		现金支付,支付宝,微信支付,App支付,优惠券抵扣;

		public static PayTypeEnum getType(int intValue) {
			switch (intValue) {
			case 2:
				return 支付宝;
			case 3:
				return 微信支付;
			case 5:
				return App支付;
			case 6:
				return App支付;
			case 15:
				return 优惠券抵扣;
			}
			return 现金支付;
		}
		public int toType(){
			if(this.ordinal()>0) {
				return 1;
			}
			return 0;
		}
	}
	public enum Property{
		plateNO,payTime,createDate,payedMoney,remark,operaName,payId,historyId
	}
	public enum Label{
		plateNO,payTimeLabel,createDateLabel,payedMoney,remark,operaName,payType,inTimeLabel,cashCost,onlineCost,couponValue
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 4222967774413025501L;
	@Index(name="CarPayHistory_plateNO_index")
	private String plateNO;
	@Column(unique=true)
	private String payId;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	@Temporal(TemporalType.TIMESTAMP)
	@Index(name="CarPayHistory_payTime_index")
	private Date payTime;
	private float payedMoney=0;
	private String operaName;
	private PayTypeEnum payType=PayTypeEnum.现金支付;
	private String remark;
	private Double balanceAmount=0d;//余额支付
	private Double cashCost=0d; //现金支付
	private Double onlineCost=0d;//在线支付
	private Double couponValue=0d;//优惠金额
	private Integer couponTime=0;//优惠时长（）
	private String couponCode;//优惠编码
	private Long historyId;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date inTime;
	@Temporal(TemporalType.TIMESTAMP)
	private Date outTime;
	public CarPayHistory(){
		
	}
	public CarPayHistory(SingleCarparkInOutHistory inout) {
		plateNO=inout.getPlateNo();
		inTime=inout.getInTime();
		outTime=inout.getOutTime();
		payTime=inout.getOutTime()==null?inout.getChargeTime():inout.getOutTime();
		historyId=inout.getId();
		cashCost=inout.getFactMoney().doubleValue();
		operaName=inout.getOperaName();
		createDate=new Date();
		payedMoney=inout.getShouldMoney();
		cashCost=inout.getFactMoney().doubleValue()-inout.getOnlineMoney();
		couponValue=inout.getFreeMoney().doubleValue();
		onlineCost=inout.getOnlineMoney().doubleValue();
		payId=UUID.randomUUID().toString().replace("-", "");
	}
	public String getInTimeLabel(){
		return StrUtil.formatDateTime(inTime);
	}
	public String getPayTimeLabel(){
		return StrUtil.formatDateTime(payTime);
	}
	public String getCreateDateLabel(){
		return StrUtil.formatDateTime(createDate);
	}
	public String getPlateNO() {
		return plateNO;
	}
	public void setPlateNO(String plateNO) {
		this.plateNO = plateNO;
		firePropertyChange("plateNO", null, null);
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
		firePropertyChange("createDate", null, null);
	}
	public Date getPayTime() {
		return payTime;
	}
	public void setPayTime(Date payTime) {
		this.payTime = payTime;
		firePropertyChange("payTime", null, null);
	}
	public float getPayedMoney() {
		return payedMoney;
	}
	public void setPayedMoney(float payedMoney) {
		this.payedMoney = payedMoney;
		firePropertyChange("payedMoney", null, null);
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
		firePropertyChange("remark", null, null);
	}
	public String getOperaName() {
		return operaName;
	}
	public void setOperaName(String operaName) {
		this.operaName = operaName;
		firePropertyChange("operaName", null, null);
	}
	public PayTypeEnum getPayType() {
		if (payType==null) {
			return PayTypeEnum.现金支付;
		}
		return payType;
	}
	public void setPayType(PayTypeEnum payType) {
		this.payType = payType;
		firePropertyChange("payType", null, null);
	}
	public String getPayId() {
		return payId;
	}
	public void setPayId(String payId) {
		this.payId = payId;
		firePropertyChange("payId", null, null);
	}
	public Double getBalanceAmount() {
		if (balanceAmount==null) {
			return 0d;
		}
		return balanceAmount;
	}
	public void setBalanceAmount(Double balanceAmount) {
		this.balanceAmount = balanceAmount;
		firePropertyChange("balanceAmount", null, null);
	}
	public Double getCashCost() {
		if (cashCost==null) {
			return 0d;
		}
		return cashCost;
	}
	public void setCashCost(Double cashCost) {
		this.cashCost = cashCost;
		firePropertyChange("cashCost", null, null);
	}
	public Double getOnlineCost() {
		if (onlineCost==null) {
			return 0d;
		}
		return onlineCost;
	}
	public void setOnlineCost(Double onlineCost) {
		this.onlineCost = onlineCost;
		firePropertyChange("onlineCost", null, null);
	}
	public Long getHistoryId() {
		return historyId;
	}
	public void setHistoryId(Long historyId) {
		this.historyId = historyId;
	}
	public Double getCouponValue() {
		if (couponValue==null) {
			return 0d;
		}
		return couponValue;
	}
	public void setCouponValue(Double couponValue) {
		this.couponValue = couponValue;
		firePropertyChange("couponValue", null, null);
	}
	public Integer getCouponTime() {
		return couponTime;
	}
	public void setCouponTime(Integer couponTime) {
		this.couponTime = couponTime;
		firePropertyChange("couponTime", null, null);
	}
	public Date getInTime() {
		return inTime;
	}
	public void setInTime(Date inTime) {
		this.inTime = inTime;
	}
	public Date getOutTime() {
		return outTime;
	}
	public void setOutTime(Date outTime) {
		this.outTime = outTime;
	}
	public String getCouponCode() {
		return couponCode;
	}
	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
		//firePropertyChange("couponCode", null, null);
	}
}
