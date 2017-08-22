package com.hisun.lemon.pwm.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * 营业厅用户账户查询结果 传输对象
 * @author tone
 * @date 2017年7月21日
 * @time 上午9:27:30
 *
 */
@ApiModel("营业厅用户账户查询结果 传输对象")
public class HallQueryResultDTO {

	/**
	 * 查询关键字
	 */
	@ApiModelProperty(name = "key", value = "查询关键字")
    private String key;
	/**
	 * 用户或商户ID
	 */
	@ApiModelProperty(name = "umId", value = "用户或商户ID")
	private String umId;

	/**
	 * 用户或商户名称
	 */
	@ApiModelProperty(name = "umName", value = "用户或商户名称")
	private  String umName;

	/**
	 * 手续费
	 */
	@ApiModelProperty(name = "fee", value = "手续费")
	private BigDecimal fee;

	/**
	 * 用户账户余额/商户结算余额
	 */
	@ApiModelProperty(name = "acBalAmt", value = "账户余额")
	private BigDecimal acBalAmt;


	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getUmId() {
		return umId;
	}

	public void setUmId(String umId) {
		this.umId = umId;
	}

	public String getUmName() {
		return umName;
	}

	public void setUmName(String umName) {
		this.umName = umName;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public BigDecimal getAcBalAmt() {
		return acBalAmt;
	}

	public void setAcBalAmt(BigDecimal acBalAmt) {
		this.acBalAmt = acBalAmt;
	}
}
