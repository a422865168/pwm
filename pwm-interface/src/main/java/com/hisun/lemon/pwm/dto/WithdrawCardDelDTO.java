package com.hisun.lemon.pwm.dto;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 删除提现银行卡请求参数 传输对象
 * @author leon
 * @date 2017年7月17日
 * @time 上午9:29:32
 *
 */
@ApiModel("删除提现银行卡请求参数")
public class WithdrawCardDelDTO {

	//卡ID
	@ApiModelProperty(name = "cardId", value = "提现银行卡ID")
    @NotEmpty(message = "PWM10046")
    @Length(max = 24)
    private String cardId;

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }
}
