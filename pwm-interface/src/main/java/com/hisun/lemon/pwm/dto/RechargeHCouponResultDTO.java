package com.hisun.lemon.pwm.dto;

import org.hibernate.validator.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

/**
 * 充值请求 传输对象
 * 
 * @author ruan
 * @date 2017年7月14日
 * @time 上午9:27:30
 *
 */
@ApiModel("海币充值结果")
public class RechargeHCouponResultDTO {
	/**
	 * @Fields userId 内部用户号
	 */
	@Pattern(regexp = "S|F", message = "PWM10023")
	private String userId;
	/**
	 * 充值金额
	 */
	@ApiModelProperty(name = "orderAmt", value = "充值金额")
	@Min(value = 0, message = "PWM10024")
	private BigDecimal orderAmt;
	/**
	 * 订单状态
	 */
	@NotEmpty(message = "PWM10022")
	@Pattern(regexp = "S|F", message = "PWM10025")
	private String orderStatus;

	/**
	 * @Fields orderCcy 币种
	 */
	@ApiModelProperty(name = "orderCcy", value = "币种")
	@NotEmpty(message = "PWM10020")
	private String orderCcy;

	/**
	 * @Fields orderNo 海币充值订单编号
	 */
	@NotEmpty(message = "PWM10021")
	private String orderNo;
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
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

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getOrderCcy() {
		return orderCcy;
	}

	public void setOrderCcy(String orderCcy) {
		this.orderCcy = orderCcy;
	}

}
