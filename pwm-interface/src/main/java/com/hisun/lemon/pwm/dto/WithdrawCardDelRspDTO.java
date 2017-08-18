package com.hisun.lemon.pwm.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;


/**
 * 删除提现银行卡返回参数 传输对象
 * @author leon
 * @date 2017年7月17日
 * @time 上午9:29:32
 *
 */
@ApiModel("删除提现银行卡返回参数")
public class WithdrawCardDelRspDTO {

	//卡ID
	@ApiModelProperty(name = "cardId", value = "提现银行卡ID")
    private String cardId;

    @ApiModelProperty(name = "cardStatus", value = "卡状态 '1'生效 '0'失效")
	private String cardStatus;

    @ApiModelProperty(name = "cardNoLast", value = "银行卡后四位")
    private String cardNoLast;

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(String cardStatus) {
        this.cardStatus = cardStatus;
    }

    public String getCardNoLast() {
        return cardNoLast;
    }

    public void setCardNoLast(String cardNoLast) {
        this.cardNoLast = cardNoLast;
    }
}
