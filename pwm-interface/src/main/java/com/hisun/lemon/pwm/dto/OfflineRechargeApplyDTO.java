package com.hisun.lemon.pwm.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;


/**
 * 线下汇款充值申请 传输对象
 * @author xian
 * @date 2017年8月8日
 * @time 上午9:27:30
 *
 */
@ApiModel("线下汇款充值申请传输对象")
public class OfflineRechargeApplyDTO {

	/**
	 * 充值金额
	 */
	@ApiModelProperty(name = "amount", value = "充值金额")
	@Min(value=0, message="PWM10012")
	private BigDecimal amount;

	/**
	 * 充值用户或商户id
	 */
	@ApiModelProperty(name = "payerId", value = "充值用户或商户id")
	@NotEmpty(message="PWM10013")
	@Length(max = 16)
	private String payerId;


	@ApiModelProperty(name = "ccy", value = "充值币种")
    @NotEmpty(message="PWM10017")
	@Length(max = 5)
	private String ccy;

	/**
	 * 对公对私标志
	 */
	@ApiModelProperty(name = "psnFlag", value = "对公私标志(0:个人 | 1:商户)")
	@NotEmpty(message="PWM10003")
	@Length(max = 1)
	private String psnFlag;

	@ApiModelProperty(name = "crdCorpOrg", value = "资金机构", dataType = "String")
	private String crdCorpOrg;


	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getPayerId() {
		return payerId;
	}

	public void setPayerId(String payerId) {
		this.payerId = payerId;
	}

	public String getCcy() {
		return ccy;
	}

	public void setCcy(String ccy) {
		this.ccy = ccy;
	}

	public String getPsnFlag() {
		return psnFlag;
	}

	public void setPsnFlag(String psnFlag) {
		this.psnFlag = psnFlag;
	}

	public String getCrdCorpOrg() {
		return crdCorpOrg;
	}

	public void setCrdCorpOrg(String crdCorpOrg) {
		this.crdCorpOrg = crdCorpOrg;
	}
}
