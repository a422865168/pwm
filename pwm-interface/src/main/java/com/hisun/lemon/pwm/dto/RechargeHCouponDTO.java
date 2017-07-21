package com.hisun.lemon.pwm.dto;

import org.hibernate.validator.constraints.NotEmpty;

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
public class RechargeHCouponDTO {
	/**
	 * @Fields orderCcy 币种
	 */
	@NotEmpty(message = "PWM10020")
	private String orderCcy;

	/**
	 * @Fields userId 内部用户号
	 */
	@Pattern(regexp = "S|F", message = "PWM10023")
	private String userId;
	/**
	 * 充值金额
	 */
	@Min(value = 0, message = "PWM10024")
	private BigDecimal orderAmt;
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
