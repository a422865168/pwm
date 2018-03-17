package com.hisun.lemon.pwm.dto;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;



/**
 * 商户充值下单
 * @author ruan
 * @date 2018年3月13日
 * @time 上午9:27:30
 *
 */
public class RechargeRspDTO {

	@ApiModelProperty(name = "orderNo", value = "收银订单号")
	private String orderNo;

	/**
	 * 账户余额
	 */
	@ApiModelProperty(name = "balAmt", value = "账户余额")
	private BigDecimal balAmt;


	/**
	 * 订单金额
	 */
	@ApiModelProperty(name = "orderAmt", value = "订单金额")
	private BigDecimal orderAmt;

	@ApiModelProperty(name = "payAmt", value = "支付金额")
	private BigDecimal payAmt;

	/**
	 * 手续费
	 */
	@ApiModelProperty(name = "feeAmt", value = "手续费")
	private BigDecimal feeAmt;


    /**
     * 支付链接
     */
    @ApiModelProperty(name = "payUrl", value = "支付链接", required = false, dataType = "String")
    private String payUrl;


	public String getOrderNo() {
		return orderNo;
	}


	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}


	public BigDecimal getBalAmt() {
		return balAmt;
	}


	public void setBalAmt(BigDecimal balAmt) {
		this.balAmt = balAmt;
	}


	public BigDecimal getOrderAmt() {
		return orderAmt;
	}


	public void setOrderAmt(BigDecimal orderAmt) {
		this.orderAmt = orderAmt;
	}


	public BigDecimal getPayAmt() {
		return payAmt;
	}


	public void setPayAmt(BigDecimal payAmt) {
		this.payAmt = payAmt;
	}


	public BigDecimal getFeeAmt() {
		return feeAmt;
	}


	public void setFeeAmt(BigDecimal feeAmt) {
		this.feeAmt = feeAmt;
	}


	public String getPayUrl() {
		return payUrl;
	}


	public void setPayUrl(String payUrl) {
		this.payUrl = payUrl;
	}
	
	
}
