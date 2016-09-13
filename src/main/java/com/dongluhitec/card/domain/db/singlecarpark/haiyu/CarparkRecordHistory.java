package com.dongluhitec.card.domain.db.singlecarpark.haiyu;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.util.StrUtil;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by xiaopan on 2016/8/27.
 */
@Entity
public class CarparkRecordHistory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    private String userName;
    private String plateNO;
    private String inTime;
    private String inDevice;
    private String outTime;
    private String outDevice;
    private Float shouldMoney;
    private Float factMoney;
    private String carparkName;

    @Embedded
    private HistoryDetail historyDetail = new HistoryDetail();

    public CarparkRecordHistory() {
    }

    public CarparkRecordHistory(SingleCarparkInOutHistory carparkInOutHistory,UpdateEnum updateEnum){
        this.userName = StrUtil.isEmptyToString(carparkInOutHistory.getUserName(),"");
        this.plateNO = StrUtil.isEmptyToString(carparkInOutHistory.getPlateNo(), "");
        this.inTime = StrUtil.isEmptyToString(StrUtil.formatDateTime(carparkInOutHistory.getInTime()), "");
        this.outTime = StrUtil.isEmptyToString(StrUtil.formatDateTime(carparkInOutHistory.getOutTime()),"");
        this.inDevice = StrUtil.isEmptyToString(carparkInOutHistory.getInDevice(), "");
        this.outDevice = StrUtil.isEmptyToString(carparkInOutHistory.getOutDevice(), "");
        this.shouldMoney = carparkInOutHistory.getShouldMoney() == null ? 0F : carparkInOutHistory.getShouldMoney();
        this.factMoney = carparkInOutHistory.getFactMoney() == null ? 0F : carparkInOutHistory.getFactMoney();
        this.carparkName = StrUtil.isEmptyToString(carparkInOutHistory.getCarparkName(), "");
        this.historyDetail.setUpdateState(updateEnum);
    }

    @PrePersist
    public void prePersist(){
        this.historyDetail.setUpdateTime(new Date());
        this.historyDetail.setProcessTime(null);
        this.historyDetail.setProcessState(ProcessEnum.未处理);
    }

    @PreUpdate
    public void preUpdate(){
        this.historyDetail.setProcessTime(new Date());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPlateNO() {
        return plateNO;
    }

    public void setPlateNO(String plateNO) {
        this.plateNO = plateNO;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getInDevice() {
        return inDevice;
    }

    public void setInDevice(String inDevice) {
        this.inDevice = inDevice;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public String getOutDevice() {
        return outDevice;
    }

    public void setOutDevice(String outDevice) {
        this.outDevice = outDevice;
    }

    public Float getShouldMoney() {
        return shouldMoney;
    }

    public void setShouldMoney(Float shouldMoney) {
        this.shouldMoney = shouldMoney;
    }

    public Float getFactMoney() {
        return factMoney;
    }

    public void setFactMoney(Float factMoney) {
        this.factMoney = factMoney;
    }

    public String getCarparkName() {
        return carparkName;
    }

    public void setCarparkName(String carparkName) {
        this.carparkName = carparkName;
    }

    public HistoryDetail getHistoryDetail() {
        return historyDetail;
    }

    public void setHistoryDetail(HistoryDetail historyDetail) {
        this.historyDetail = historyDetail;
    }
}
