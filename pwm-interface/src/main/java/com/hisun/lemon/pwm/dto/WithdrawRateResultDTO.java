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
	/** 计费起始金额 */
	@ApiModelProperty(name = "calculateMinAmt", value = "计费起始金额")
	private BigDecimal calculateMinAmt;
    /** 最低收取费用 */
    @ApiModelProperty(name = "minFee", value = "最低收取费用")
    private BigDecimal minFee;
    /** 最低收取费用 */
    @ApiModelProperty(name = "maxFee", value = "最高收取费用")
    private BigDecimal maxFee;

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

    public BigDecimal getCalculateMinAmt() {
        return calculateMinAmt;
    }

    public void setCalculateMinAmt(BigDecimal calculateMinAmt) {
        this.calculateMinAmt = calculateMinAmt;
    }

    public BigDecimal getMinFee() {
        return minFee;
    }

    public void setMinFee(BigDecimal minFee) {
        this.minFee = minFee;
    }

    public BigDecimal getMaxFee() {
        return maxFee;
    }

    public void setMaxFee(BigDecimal maxFee) {
        this.maxFee = maxFee;
    }
}
