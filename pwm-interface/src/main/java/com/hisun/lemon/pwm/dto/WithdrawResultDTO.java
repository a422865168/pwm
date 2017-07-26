package com.hisun.lemon.pwm.dto;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import java.math.BigDecimal;

/**
 * 提现结果处理：等待异步通知，传输对象
 * @author leon
 * @date 2017/7/17
 * @time 15:43
 */
public class WithdrawResultDTO {

    //提现订单号
    @NotEmpty(message="PWM10005")
    private String orderNo;
    //实际提现金额
    @Min(value=0, message="PWM10026")
    private BigDecimal wcActAmt;
    //资金流出模块订单号
    private String rspOrderNo;
    //资金流出模块成功时间
    private String rspSuccTm;
    //记账时间
    private String acTm;
    //提现状态
    @NotEmpty(message="PWM10009")
    private String orderStatus;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getWcActAmt() {
        return wcActAmt;
    }

    public void setWcActAmt(BigDecimal wcActAmt) {
        this.wcActAmt = wcActAmt;
    }

    public String getRspOrderNo() {
        return rspOrderNo;
    }

    public void setRspOrderNo(String rspOrderNo) {
        this.rspOrderNo = rspOrderNo;
    }

    public String getRspSuccTm() {
        return rspSuccTm;
    }

    public void setRspSuccTm(String rspSuccTm) {
        this.rspSuccTm = rspSuccTm;
    }

    public String getAcTm() {
        return acTm;
    }

    public void setAcTm(String acTm) {
        this.acTm = acTm;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
