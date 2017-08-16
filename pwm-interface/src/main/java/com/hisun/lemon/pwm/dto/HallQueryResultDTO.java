package com.hisun.lemon.pwm.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * 营业厅充值查询结果 传输对象
 * @author tone
 * @date 2017年7月21日
 * @time 上午9:27:30
 *
 */
@ApiModel("营业厅充值查询结果传输对象")
public class HallQueryResultDTO {

	@ApiModelProperty(name = "orderNo", value = "平台充值订单号")
	private  String orderNo;
	/**
	 * 查询关键字
	 */
	@ApiModelProperty(name = "key", value = "查询关键字")
    private String key;
	/**
	 * 用户或商户ID
	 */
	@ApiModelProperty(name = "umId", value = "用户或商户ID")
	private String umId;

	/**
	 * 用户或商户名称
	 */
	@ApiModelProperty(name = "umName", value = "用户或商户名称")
	private  String umName;

	/**
	 * 手续费
	 */
	@ApiModelProperty(name = "fee", value = "手续费")
	private BigDecimal fee;

	/**
	 * 营业厅充值订单号
	 */
	@ApiModelProperty(name = "hallOrderNo", value = "营业厅充值订单号")
	private  String hallOrderNo;

	/**
	 * 营业厅充值订单状态
	 */
	@ApiModelProperty(name = "orderStatus", value = "营业厅充值订单状态")
	private  String orderStatus;

	@ApiModelProperty(name = "orderAmt", value = "订单金额")
	private  BigDecimal orderAmt;

	/**
	 * 下单时间
	 */
	@ApiModelProperty(name = "orderTm", value = "下单时间")
	private LocalDateTime orderTm;

	/**
	 * 订单交易成功时间
	 */
	@ApiModelProperty(name = "orderTm", value = "交易完成时间")
	private LocalDateTime orderSuccTm;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getUmId() {
		return umId;
	}

	public void setUmId(String umId) {
		this.umId = umId;
	}

	public String getUmName() {
		return umName;
	}

	public void setUmName(String umName) {
		this.umName = umName;
	}

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

	public LocalDateTime getOrderSuccTm() {
		return orderSuccTm;
	}

	public void setOrderSuccTm(LocalDateTime orderSuccTm) {
		this.orderSuccTm = orderSuccTm;
	}
}
