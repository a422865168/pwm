package com.hisun.lemon.pwm.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern; 
import org.hibernate.validator.constraints.NotEmpty; 
import com.hisun.lemon.framework.data.GenericDTO;
 

/**
 * 充值结果通知 传输对象
 * @author tone
 * @date 2017年6月14日
 * @time 上午9:27:30
 *
 */
public class RechangeResultDTO extends GenericDTO{ 
    @NotEmpty(message="PWM10001")
    private String orderNo;
    @NotEmpty(message="PWM10002")
    private String orderCcy;
    @Min(value=0, message="PWM10003")
    private Double amount; 
    private String mobileNo;
    @NotEmpty(message="PWM10004")
    private String rutCorgNo;
    @NotEmpty(message="PWM10005")
    private String capCorgNo;
    @NotEmpty(message="PWM10006")
    private String cropBusType;
    @NotEmpty(message="PWM10007")
    private String cropBusSubType;
    @NotEmpty(message="PWM10008")
    @Pattern(regexp="0|1|2|3",message="PWM10008")
    private String capCardType;
    @NotEmpty(message="PWM10009")
    private String cardNoHide;
    @NotEmpty(message="PWM10010")
    private String cardNoLast;
    @NotEmpty(message="PWM10011")
    private String cardUserNameHide;
    @NotEmpty(message="PWM10012")
    @Pattern(regexp="0|1|2|3|4|5",message="PWM10012")
    private String idType;
    @NotEmpty(message="PWM10013")
    private String idNoHide;
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getOrderCcy() {
		return orderCcy;
	}
	public void setOrderCcy(String orderCcy) {
		this.orderCcy = orderCcy;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getRutCorgNo() {
		return rutCorgNo;
	}
	public void setRutCorgNo(String rutCorgNo) {
		this.rutCorgNo = rutCorgNo;
	}
	public String getCapCorgNo() {
		return capCorgNo;
	}
	public void setCapCorgNo(String capCorgNo) {
		this.capCorgNo = capCorgNo;
	}
	public String getCropBusType() {
		return cropBusType;
	}
	public void setCropBusType(String cropBusType) {
		this.cropBusType = cropBusType;
	}
	public String getCropBusSubType() {
		return cropBusSubType;
	}
	public void setCropBusSubType(String cropBusSubType) {
		this.cropBusSubType = cropBusSubType;
	}
	public String getCapCardType() {
		return capCardType;
	}
	public void setCapCardType(String capCardType) {
		this.capCardType = capCardType;
	}
	public String getCardNoHide() {
		return cardNoHide;
	}
	public void setCardNoHide(String cardNoHide) {
		this.cardNoHide = cardNoHide;
	}
	public String getCardNoLast() {
		return cardNoLast;
	}
	public void setCardNoLast(String cardNoLast) {
		this.cardNoLast = cardNoLast;
	}
	public String getCardUserNameHide() {
		return cardUserNameHide;
	}
	public void setCardUserNameHide(String cardUserNameHide) {
		this.cardUserNameHide = cardUserNameHide;
	}
	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}
	public String getIdNoHide() {
		return idNoHide;
	}
	public void setIdNoHide(String idNoHide) {
		this.idNoHide = idNoHide;
	}  
}
