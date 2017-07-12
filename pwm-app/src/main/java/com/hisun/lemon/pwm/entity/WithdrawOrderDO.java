package com.hisun.lemon.pwm.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.hisun.lemon.framework.data.BaseDO;

/**
 * WithdrawOrderDO
 * @author tone
 * @date 2017年6月7日
 * @time 下午2:08:21
 *
 */
public class WithdrawOrderDO extends BaseDO{
    //订单号
    private String orderNo;
    //订单时间
    private LocalDate orderTm;
    //订单失效时间
    private LocalDate orderExpTm;
    //记账时间
    private LocalDate accTm;
    //币种
    private String orderCcy;
    //订单成功时间
    private LocalDate orderSuccTm; 
    //提现类型
    private String withdrawType; 
    //交易类型
    private String busType;
    //申请提现金额
    private BigDecimal applyAmount;
    //实际体现金额
    private BigDecimal actAmount;
    //手续费金额
    private BigDecimal feeAmount;
    //付款加急标识
    private String urgeFlag;
    
    private String userId;
    private String userName;
    //协议号
    private String agreementNo;
    private String capCorgNo; 
    private String capCardNo; 
    private String capCardType;
    private String capCardName; 
    private String remark; 
    private String notifyMobileNo;
    //订单状态
    private String orderSataus;
    private String rspOrderNo;
    private LocalDate rspOrderSuccTm;
    private String sysChannel; 
    private String ipAddress;
    //支付密码
    private String payPassword;
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
	public LocalDate getOrderSuccTm() {
		return orderSuccTm;
	}
	public void setOrderSuccTm(LocalDate orderSuccTm) {
		this.orderSuccTm = orderSuccTm;
	}
	public String getWithdrawType() {
		return withdrawType;
	}
	public void setWithdrawType(String withdrawType) {
		this.withdrawType = withdrawType;
	}
	public String getBusType() {
		return busType;
	}
	public void setBusType(String busType) {
		this.busType = busType;
	}
	public BigDecimal getApplyAmount() {
		return applyAmount;
	}
	public void setApplyAmount(BigDecimal applyAmount) {
		this.applyAmount = applyAmount;
	}
	public BigDecimal getActAmount() {
		return actAmount;
	}
	public void setActAmount(BigDecimal actAmount) {
		this.actAmount = actAmount;
	}
	public BigDecimal getFeeAmount() {
		return feeAmount;
	}
	public void setFeeAmount(BigDecimal feeAmount) {
		this.feeAmount = feeAmount;
	}
	public String getUrgeFlag() {
		return urgeFlag;
	}
	public void setUrgeFlag(String urgeFlag) {
		this.urgeFlag = urgeFlag;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getAgreementNo() {
		return agreementNo;
	}
	public void setAgreementNo(String agreementNo) {
		this.agreementNo = agreementNo;
	}
	public String getCapCorgNo() {
		return capCorgNo;
	}
	public void setCapCorgNo(String capCorgNo) {
		this.capCorgNo = capCorgNo;
	}
	public String getCapCardNo() {
		return capCardNo;
	}
	public void setCapCardNo(String capCardNo) {
		this.capCardNo = capCardNo;
	}
	public String getCapCardType() {
		return capCardType;
	}
	public void setCapCardType(String capCardType) {
		this.capCardType = capCardType;
	}
	public String getCapCardName() {
		return capCardName;
	}
	public void setCapCardName(String capCardName) {
		this.capCardName = capCardName;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getNotifyMobileNo() {
		return notifyMobileNo;
	}
	public void setNotifyMobileNo(String notifyMobileNo) {
		this.notifyMobileNo = notifyMobileNo;
	}
	public String getOrderSataus() {
		return orderSataus;
	}
	public void setOrderSataus(String orderSataus) {
		this.orderSataus = orderSataus;
	}
	public String getRspOrderNo() {
		return rspOrderNo;
	}
	public void setRspOrderNo(String rspOrderNo) {
		this.rspOrderNo = rspOrderNo;
	}
	public LocalDate getRspOrderSuccTm() {
		return rspOrderSuccTm;
	}
	public void setRspOrderSuccTm(LocalDate rspOrderSuccTm) {
		this.rspOrderSuccTm = rspOrderSuccTm;
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
	public String getPayPassword() {
		return payPassword;
	}
	public void setPayPassword(String payPassword) {
		this.payPassword = payPassword;
	}
}
