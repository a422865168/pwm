package com.hisun.lemon.pwm.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.hisun.lemon.framework.data.BaseDO;

public class RechargeHCouponDO extends BaseDO {
	 /**
     * @Fields orderNo 海币充值订单编号
     */
    private String orderNo;
    /**
     * @Fields userId 内部用户号
     */
    private String userId;
    /**
     * @Fields orderAmt 充值金额
     */
    private BigDecimal orderAmt;
    /**
     * @Fields orderCcy 币种
     */
    private String orderCcy;
    /**
     * @Fields sysChannel 渠道
     */
    private String sysChannel;
    /**
     * @Fields orderStatus 订单状态
     */
    private String orderStatus;
    /**
     * @Fields busType 业务类型  0501充海币
     */
    private String busType;
    /**
     * @Fields txType 交易类型  05
     */
    private String txType;
    /**
     * @Fields hCouponAmt 海币数量
     */
    private BigDecimal hCouponAmt;
    /**
     * @Fields acTm 会计日期
     */
    private LocalDate acTm;
    /**
     * @Fields txTm 交易时间
     */
    private LocalDateTime txTm;
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public BigDecimal getOrderAmt() {
		return orderAmt;
	}
	public void setOrderAmt(BigDecimal orderAmt) {
		this.orderAmt = orderAmt;
	}
	public String getOrderCcy() {
		return orderCcy;
	}
	public void setOrderCcy(String orderCcy) {
		this.orderCcy = orderCcy;
	}
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	public String getBusType() {
		return busType;
	}
	public void setBusType(String busType) {
		this.busType = busType;
	}
	public String getTxType() {
		return txType;
	}
	public void setTxType(String txType) {
		this.txType = txType;
	}
	public BigDecimal gethCouponAmt() {
		return hCouponAmt;
	}
	public void sethCouponAmt(BigDecimal hCouponAmt) {
		this.hCouponAmt = hCouponAmt;
	}
	public LocalDate getAcTm() {
		return acTm;
	}
	public void setAcTm(LocalDate acTm) {
		this.acTm = acTm;
	}
	public LocalDateTime getTxTm() {
		return txTm;
	}
	public void setTxTm(LocalDateTime txTm) {
		this.txTm = txTm;
	}
	public String getSysChannel() {
		return sysChannel;
	}
	public void setSysChannel(String sysChannel) {
		this.sysChannel = sysChannel;
	}
}