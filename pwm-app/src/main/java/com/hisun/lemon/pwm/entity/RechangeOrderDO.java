package com.hisun.lemon.pwm.entity;

import java.time.LocalDate;

import com.hisun.lemon.framework.data.BaseDO;

/**
 * PpdOrderDO
 * @author tone
 * @date 2017年6月7日
 * @time 下午2:08:21
 *
 */
public class RechangeOrderDO extends BaseDO{
   
    private String orderNo;
    private LocalDate orderTm;
    private LocalDate orderExpTm;
    private LocalDate accTm; 
    private String orderCcy;
    private Double orderAmt;
    private String orderSataus;
    private LocalDate orderSuccTm; 
    
    private String mobileNo;
    
    private String capCorgNo;
    private String capCardType;
    private String cardNoHide; 
    private String cardNoLast;
    private String cardUserNameHide; 
    private String idType;
    private String idNoHide; 
    private String psnFlag;
    
    private String rutCorgNo;
    private String cropBusType;
    private String cropBusSubType;
    private String sysChannel; 
    private String ipAddress;
    private String remark;
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public LocalDate getOrderTm() {
		return orderTm;
	}
	public void setOrderTm(LocalDate orderTm) {
		this.orderTm = orderTm;
	}
	public LocalDate getOrderExpTm() {
		return orderExpTm;
	}
	public void setOrderExpTm(LocalDate orderExpTm) {
		this.orderExpTm = orderExpTm;
	}
	public LocalDate getAccTm() {
		return accTm;
	}
	public void setAccTm(LocalDate accTm) {
		this.accTm = accTm;
	}
	public String getOrderCcy() {
		return orderCcy;
	}
	public void setOrderCcy(String orderCcy) {
		this.orderCcy = orderCcy;
	}
	public Double getOrderAmt() {
		return orderAmt;
	}
	public void setOrderAmt(Double orderAmt) {
		this.orderAmt = orderAmt;
	}
	public String getOrderSataus() {
		return orderSataus;
	}
	public void setOrderSataus(String orderSataus) {
		this.orderSataus = orderSataus;
	}
	public LocalDate getOrderSuccTm() {
		return orderSuccTm;
	}
	public void setOrderSuccTm(LocalDate orderSuccTm) {
		this.orderSuccTm = orderSuccTm;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getCapCorgNo() {
		return capCorgNo;
	}
	public void setCapCorgNo(String capCorgNo) {
		this.capCorgNo = capCorgNo;
	}
	public String getCapCardType() {
		return capCardType;
	}
	public void setCapCardType(String capCardType) {
		this.capCardType = capCardType;
	}
	public String getCardNoHide() {
		return cardNoHide;
	}
	public void setCardNoHide(String cardNoHide) {
		this.cardNoHide = cardNoHide;
	}
	public String getCardNoLast() {
		return cardNoLast;
	}
	public void setCardNoLast(String cardNoLast) {
		this.cardNoLast = cardNoLast;
	}
	public String getCardUserNameHide() {
		return cardUserNameHide;
	}
	public void setCardUserNameHide(String cardUserNameHide) {
		this.cardUserNameHide = cardUserNameHide;
	}
	public String getPsnFlag() {
		return psnFlag;
	}
	public void setPsnFlag(String psnFlag) {
		this.psnFlag = psnFlag;
	}
	public String getRutCorgNo() {
		return rutCorgNo;
	}
	public void setRutCorgNo(String rutCorgNo) {
		this.rutCorgNo = rutCorgNo;
	}
	public String getCropBusType() {
		return cropBusType;
	}
	public void setCropBusType(String cropBusType) {
		this.cropBusType = cropBusType;
	}
	public String getCropBusSubType() {
		return cropBusSubType;
	}
	public void setCropBusSubType(String cropBusSubType) {
		this.cropBusSubType = cropBusSubType;
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
	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}
	public String getIdNoHide() {
		return idNoHide;
	}
	public void setIdNoHide(String idNoHide) {
		this.idNoHide = idNoHide;
	}
    
    
    
}
