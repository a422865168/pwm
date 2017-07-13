package com.hisun.lemon.pwm.entity;

import com.hisun.lemon.framework.data.BaseDO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RechangeOrderDO extends BaseDO {
    /**
     * @Fields orderNo 
     */
    private String orderNo;
    /**
     * @Fields orderTm 
     */
    private LocalDateTime orderTm;
    /**
     * @Fields acTm 
     */
    private LocalDate acTm;
    /**
     * @Fields txType 
     */
    private String txType;
    /**
     * @Fields busType 
     */
    private String busType;
    /**
     * @Fields orderCcy 
     */
    private String orderCcy;
    /**
     * @Fields orderAmt 
     */
    private BigDecimal orderAmt;
    /**
     * @Fields orderStatus 
     */
    private String orderStatus;
    /**
     * @Fields orderSuccTm 
     */
    private LocalDateTime orderSuccTm;
    /**
     * @Fields psnFlag 
     */
    private String psnFlag;
    /**
     * @Fields orderExpTm 
     */
    private LocalDateTime orderExpTm;
    /**
     * @Fields sysChannel 
     */
    private String sysChannel;
    /**
     * @Fields ipAddress 
     */
    private String ipAddress;
    /**
     * @Fields remark 
     */
    private String remark;
    /**
     * @Fields modifyOpr 
     */
    private String modifyOpr;
  

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public LocalDateTime getOrderTm() {
        return orderTm;
    }

    public void setOrderTm(LocalDateTime orderTm) {
        this.orderTm = orderTm;
    }

    public LocalDate getAcTm() {
        return acTm;
    }

    public void setAcTm(LocalDate acTm) {
        this.acTm = acTm;
    }

    public String getTxType() {
        return txType;
    }

    public void setTxType(String txType) {
        this.txType = txType;
    }

    public String getBusType() {
        return busType;
    }

    public void setBusType(String busType) {
        this.busType = busType;
    }

    public String getOrderCcy() {
        return orderCcy;
    }

    public void setOrderCcy(String orderCcy) {
        this.orderCcy = orderCcy;
    }

    public BigDecimal getOrderAmt() {
        return orderAmt;
    }

    public void setOrderAmt(BigDecimal orderAmt) {
        this.orderAmt = orderAmt;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getOrderSuccTm() {
        return orderSuccTm;
    }

    public void setOrderSuccTm(LocalDateTime orderSuccTm) {
        this.orderSuccTm = orderSuccTm;
    }

    public String getPsnFlag() {
        return psnFlag;
    }

    public void setPsnFlag(String psnFlag) {
        this.psnFlag = psnFlag;
    }

    public LocalDateTime getOrderExpTm() {
        return orderExpTm;
    }

    public void setOrderExpTm(LocalDateTime orderExpTm) {
        this.orderExpTm = orderExpTm;
    }

    public String getSysChannel() {
        return sysChannel;
    }

    public void setSysChannel(String sysChannel) {
        this.sysChannel = sysChannel;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getModifyOpr() {
        return modifyOpr;
    }

    public void setModifyOpr(String modifyOpr) {
        this.modifyOpr = modifyOpr;
    }
 
}