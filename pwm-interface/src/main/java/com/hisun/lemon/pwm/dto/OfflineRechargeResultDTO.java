package com.hisun.lemon.pwm.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalTime;


/**
 * 线下汇款充值申请响应 传输对象
 * @author xian
 * @date 2017年8月8日
 * @time 上午9:27:30
 *
 */
@ApiModel("线下汇款充值申请 响应传输对象")
public class OfflineRechargeResultDTO {

	/**
	 * 交易订单号
	 */
	@ApiModelProperty(name = "cshOrderNo", value = "交易订单号")
	@Length(max =24)
	private String orderNo;

	/**
	 * 收银订单号
	 */
	@ApiModelProperty(name = "cshOrderNo", value = "收银订单号")
	@Length(max =24)
	private String cashierOrderNo;

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
	private String payerId;

	/**
	 * 状态:<br/>
	 * 订单状态
	 */
	@ApiModelProperty(name = "status", value = "汇款充值订单状态(W:待审核处理|F:交易失败|S:交易成功|W1:审核已提交)")
	@Length(max = 1)
	private String status;


	@ApiModelProperty(name = "ccy", value = "充值币种")
	@Length(max = 5)
	private String ccy;

	/**
	 * 交易时间
	 */
	@ApiModelProperty(name = "orderTm", value = "订单创建时间", dataType = "String")
	private LocalTime orderTm;

	/**
	 * 备注：运营审核结果信息
	 */
	@ApiModelProperty(name = "orderTm", value = "运营审核结果信息", dataType = "String")
	private String remark;

	/**
	 * 银行账号
	 */
	@ApiModelProperty(name = "crdNo", value = "银行账号", required = true, dataType = "String")
	private String crdNo;

	/**
	 * 户名
	 */
	@ApiModelProperty(name = "crdUsrNm", value = "银行卡户名", required = true, dataType = "String")
	private String crdUsrNm;

	/**
	 * mblNo 手机号
	 */
	@ApiModelProperty(name = "mblNo", value = "用户手机号")
	private String mblNo;


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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCcy() {
		return ccy;
	}

	public void setCcy(String ccy) {
		this.ccy = ccy;
	}

	public LocalTime getOrderTm() {
		return orderTm;
	}

	public void setOrderTm(LocalTime orderTm) {
		this.orderTm = orderTm;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getCashierOrderNo() {
		return cashierOrderNo;
	}

	public void setCashierOrderNo(String cashierOrderNo) {
		this.cashierOrderNo = cashierOrderNo;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCrdNo() {
		return crdNo;
	}

	public void setCrdNo(String crdNo) {
		this.crdNo = crdNo;
	}

	public String getCrdUsrNm() {
		return crdUsrNm;
	}

	public void setCrdUsrNm(String crdUsrNm) {
		this.crdUsrNm = crdUsrNm;
	}

	public String getMblNo() {
		return mblNo;
	}

	public void setMblNo(String mblNo) {
		this.mblNo = mblNo;
	}
}
