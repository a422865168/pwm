package com.hisun.lemon.pwm.entity;

import com.hisun.lemon.framework.data.BaseDO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RechargeOrderDO extends BaseDO {
    /**
     * @Fields orderNo 订单号
     */
    private String orderNo;
    /**
     * @Fields orderTm 订单时间
     */
    private LocalDateTime orderTm;
    /**
     * @Fields acTm  会计日期
     */
    private LocalDate acTm;
    /**
     * @Fields txType 交易类型
     */
    private String txType;
    /**
     * @Fields busType  业务类型
     */
    private String busType;
    /**
     * @Fields orderCcy 币种
     */
    private String orderCcy;
    /**
     * @Fields orderAmt 订单金额
     */
    private BigDecimal orderAmt;
    /**
     * @Fields orderStatus  订单状态
     */
    private String orderStatus;
    /**
     * @Fields fee 充值手续费
     */
    private BigDecimal fee;
    /**
     * @Fields hallOrderNo 营业厅充值订单号
     */
    private String hallOrderNo;
    /**
     * @Fields orderSuccTm 订单成功时间
     */
    private LocalDateTime orderSuccTm;
    /**
     * @Fields psnFlag 个企标志
     */
    private String psnFlag;
    /**
     * @Fields orderExpTm 过期时间
     */
    private LocalDateTime orderExpTm;
    /**
     * @Fields sysChannel 订单渠道
     */
    private String sysChannel;
    /**
     * @Fields ipAddress ip地址
     */
    private String ipAddress;
    /**
     * @Fields remark 备注
     */
    private String remark;
    /**
     * @Fields modifyOpr  最后修改人
     */
    private String modifyOpr;

    /**
     * @Fields extOrderNo  外部订单号
     */
    private String extOrderNo;

    /**
     * @Fields crdCorpOrg  资金机构
     */
    private String crdCorpOrg;
  

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

    public void setExtOrderNo(String extOrderNo) {
        this.extOrderNo = extOrderNo;
    }

    public String getExtOrderNo() {
        return extOrderNo;
    }

    public String getCrdCorpOrg() {
        return crdCorpOrg;
    }

    public void setCrdCorpOrg(String crdCorpOrg) {
        this.crdCorpOrg = crdCorpOrg;
    }


    public String getHallOrderNo() {
        return hallOrderNo;
    }

    public void setHallOrderNo(String hallOrderNo) {
        this.hallOrderNo = hallOrderNo;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }
}