package com.dongluhitec.card.domain.db.singlecarpark.haiyu;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by xiaopan on 2016/8/23.
 */
@Embeddable
public class HistoryDetail implements Serializable {

    public enum Property{
        updateState,updateTime,processTime,processState
    }

    @Column
    @Enumerated(EnumType.STRING)
    private UpdateEnum updateState;

    @Column(name = "updateTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @Column(name = "processTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date processTime;

    @Column
    @Enumerated(EnumType.STRING)
    private ProcessEnum processState;

    public UpdateEnum getUpdateState() {
        return updateState;
    }

    public void setUpdateState(UpdateEnum updateState) {
        this.updateState = updateState;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getProcessTime() {
        return processTime;
    }

    public void setProcessTime(Date processTime) {
        this.processTime = processTime;
    }

    public ProcessEnum getProcessState() {
        return processState;
    }

    public void setProcessState(ProcessEnum processState) {
        this.processState = processState;
    }
}
