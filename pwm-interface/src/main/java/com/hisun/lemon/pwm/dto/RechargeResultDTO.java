package com.hisun.lemon.pwm.dto;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * 充值结果通知 传输对象
 * @author tone
 * @date 2017年6月14日
 * @time 上午9:27:30
 *
 */
@ApiModel("充值结果通知 传输对象")
public class RechargeResultDTO {
	/**
	 * 充提订单号
	 */
	@ApiModelProperty(name = "orderNo", value = "充提订单号")
    @NotEmpty(message="PWM10005")
    private String orderNo;

	/**
	 * 币种
	 */
	@ApiModelProperty(name = "orderCcy", value = "币种")
    private String orderCcy;
	/**
	 * 订单金额
	 */
	@ApiModelProperty(name = "amount", value = "订单金额")
    @Min(value=0, message="PWM10006")
    private BigDecimal amount;

	/**
	 * 收银订单号
	 */
	@ApiModelProperty(name = "extOrderNo", value = "收银订单号")
	@NotEmpty(message="PWM10007")
	private String extOrderNo;
	/**
	 * 订单状态
	 */
	@ApiModelProperty(name = "status", value = "订单状态")
	@NotEmpty(message="PWM10008")
	@Pattern(regexp="S|F",message="PWM10009")
	private String status;

	public String getOrderNo() {
		return orderNo;
	}

	public String getOrderCcy() {
		return orderCcy;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getExtOrderNo() {
		return extOrderNo;
	}

	public String getStatus() {
		return status;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public void setOrderCcy(String orderCcy) {
		this.orderCcy = orderCcy;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setExtOrderNo(String extOrderNo) {
		this.extOrderNo = extOrderNo;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
