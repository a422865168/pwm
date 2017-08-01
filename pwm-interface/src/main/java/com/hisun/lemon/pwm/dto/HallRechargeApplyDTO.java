package com.hisun.lemon.pwm.dto;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;


/**
 * 营业厅充值申请 传输对象
 * @author tone
 * @date 2017年7月17日
 * @time 上午9:27:30
 *
 */
@ApiModel("营业厅充值申请传输对象")
public class HallRechargeApplyDTO {

	/**
	 * 营业厅ID
	 */
	@ApiModelProperty(name = "merchantId", value = "营业厅ID")
	@NotEmpty(message="PWM10010")
	@Length(max = 16)
	private String merchantId;

	private BussinessBody body;

	/**
	 * 签名
	 */
	@ApiModelProperty(name = "sign", value = "业务报文的md5值",required = true)
	@Length(max = 32)
	private String sign;

	public class BussinessBody{
		/**
		 * 营业厅充值订单号
		 */
		@ApiModelProperty(name = "hallOrderNo", value = "营业厅充值订单号",required = true)
		@NotBlank(message="PWM10011")
		@Length(max = 20)
		private String hallOrderNo;

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
		@NotEmpty(message="PWM10013")
		@Length(max = 16)
		private String payerId;

		/**
		 * 状态:<br/>
		 * A:申请<br/>
		 * O:确认<br/>
		 * C:取消<br/>
		 */
		@ApiModelProperty(name = "status", value = "营业厅充值操作状态(A:申请|O:确认|C:取消)",required = true)
		@NotEmpty(message="PWM10014")
		@Pattern(regexp="A|O|C",message="PWM10015")
		@Length(max = 1)
		private String status;

		@ApiModelProperty(name = "fee", value = "营业厅充值操作手续费")
		@Min(value=0, message="PWM10016")
		private Double fee;

		@ApiModelProperty(name = "ccy", value = "营业厅充值币种")
        @NotNull(message="PWM10017")
		@Length(max = 5)
		private String ccy;

		/**
		 * 对公对私标志
		 */
		@ApiModelProperty(name = "psnFlag", value = "对公私标志")
		@NotEmpty(message="PWM10003")
		@Length(max = 1)
		private String psnFlag;

		public String getStatus() {
			return status;
		}

		public BigDecimal getAmount() {
			return amount;
		}

		public String getPayerId() {
			return payerId;
		}

		public String getHallOrderNo() {
			return hallOrderNo;
		}

		public void setHallOrderNo(String hallOrderNo) {
			this.hallOrderNo = hallOrderNo;
		}

		public void setAmount(BigDecimal amount) {
			this.amount = amount;
		}

		public void setPayerId(String payerId) {
			this.payerId = payerId;
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

		public Double getFee() {
			return fee;
		}

		public void setFee(Double fee) {
			this.fee = fee;
		}

		public String getPsnFlag() {
			return psnFlag;
		}

		public void setPsnFlag(String psnFlag) {
			this.psnFlag = psnFlag;
		}
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getSign() {
		return sign;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getMerchantId() {
		return merchantId;
	}

	public BussinessBody getBody() {
		return body;
	}

	public void setBody(BussinessBody body) {
		this.body = body;
	}
}
