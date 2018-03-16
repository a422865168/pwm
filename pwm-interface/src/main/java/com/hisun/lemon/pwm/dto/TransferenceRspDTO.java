package com.hisun.lemon.pwm.dto;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;



/**
 * 圈存传输对象
 * @author ruan
 * @date 2018年3月13日
 * @time 上午9:27:30
 *
 */
public class TransferenceRspDTO {
	@ApiModelProperty(name = "userId", value = "内部用户号")
	private String  userId;

	@ApiModelProperty(name = "rechargeAmount", value = "金额")
    private BigDecimal rechargeAmount;
	
	@ApiModelProperty(name = "orderNo", value = "NFC订单号")
	private String orderNo;
	@ApiModelProperty(name = "orderNo", value = "收银订单号")
	private String busOrderNo;

	@ApiModelProperty(name = "busType", value = "业务类型(充值[0101:快捷|0102:线下|0103:营业厅|0104:企业网银]|0105:个人网银|0106：圈存] 提现[0401:个人|0402:商户])")
    private String busType;
	
	@ApiModelProperty(name = "orderStatus", value = "W:交易受理成功  S:交易成功   F:交易失败  T：交易逾期")
	private String orderStatus;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public BigDecimal getRechargeAmount() {
		return rechargeAmount;
	}

	public void setRechargeAmount(BigDecimal rechargeAmount) {
		this.rechargeAmount = rechargeAmount;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getBusType() {
		return busType;
	}

	public void setBusType(String busType) {
		this.busType = busType;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getBusOrderNo() {
		return busOrderNo;
	}

	public void setBusOrderNo(String busOrderNo) {
		this.busOrderNo = busOrderNo;
	}
	
}
