package com.hisun.lemon.pwm.dto;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
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
public class RechargeReqHCouponDTO {
	/**
	 * @Fields orderCcy 币种
	 */
	@ApiModelProperty(name = "orderCcy", value = "币种(USD:美元)")
	@Length(max =4)
	private String orderCcy;
	/**
	 * 充值金额
	 */
	@ApiModelProperty(name = "hCouponAmt", value = "海币充值数量")
	@NotNull(message = "PWM10024")
	@Min(value = 0, message = "PWM10024")
	private BigDecimal hCouponAmt;

	/**
	 * @Fields txType 交易类型
	 */
	@ApiModelProperty(name = "txType", value = "交易类型 05")
	@NotEmpty(message = "TAM10002")
	@Length(max =2)
	private String txType;
	/**
	 * @Fields busType 业务类型 0501 充海币
	 */
	@ApiModelProperty(name = "busType", value = "业务类型   0501 充海币")
	@NotEmpty(message = "TAM10003")
	@Length(max =4)
	private String busType;
	
	
	/**
	 * @Fields busType 业务类型 0501 充海币
	 */
	@ApiModelProperty(name = "mblNo", value = "手机号")
	@NotEmpty(message = "PWM10049")
	private String mblNo;
	
	@ApiModelProperty(name = "payPassword", value = "支付密码")
	@NotEmpty(message = "PWM10049")
	private String payPassword;

	
	public String getPayPassword() {
		return payPassword;
	}

	public void setPayPassword(String payPassword) {
		this.payPassword = payPassword;
	}

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

	public String getMblNo() {
		return mblNo;
	}

	public void setMblNo(String mblNo) {
		this.mblNo = mblNo;
	}

	public BigDecimal gethCouponAmt() {
		return hCouponAmt;
	}

	public void sethCouponAmt(BigDecimal hCouponAmt) {
		this.hCouponAmt = hCouponAmt;
	}
	
	
}