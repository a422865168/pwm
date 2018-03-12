package com.hisun.lemon.pwm.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 查询提现银行卡返回参数 传输对象
 * @author leon
 * @date 2017年7月17日
 * @time 上午9:29:32
 *
 */
@ApiModel("查询提现银行卡返回参数")
public class WithdrawCardQueryDTO {
    //卡ID
    @ApiModelProperty(name = "cardId", value = "卡ID")
    private String cardId;
	//加密银行卡号
	@ApiModelProperty(name = "cardNo", value = "加密银行卡号")
    private String cardNo;
	//支行名称
	@ApiModelProperty(name = "branchName", value = "支行名称")
    private String branchName;
    //资金机构
    @ApiModelProperty(name = "capCorg", value = "资金机构")
    private String capCorg;
    //银行卡后四位
    @ApiModelProperty(name = "cardNoLast", value = "银行卡后四位")
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

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCapCorg() {
        return capCorg;
    }

    public void setCapCorg(String capCorg) {
        this.capCorg = capCorg;
    }

    public String getCardNoLast() {
        return cardNoLast;
    }

    public void setCardNoLast(String cardNoLast) {
        this.cardNoLast = cardNoLast;
    }
}
