package com.hisun.lemon.pwm.dto;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 充值请求 传输对象
 * 
 * @author ruan
 * @date 2017年7月14日
 * @time 上午9:27:30
 *
 */
@ApiModel("海币充值请求")
public class RechargeHCouponDTO {
	/**
	 * @Fields orderCcy 币种
	 */
	@ApiModelProperty(name = "orderCcy", value = "币种")
	private String orderCcy;

	/**
	 * @Fields userId 内部用户号
	 */
	@NotEmpty(message="PWM10023")
	private String userId;
	/**
	 * 充值金额
	 */
	@ApiModelProperty(name = "orderAmt", value = "充值金额")
	@NotNull(message = "PWM10024")
	@Min(value = 0, message = "PWM10024")
	private BigDecimal orderAmt;

	/**
	 * @Fields txType 交易类型
	 */
	@ApiModelProperty(name = "txType", value = "交易类型")
	@NotEmpty(message = "TAM10002")
	private String txType;
	/**
	 * @Fields busType 业务类型 0501 充海币
	 */
	@ApiModelProperty(name = "busType", value = "业务类型   0501 充海币")
	@NotEmpty(message = "TAM10003")
	private String busType;

	public String getTxType() {
		return txType;
	}

	public void setTxType(String txType) {
		this.txType = txType;
	}

	public String getBusType() {
		return busType;
	}

	public void setBusType(String busType) {
		this.busType = busType;
	}

	public String getOrderCcy() {
		return orderCcy;
	}

	public void setOrderCcy(String orderCcy) {
		this.orderCcy = orderCcy;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public BigDecimal getOrderAmt() {
		return orderAmt;
	}

	public void setOrderAmt(BigDecimal orderAmt) {
		this.orderAmt = orderAmt;
	}

}
