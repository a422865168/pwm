package com.hisun.lemon.pwm.dto;

import java.time.LocalDate;

import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotEmpty;

import com.hisun.lemon.framework.data.GenericDTO;
 

/**
 * 提现结果通知 传输对象
 * @author tone
 * @date 2017年6月14日
 * @time 上午9:27:30
 *
 */
public class WithdrawResultDTO extends GenericDTO{ 
    @NotEmpty(message="PWM17001")
    private String orderNo;
    
    @Min(value=0, message="PWM17002")
    private Double actAmount;
    @Min(value=0, message="PWM17003")
    private Double feeAmount; 
    @NotEmpty(message="PWM17004")
    private String rspOrderNo;
    @NotEmpty(message="PWM17005")
    private String rspOrderSuccTm;
    @NotEmpty(message="PWM17006")
    private LocalDate accTm; 
    @NotEmpty(message="PWM17007")
    private String orderSataus;
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public Double getActAmount() {
		return actAmount;
	}
	public void setActAmount(Double actAmount) {
		this.actAmount = actAmount;
	}
	public Double getFeeAmount() {
		return feeAmount;
	}
	public void setFeeAmount(Double feeAmount) {
		this.feeAmount = feeAmount;
	}
	public String getRspOrderNo() {
		return rspOrderNo;
	}
	public void setRspOrderNo(String rspOrderNo) {
		this.rspOrderNo = rspOrderNo;
	}
	public String getRspOrderSuccTm() {
		return rspOrderSuccTm;
	}
	public void setRspOrderSuccTm(String rspOrderSuccTm) {
		this.rspOrderSuccTm = rspOrderSuccTm;
	}
	public LocalDate getAccTm() {
		return accTm;
	}
	public void setAccTm(LocalDate accTm) {
		this.accTm = accTm;
	}
	public String getOrderSataus() {
		return orderSataus;
	}
	public void setOrderSataus(String orderSataus) {
		this.orderSataus = orderSataus;
	} 
}
