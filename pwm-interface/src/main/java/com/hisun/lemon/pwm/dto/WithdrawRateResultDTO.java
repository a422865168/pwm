package com.hisun.lemon.pwm.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;


/**
 * 交易费率查询响应传输对象
 * @author leon
 * @date 2017年8月9日
 * @time 上午19:27:30
 *
 */
@ApiModel("交易费率查询响应传输对象")
public class WithdrawRateResultDTO {
	/** 计费方式 */
	@ApiModelProperty(name = "caculateType", value = "计费方式")
	private String caculateType;
	/** 费率 */
	@ApiModelProperty(name = "rate", value = "费率")
	private BigDecimal rate;
	/** 固定手续费 */
	@ApiModelProperty(name = "fixFee", value = "固定手续费")
	private BigDecimal fixFee;

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public String getCaculateType() {
		return caculateType;
	}

	public void setCaculateType(String caculateType) {
		this.caculateType = caculateType;
	}

	public BigDecimal getFixFee() {
		return fixFee;
	}

	public void setFixFee(BigDecimal fixFee) {
		this.fixFee = fixFee;
	}
}
