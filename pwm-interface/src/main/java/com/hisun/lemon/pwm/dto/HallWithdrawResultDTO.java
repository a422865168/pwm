package com.hisun.lemon.pwm.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 营业厅个人提现响应 传输对象
 * @author xian
 * @date 2017年11月1日
 * @time 上午9:27:30
 *
 */
@ApiModel(" 营业厅个人提现结果对象")
public class HallWithdrawResultDTO {

    /**
     * 提现手续费
     * */
    @ApiModelProperty(name = "feeAmt", value = "提现手续费")
    private String feeAmt;

    /**
     * 提现手续费
     * */
    @ApiModelProperty(name = "withdrawAmt", value = "申请提现金额")
    private String withdrawAmt;

    /**
     * 业务订单号
     * */
    @ApiModelProperty(name = "busOrderNo", value = "业务订单号(营业厅请求订单号)")
    private String busOrderNo;

    /**
     * 平台订单号
     */
    @ApiModelProperty(name = "orderNo", value = "平台提现订单号")
    private String orderNo;

    /**
     * 订单状态 success-成功；fail-失败
     */
    @ApiModelProperty(name = "orderSts", value = "订单状态")
    private String orderSts;


    /**
     * 订单日期
     * */
    @ApiModelProperty(name = "orderDate", value = "交易日期")
    private LocalDate tradeDate;


    /**
     * 订单时间
     * */
    @ApiModelProperty(name = "orderTime", value = "交易时间")
    private LocalTime tradeTime;


    public String getBusOrderNo() {
        return busOrderNo;
    }

    public void setBusOrderNo(String busOrderNo) {
        this.busOrderNo = busOrderNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public LocalDate getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(LocalDate tradeDate) {
        this.tradeDate = tradeDate;
    }

    public LocalTime getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(LocalTime tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getFeeAmt() {
        return feeAmt;
    }

    public void setFeeAmt(String feeAmt) {
        this.feeAmt = feeAmt;
    }

    public String getOrderSts() {
        return orderSts;
    }

    public void setOrderSts(String orderSts) {
        this.orderSts = orderSts;
    }

    public String getWithdrawAmt() {
        return withdrawAmt;
    }

    public void setWithdrawAmt(String withdrawAmt) {
        this.withdrawAmt = withdrawAmt;
    }
}
