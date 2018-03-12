package com.hisun.lemon.pwm.dto;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import io.swagger.annotations.ApiModelProperty;

//import io.swagger.annotations.ApiModelProperty;


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
	@ApiModelProperty(name = "sysChannel", value = "订单来源渠道(WEB:web站点|APP:APP手机|HALL:营业厅|OTHER:其他渠道)")
    @NotEmpty(message="PWM10002")
	@Pattern(regexp="WEB|APP|HALL|OTHER",message="PWM10002")
	@Length(max = 5)
    private String sysChannel;
	/**
	 * 对公对私标志
	 */
	@ApiModelProperty(name = "psnFlag", value = "对公对私标志(0:个人|1:商户)")
    @NotEmpty(message="PWM10003")
	@Length(max = 1)
    private String psnFlag;

	/**
	 * 业务类型
	 */
	@ApiModelProperty(name = "busType", value = "业务类型(充值[0101:快捷|0102:线下|0103:营业厅|0104:企业网银]|0105:个人网银] 提现[0401:个人|0402:商户])")
    @NotEmpty(message="PWM10004")
	@Length(max =4)
    private String busType;

	/**
	 * 充值手续费
	 */
	@ApiModelProperty(name = "fee", value = " 充值手续费")
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
}
