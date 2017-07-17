package com.hisun.lemon.pwm.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotEmpty;

import com.hisun.lemon.framework.data.GenericDTO;
 

/**
 * 提现结果通知 传输对象
 * @author leon
 * @date 2017年7月17日
 * @time 上午9:29:32
 *
 */
public class WithdrawResultDTO {

	//用户编号
    @NotEmpty(message="PWM17007")
    private String userId;
	//提现银行卡号
	@NotEmpty(message="PWM17001")
    private String capCardNo;
	//申请提现金额
	@Min(value=0, message="PWM17002")
	private BigDecimal wcApplyAmt;
	//提现手续费
	@Min(value=0, message="PWM17002")
	private BigDecimal feeAmt;
	//币种
	@NotEmpty(message="PWM17001")
	private String orderCcy;
	//提现类型
	@NotEmpty(message="PWM17001")
	private String wcType;
	//付款加急标识
	@NotEmpty(message="PWM17001")
	private String payUrgeFlg;
	//资金合作机构
	@NotEmpty(message="PWM17001")
	private String capCorgNo;
	//支付密码
	@NotEmpty(message="PWM17001")
	private String payPassWord;
	//订单渠道
	@NotEmpty(message="PWM17001")
	private String busCnl;
	//通知手机号
	@NotEmpty(message="PWM17001")
	private String ntfMbl;
	//备注
	private String wcRemark;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCapCardNo() {
		return capCardNo;
	}

	public void setCapCardNo(String capCardNo) {
		this.capCardNo = capCardNo;
	}

	public BigDecimal getWcApplyAmt() {
		return wcApplyAmt;
	}

	public void setWcApplyAmt(BigDecimal wcApplyAmt) {
		this.wcApplyAmt = wcApplyAmt;
	}

	public BigDecimal getFeeAmt() {
		return feeAmt;
	}

	public void setFeeAmt(BigDecimal feeAmt) {
		this.feeAmt = feeAmt;
	}

	public String getOrderCcy() {
		return orderCcy;
	}

	public void setOrderCcy(String orderCcy) {
		this.orderCcy = orderCcy;
	}

	public String getWcType() {
		return wcType;
	}

	public void setWcType(String wcType) {
		this.wcType = wcType;
	}

	public String getPayUrgeFlg() {
		return payUrgeFlg;
	}

	public void setPayUrgeFlg(String payUrgeFlg) {
		this.payUrgeFlg = payUrgeFlg;
	}

	public String getCapCorgNo() {
		return capCorgNo;
	}

	public void setCapCorgNo(String capCorgNo) {
		this.capCorgNo = capCorgNo;
	}

	public String getPayPassWord() {
		return payPassWord;
	}

	public void setPayPassWord(String payPassWord) {
		this.payPassWord = payPassWord;
	}

	public String getBusCnl() {
		return busCnl;
	}

	public void setBusCnl(String busCnl) {
		this.busCnl = busCnl;
	}

	public String getNtfMbl() {
		return ntfMbl;
	}

	public void setNtfMbl(String ntfMbl) {
		this.ntfMbl = ntfMbl;
	}

	public String getWcRemark() {
		return wcRemark;
	}

	public void setWcRemark(String wcRemark) {
		this.wcRemark = wcRemark;
	}
}
