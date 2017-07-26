package com.hisun.lemon.pwm.dto;

import org.hibernate.validator.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;


/**
 * 营业厅充值查询 传输对象
 * @author tone
 * @date 2017年7月14日
 * @time 上午9:27:30
 *
 */
@ApiModel("转账到银行卡对象")
public class HallQueryDTO {
	/**
	 * 充值金额
	 */
	@ApiModelProperty(name = "amount", value = "充值金额")
    private BigDecimal amount;

	@NotEmpty(message = "PWM10018")
	private  String key;
	@ApiModelProperty(name = "type", value = "类型")
	@NotEmpty(message = "PWM10019")
	private String type;

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
