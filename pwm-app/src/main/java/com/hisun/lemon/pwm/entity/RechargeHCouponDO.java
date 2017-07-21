/*
 * @ClassName RechargeHCouponDO
 * @Description 
 * @version 1.0
 * @Date 2017-07-21 17:27:24
 */
package com.hisun.lemon.pwm.entity;

import com.hisun.lemon.framework.data.BaseDO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
     * @Fields orderStatus 订单状态
     */
    private String orderStatus;
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
    /**
     * @Fields tmSmp 时间戳
     */
    private LocalDateTime tmSmp;

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

    public LocalDateTime getTmSmp() {
        return tmSmp;
    }

    public void setTmSmp(LocalDateTime tmSmp) {
        this.tmSmp = tmSmp;
    }
}