package com.hisun.lemon.pwm.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private LocalDateTime orderTm;
    //订单失效时间
    private LocalDateTime orderExpTm;
    //记账时间
    private LocalDate acTm;
    //币种
    private String orderCcy;
    //订单成功时间
    private LocalDateTime orderSuccTm;
    //提现类型
    private String wcType; 
    //交易类型
	private String txType;
	//业务类型
    private String busType;
    //申请提现金额
    private BigDecimal wcApplyAmt;
    //实际体现金额
    private BigDecimal wcActAmt;
    //手续费金额
    private BigDecimal feeAmt;
    //付款加急标识
    private String payUrgeFlg;

    private String userId;
    private String userName;
    //协议号
    private String agrNo;
    private String capCorgNo; 
    private String capCardNo; 
    private String capCardType;
    private String capCardName; 
    private String wcRemark;
    private String ntfMbl;
    //订单状态
    private String orderStatus;
    private String rspOrderNo;
    private LocalDateTime rspSuccTm;
    private String busCnl; 
    private String userIpAdr;
    //支付密码
    private String payPassword;
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
	public LocalDateTime getOrderExpTm() {
		return orderExpTm;
	}
	public void setOrderExpTm(LocalDateTime orderExpTm) {
		this.orderExpTm = orderExpTm;
	}
	public LocalDate getAcTm() {
		return acTm;
	}
	public void setAcTm(LocalDate acTm) {
		this.acTm = acTm;
	}
	public String getOrderCcy() {
		return orderCcy;
	}
	public void setOrderCcy(String orderCcy) {
		this.orderCcy = orderCcy;
	}
	public LocalDateTime getOrderSuccTm() {
		return orderSuccTm;
	}
	public void setOrderSuccTm(LocalDateTime orderSuccTm) {
		this.orderSuccTm = orderSuccTm;
	}
	public String getWcType() {
		return wcType;
	}
	public void setWcType(String wcType) {
		this.wcType = wcType;
	}
	public String getBusType() {
		return busType;
	}
	public void setBusType(String busType) {
		this.busType = busType;
	}
	public BigDecimal getWcApplyAmt() {
		return wcApplyAmt;
	}
	public void setWcApplyAmt(BigDecimal wcApplyAmt) {
		this.wcApplyAmt = wcApplyAmt;
	}
	public BigDecimal getWcActAmt() {
		return wcActAmt;
	}
	public void setWcActAmt(BigDecimal wcActAmt) {
		this.wcActAmt = wcActAmt;
	}
	public BigDecimal getFeeAmt() {
		return feeAmt;
	}
	public void setFeeAmt(BigDecimal feeAmt) {
		this.feeAmt = feeAmt;
	}
	public String getPayUrgeFlg() {
		return payUrgeFlg;
	}
	public void setPayUrgeFlg(String payUrgeFlg) {
		this.payUrgeFlg = payUrgeFlg;
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
	public String getAgrNo() {
		return agrNo;
	}
	public void setAgrNo(String agrNo) {
		this.agrNo = agrNo;
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
	public String getWcRemark() {
		return wcRemark;
	}
	public void setWcRemark(String wcRemark) {
		this.wcRemark = wcRemark;
	}
	public String getNtfMbl() {
		return ntfMbl;
	}
	public void setNtfMbl(String ntfMbl) {
		this.ntfMbl = ntfMbl;
	}
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	public String getRspOrderNo() {
		return rspOrderNo;
	}
	public void setRspOrderNo(String rspOrderNo) {
		this.rspOrderNo = rspOrderNo;
	}
	public LocalDateTime getrspSuccTm() {
		return rspSuccTm;
	}
	public void setrspSuccTm(LocalDateTime rspSuccTm) {
		this.rspSuccTm = rspSuccTm;
	}
	public String getBusCnl() {
		return busCnl;
	}
	public void setBusCnl(String busCnl) {
		this.busCnl = busCnl;
	}
	public String getUserIpAdr() {
		return userIpAdr;
	}
	public void setUserIpAdr(String userIpAdr) {
		this.userIpAdr = userIpAdr;
	}
	public String getPayPassword() {
		return payPassword;
	}
	public void setPayPassword(String payPassword) {
		this.payPassword = payPassword;
	}

	public String getTxType() {
		return txType;
	}

	public void setTxType(String txType) {
		this.txType = txType;
	}
}
