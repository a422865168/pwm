package com.hisun.lemon.pwm.dto;

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
@ApiModel("营业厅充值申请")
public class HallRechargeApplyDTO {

	/**
	 * 营业厅ID
	 */
	@ApiModelProperty(name = "merchantId", value = "营业厅ID")
	@NotEmpty(message="PWM10010")
	private String merchantId;

	private BussinessBody body;

	/**
	 * 签名
	 */
	private String sign;

	public class BussinessBody{
		/**
		 * 营业厅充值订单号
		 */
		@NotBlank(message="PWM10011")
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
		@NotEmpty(message="PWM10013")
		private String payerId;

		/**
		 * 状态:<br/>
		 * A:申请<br/>
		 * O:确认<br/>
		 * C:取消<br/>
		 */
		@NotEmpty(message="PWM10014")
		@Pattern(regexp="A|O|C",message="PWM10015")
		private String status;

		@Min(value=0, message="PWM10016")
		private Double fee;

        @NotNull(message="PWM10017")
		private String ccy;

		/**
		 * 对公对私标志
		 */
		@NotEmpty(message="PWM10003")
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
