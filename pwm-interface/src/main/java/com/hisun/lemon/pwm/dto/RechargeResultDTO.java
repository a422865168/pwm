package com.hisun.lemon.pwm.dto;

import java.math.BigDecimal;

import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 充值结果通知 传输对象
 * @author tone
 * @date 2017年6月14日
 * @time 上午9:27:30
 *
 */
@ApiModel("充值结果通知")
public class RechargeResultDTO {
	/**
	 * 充提订单号
	 */
	@ApiModelProperty(name = "orderNo", value = "充提订单号")
    @NotEmpty(message="PWM10005")
    @Length(max=204)
    private String orderNo;

	@ApiModelProperty(name = "busType", value = "业务类型")
	@NotEmpty(message="PWM10038")
	@Length(max =4)
	private String busType;

	@ApiModelProperty(name = "payerId", value = "收银订单付款用户Id")
	@Length(max =20)
	private String payerId;

	@ApiModelProperty(name = "txJrnNo", value = "收银订单流水")
	@Length(max =30)
	private String txJrnNo;

	/**
	 * 币种
	 */
	@ApiModelProperty(name = "orderCcy", value = "币种")
	@Length(max=4)
	private String orderCcy;
	/**
	 * 订单金额
	 */
	@ApiModelProperty(name = "amount", value = "订单金额")
    @Min(value=0, message="PWM10006")
    private BigDecimal amount;

	@ApiModelProperty(name = "fee", value = "充值手续费")
	private BigDecimal fee;

	@ApiModelProperty(name = "totalAmount", value = "收银订单金额")
	@Min(value=0, message="PWM10006")
	private BigDecimal totalAmount;

	/**
	 * 收银订单号
	 */
	@ApiModelProperty(name = "extOrderNo", value = "收银订单号")
	@NotEmpty(message="PWM10007")
	@Length(max=24)
	private String extOrderNo;
	/**
	 * 订单状态
	 */
	@ApiModelProperty(name = "status", value = "订单状态(S:成功|F:失败)")
	private String status;

	/**
	 * 充值备注信息
	 */
	@ApiModelProperty(name = "remark", value = "备注")
	private String remark;
	
	/**
	 * 支付方式
	 */
	@ApiModelProperty(name = "payTypes", value = "支付方式   0.余额支付   1.快捷支付")
	private String payTypes;


	public String getPayTypes() {
		return payTypes;
	}

	public void setPayTypes(String payTypes) {
		this.payTypes = payTypes;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public String getOrderCcy() {
		return orderCcy;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getExtOrderNo() {
		return extOrderNo;
	}

	public String getStatus() {
		return status;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public void setOrderCcy(String orderCcy) {
		this.orderCcy = orderCcy;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setExtOrderNo(String extOrderNo) {
		this.extOrderNo = extOrderNo;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBusType() {
		return busType;
	}

	public void setBusType(String busType) {
		this.busType = busType;
	}

	public String getPayerId() {
		return payerId;
	}

	public void setPayerId(String payerId) {
		this.payerId = payerId;
	}

	public String getTxJrnNo() {
		return txJrnNo;
	}

	public void setTxJrnNo(String txJrnNo) {
		this.txJrnNo = txJrnNo;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	
	
}
