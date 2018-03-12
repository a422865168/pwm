package com.hisun.lemon.pwm.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 提现申请返回参数 传输对象
 * @author leon
 * @date 2017年7月17日
 * @time 上午9:29:32
 *
 */
@ApiModel("提现申请返回参数")
public class WithdrawRspDTO {

	//订单号
	@ApiModelProperty(name = "orderNo", value = "订单号")
    private String orderNo;
	//订单状态
	@ApiModelProperty(name = "orderStatus", value = "订单状态 W1:系统受理中 W2:资金流出已受理 S1:付款成功 F1:付款失败 F2:付款核销 R9:审批拒绝")
    private String orderStatus;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
}
