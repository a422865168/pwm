package com.hisun.lemon.pwm.dto;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;

/**
 * 充值请求 传输对象
 * 
 * @author ruan
 * @date 2017年7月14日
 * @time 上午9:27:30
 *
 */
public class RechargeRspHCouponDTO {
	/**
	 *金额
	 */
	@ApiModelProperty(name = "orderAmount", value = "金额")
	private BigDecimal orderAmount;
	
	/**
	 *海币充值数量
	 */
	@ApiModelProperty(name = "hCouponAmt", value = "海币充值数量")
	private BigDecimal hCouponAmt;

	public BigDecimal getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(BigDecimal orderAmount) {
		this.orderAmount = orderAmount;
	}

	/**
	 * 充值订单号
	 */
	@ApiModelProperty(name = "orderNo", value = "充值订单号")
	private String orderNo;

	/**
	 * 状态
	 */
	@ApiModelProperty(name = "orderSts", value = "状态   S:成功   F：失败   U:充值初始状态")
	private String orderSts;
	

	public BigDecimal gethCouponAmt() {
		return hCouponAmt;
	}

	public void sethCouponAmt(BigDecimal hCouponAmt) {
		this.hCouponAmt = hCouponAmt;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOrderSts() {
		return orderSts;
	}

	public void setOrderSts(String orderSts) {
		this.orderSts = orderSts;
	}

}
