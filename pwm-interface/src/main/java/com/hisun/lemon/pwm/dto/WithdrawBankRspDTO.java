package com.hisun.lemon.pwm.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * 提现银行列表返回参数 传输对象
 * @author leon
 * @date 2017年7月17日
 * @time 上午9:29:32
 *
 */
@ApiModel("提现银行列表返回参数")
public class WithdrawBankRspDTO {

	/**
	 * @Fields cardBin 卡bin
	 */
	@ApiModelProperty(name = "cardBin", value = "卡bin")
	private String cardBin;
	/**
	 * @Fields capCorg 资金机构
	 */
	@ApiModelProperty(name = "capCorg", value = "资金机构")
	private String capCorg;
	/**
	 * @Fields bankName 提现银行
	 */
	@ApiModelProperty(name = "bankName", value = "资金机构")
	private String bankName;
	/**
	 * @Fields cardAcType 卡类型，D借记卡，C贷记卡
	 */
	@ApiModelProperty(name = "cardAcType", value = "卡类型：D借记卡，C贷记卡")
	private String cardAcType;
	/**
	 * @Fields cardLength 卡长度
	 */
	@ApiModelProperty(name = "cardLength", value = "卡长度")
	private Integer cardLength;
	/**
	 * @Fields oprId 操作员
	 */
	@ApiModelProperty(name = "oprId", value = "操作员")
	private String oprId;

	public String getCardBin() {
		return cardBin;
	}

	public void setCardBin(String cardBin) {
		this.cardBin = cardBin;
	}

	public String getCapCorg() {
		return capCorg;
	}

	public void setCapCorg(String capCorg) {
		this.capCorg = capCorg;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getCardAcType() {
		return cardAcType;
	}

	public void setCardAcType(String cardAcType) {
		this.cardAcType = cardAcType;
	}

	public Integer getCardLength() {
		return cardLength;
	}

	public void setCardLength(Integer cardLength) {
		this.cardLength = cardLength;
	}

	public String getOprId() {
		return oprId;
	}

	public void setOprId(String oprId) {
		this.oprId = oprId;
	}
}
