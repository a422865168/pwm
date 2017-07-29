package com.hisun.lemon.pwm.dto;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;


/**
 * 充值请求 传输对象
 * @author tone
 * @date 2017年7月14日
 * @time 上午9:27:30
 *
 */
@ApiModel("充值请请求")
public class RechargeDTO {
	/**
	 * 充值金额
	 */
	@ApiModelProperty(name = "amount", value = "充值金额")
	@Min(value=0, message="PWM10001")
    private BigDecimal amount;
	/**
	 * 订单来源渠道
	 * WEB:WEB站点 <br/>
	 * APP:手机APP<br/>
	 * HALL:营业厅<br/>
	 * OTHER:其他渠道<br/>
	 */
	@ApiModelProperty(name = "sysChannel", value = "订单来源渠道")
    @NotEmpty(message="PWM10002")
	@Pattern(regexp="WEB|APP|HALL|OTHER",message="PWM10002")
    private String sysChannel;
	/**
	 * 对公对私标志
	 */
    @NotEmpty(message="PWM10003")
    private String psnFlag;

	/**
	 * 业务类型
	 */
	@ApiModelProperty(name = "busType", value = "业务类型")
    @NotEmpty(message="PWM10004")
	@Length(max =4)
    private String busType;
	/**
	 * 收款方id
	 */
	@ApiModelProperty(name = "payeeId", value = " 收款方id")
	@Length(max=20)
	private String payeeId;

	/**
	 * 付款方id
	 */
	@ApiModelProperty(name = "payerId", value = " 付款方id")
	@Length(max=20)
	private String payerId;

	/**
	 * 充值手续费
	 */
	@Min(value=0, message="PWM10016")
	private BigDecimal fee;

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
