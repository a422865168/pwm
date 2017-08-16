package com.hisun.lemon.pwm.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;


/**
 * 添加提现银行卡请求参数 传输对象
 * @author leon
 * @date 2017年7月17日
 * @time 上午9:29:32
 *
 */
@ApiModel("添加提现银行卡请求参数")
public class WithdrawCardBindDTO {

	//银行卡号
	@ApiModelProperty(name = "cardNo", value = "银行卡号")
    @NotEmpty(message = "PWM10043")
    @Length(max = 24)
    private String cardNo;
	//支行名称
	@ApiModelProperty(name = "branchName", value = "支行名称")
    @NotEmpty(message = "PWM10044")
    @Length(max = 24)
    private String branchName;
    //卡bin
    @ApiModelProperty(name = "cardBin", value = "卡bin")
    @NotEmpty(message = "PWM10045")
    @Length(max = 12)
    private String cardBin;
    //用户编号
    @ApiModelProperty(hidden = true)
	private String userId;
    //银行卡后四位
    @ApiModelProperty(hidden = true)
    private String cardNoLast;

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getCardBin() {
        return cardBin;
    }

    public void setCardBin(String cardBin) {
        this.cardBin = cardBin;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCardNoLast() {
        return cardNoLast;
    }

    public void setCardNoLast(String cardNoLast) {
        this.cardNoLast = cardNoLast;
    }
}
