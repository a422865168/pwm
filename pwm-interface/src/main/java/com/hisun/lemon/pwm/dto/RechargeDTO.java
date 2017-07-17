package com.hisun.lemon.pwm.dto;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import java.math.BigDecimal;


/**
 * 充值请求 传输对象
 * @author tone
 * @date 2017年7月14日
 * @time 上午9:27:30
 *
 */
public class RechargeDTO {
	/**
	 * 充值金额
	 */
	@Min(value=0, message="PWM10001")
    private BigDecimal amount;
	/**
	 * 订单来源渠道
	 */
    @NotEmpty(message="PWM10002")
    private String sysChannel;
	/**
	 * 对公对私标志
	 */
    @NotEmpty(message="PWM10003")
    private String psnFlag;

	/**
	 * 业务类型
	 */
    @NotEmpty(message="PWM10004")
    private String busType;
	/**
	 * 收款方id
	 */
	private String payeeId;

	/**
	 * 付款方id
	 */
	private String payerId;

	public BigDecimal getAmount() {
		return amount;
	}

	public String getSysChannel() {
		return sysChannel;
	}

	public String getPsnFlag() {
		return psnFlag;
	}

	public String getBusType() {
		return busType;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setSysChannel(String sysChannel) {
		this.sysChannel = sysChannel;
	}

	public void setPsnFlag(String psnFlag) {
		this.psnFlag = psnFlag;
	}

	public void setBusType(String busType) {
		this.busType = busType;
	}

	public String getPayeeId() {
		return payeeId;
	}

	public String getPayerId() {
		return payerId;
	}

	public void setPayerId(String payerId) {
		this.payerId = payerId;
	}

	public void setPayeeId(String payeeId) {
		this.payeeId = payeeId;
	}
}
