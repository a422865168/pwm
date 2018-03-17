package com.hisun.lemon.pwm.dto;

import io.swagger.annotations.ApiModelProperty;



/**
 * 订单状态查询
 * @author ruan
 * @date 2018年3月13日
 * @time 上午9:27:30
 *
 */
public class OrderSearchRspDTO {

	@ApiModelProperty(name = "status", value = "S:成功")
    private String status;
	
	@ApiModelProperty(name = "orderNo", value = "NFC订单号")
	private String orderNo;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
 
	
	
}
