package com.hisun.lemon.pwm.dto;

import java.math.BigDecimal;


/**
 * 营业厅充值响应 传输对象
 * @author tone
 * @date 2017年7月21日
 * @time 上午9:27:30
 *
 */
public class HallRechargeResultDTO {
	/**
	 * 营业厅订单号
	 */
    private String hallOrderNo;
	/**
	 * 平台订单号
	 */
	private String orderNo;

	/**
	 * 状态
	 */
	private  String status;
	/**
	 *订单金额
	 */
	private BigDecimal amount;
	/**
	 * 手续费
	 */
	private BigDecimal fee;

	public String getHallOrderNo() {
		return hallOrderNo;
	}

	public void setHallOrderNo(String hallOrderNo) {
		this.hallOrderNo = hallOrderNo;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}
}
