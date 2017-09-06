/*
 * @ClassName WithdrawOrderDO
 * @Description 
 * @version 1.0
 * @Date 2017-07-15 15:25:38
 */
package com.hisun.lemon.pwm.entity;

import com.hisun.lemon.framework.data.BaseDO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class WithdrawOrderDO extends BaseDO {
    /**
     * @Fields orderNo 订单号
     */
    private String orderNo;
    /**
     * @Fields orderTm 订单时间
     */
    private LocalDateTime orderTm;
    /**
     * @Fields orderExpTm 订单失效时间
     */
    private LocalDateTime orderExpTm;
    /**
     * @Fields acTm 记账时间
     */
    private LocalDate acTm;
    /**
     * @Fields orderCcy 币种
     */
    private String orderCcy;
    /**
     * @Fields orderSuccTm 订单成功时间
     */
    private LocalDateTime orderSuccTm;
    /**
     * @Fields wcType 提现类型 11:自主提现 21:自动结算
     */
    private String wcType;
    /**
     * @Fields txType 交易类型 01.充值 02.消费 03.转账 04.提现 05.充海币
     */
    private String txType;
    /**
     * @Fields busType 业务类型 04:提现 0401:个人提现 0402:商户提现
     */
    private String busType;
    /**
     * @Fields wcApplyAmt 申请提现金额
     */
    private BigDecimal wcApplyAmt;
    /**
     * @Fields wcActAmt 实际提现金额
     */
    private BigDecimal wcActAmt;
    /**
     * @Fields wcActAmt 提现总金额
     */
    private BigDecimal wcTotalAmt;
    /**
     * @Fields feeAmt 手续费金额
     */
    private BigDecimal feeAmt;
    /**
     * @Fields payUrgeFlg 付款加急标识
     */
    private String payUrgeFlg;
    /**
     * @Fields userId 内部用户编号
     */
    private String userId;
    /**
     * @Fields userName 用户/商户名称
     */
    private String userName;
    /**
     * @Fields agrNo 签约协议号
     */
    private String agrNo;
    /**
     * @Fields capCorgNo 资金合作机构号
     */
    private String capCorgNo;
    /**
     * @Fields capCardNo 资金卡号
     */
    private String capCardNo;
    /**
     * @Fields capCardType 资金卡账户类型 0:借记卡 1:信用卡 2:准贷记卡 3:储蓄账户
     */
    private String capCardType;
    /**
     * @Fields capCardName 资金卡账户姓名
     */
    private String capCardName;
    /**
     * @Fields wcRemark 提现备注
     */
    private String wcRemark;
    /**
     * @Fields ntfMbl 通知的手机号
     */
    private String ntfMbl;
    /**
     * @Fields orderStatus 订单状态 W1:系统受理中 W2:资金流出已受理 S1:付款成功 F1:付款失败 F2:付款核销 R9:审批拒绝
     */
    private String orderStatus;
    /**
     * @Fields rspOrderNo 资金流出模块订单号
     */
    private String rspOrderNo;
    /**
     * @Fields rspSuccTm 资金流出模块成功时间
     */
    private LocalDateTime rspSuccTm;
    /**
     * @Fields busCnl 业务受理渠道
     */
    private String busCnl;

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

    public LocalDateTime getRspSuccTm() {
        return rspSuccTm;
    }

    public void setRspSuccTm(LocalDateTime rspSuccTm) {
        this.rspSuccTm = rspSuccTm;
    }

    public String getBusCnl() {
        return busCnl;
    }

    public void setBusCnl(String busCnl) {
        this.busCnl = busCnl;
    }

    public BigDecimal getWcTotalAmt() {
        return wcTotalAmt;
    }

    public void setWcTotalAmt(BigDecimal wcTotalAmt) {
        this.wcTotalAmt = wcTotalAmt;
    }
}