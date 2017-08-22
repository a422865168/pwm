package com.hisun.lemon.pwm.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * 营业厅充值订单查询结果 传输对象
 * @author xian
 * @date 2017年8月22日
 * @time 上午10:27:30
 *
 */
@ApiModel("营业厅充值订单 查询结果传输对象")
public class HallOrderQueryResultDTO {

	/**
	 * 营业厅充值订单号
	 */
	@ApiModelProperty(name = "hallOrderNo", value = "营业厅充值订单号")
	private  String hallOrderNo;

	@ApiModelProperty(name = "orderNo", value = "平台充值订单号")
	private  String orderNo;

	/**
	 * 手续费
	 */
	@ApiModelProperty(name = "fee", value = "手续费")
	private BigDecimal fee;

	/**
	 * 充值订单状态
	 */
	@ApiModelProperty(name = "orderStatus", value = "充值订单状态")
	private  String orderStatus;

	@ApiModelProperty(name = "orderAmt", value = "订单金额")
	private  BigDecimal orderAmt;

	@ApiModelProperty(name = "totalAmt", value = "总金额")
	private  BigDecimal totalAmt;

	/**
	 * 下单时间
	 */
	@ApiModelProperty(name = "orderTm", value = "下单时间")
	private LocalDateTime orderTm;


	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public String getHallOrderNo() {
		return hallOrderNo;
	}

	public void setHallOrderNo(String hallOrderNo) {
		this.hallOrderNo = hallOrderNo;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public BigDecimal getOrderAmt() {
		return orderAmt;
	}

	public void setOrderAmt(BigDecimal orderAmt) {
		this.orderAmt = orderAmt;
	}

	public LocalDateTime getOrderTm() {
		return orderTm;
	}

	public void setOrderTm(LocalDateTime orderTm) {
		this.orderTm = orderTm;
	}

	public BigDecimal getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(BigDecimal totalAmt) {
		this.totalAmt = totalAmt;
	}
}
