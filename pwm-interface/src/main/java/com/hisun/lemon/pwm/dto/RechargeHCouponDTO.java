package com.hisun.lemon.pwm.dto;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 充值请求 传输对象
 * 
 * @author ruan
 * @date 2017年7月14日
 * @time 上午9:27:30
 *
 */
@ApiModel("海币充值请求")
public class RechargeHCouponDTO {
	/**
	 * @Fields orderCcy 币种
	 */
	@ApiModelProperty(name = "orderCcy", value = "币种(USD:美元)")
	@Length(max =4)
	private String orderCcy;
	/**
	 * 充值金额
	 */
	@ApiModelProperty(name = "hCouponAmt", value = "海币充值数量")
	@NotNull(message = "PWM10024")
	@Min(value = 0, message = "PWM10024")
	private BigDecimal hCouponAmt;

	/**
	 * @Fields txType 交易类型
	 */
	@ApiModelProperty(name = "txType", value = "交易类型 05")
	@NotEmpty(message = "TAM10002")
	@Length(max =2)
	private String txType;
	/**
	 * @Fields busType 业务类型 0501 充海币
	 */
	@ApiModelProperty(name = "busType", value = "业务类型   0501 充海币")
	@NotEmpty(message = "TAM10003")
	@Length(max =4)
	private String busType;
	
	/**
	 * 订单来源渠道
	 * WEB:WEB站点 <br/>
	 * APP:手机APP<br/>
	 * HALL:营业厅<br/>
	 * OTHER:其他渠道<br/>
	 */
	@ApiModelProperty(name = "sysChannel", value = "订单来源渠道(WEB:web站点|APP:APP手机|HALL:营业厅|OTHER:其他渠道)")
	@Length(max = 5)
    private String sysChannel;
	

	public String getTxType() {
		return txType;
	}

	public void setTxType(String txType) {
		this.txType = txType;
	}

	public String getBusType() {
		return busType;
	}

	public void setBusType(String busType) {
		this.busType = busType;
	}

	public String getOrderCcy() {
		return orderCcy;
	}

	public void setOrderCcy(String orderCcy) {
		this.orderCcy = orderCcy;
	}

	public BigDecimal gethCouponAmt() {
		return hCouponAmt;
	}

	public void sethCouponAmt(BigDecimal hCouponAmt) {
		this.hCouponAmt = hCouponAmt;
	}

	public String getSysChannel() {
		return sysChannel;
	}

	public void setSysChannel(String sysChannel) {
		this.sysChannel = sysChannel;
	}
	
}
