package com.hisun.lemon.pwm.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import java.math.BigDecimal;

/**
 * Created by Xian on 2017/8/9.
 */
@ApiModel("线下汇款充值 上传汇款凭证传输对象")
public class RemittanceUploadDTO {

    /**
     * 充值订单号
     */
    @ApiModelProperty(name = "orderNo", value = "充值订单号")
    @NotEmpty(message="PWM10007")
    @Length(max =24)
    private String orderNo;

    /**
     * 收银订单号
     */
    @ApiModelProperty(name = "cshOrderNo", value = "收银订单号")
    @Length(max =24)
    private String cashOrderNo;

    /**
     * 充值币种
     */
    @ApiModelProperty(name = "ccy", value = "币种")
    private String ccy;

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

    @ApiModelProperty(name = "cashRemittUrl", value = "汇款单图片url", required = true, dataType = "String")
    @NotEmpty(message="PWM10039")
    private String remittUrl;

    /**
     * 附言摘要
     */
    @ApiModelProperty(name = "remark", value = "附言摘要")
    @NotEmpty(message="PWM10003")
    private String remark;


    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCashOrderNo() {
        return cashOrderNo;
    }

    public void setCashOrderNo(String cashOrderNo) {
        this.cashOrderNo = cashOrderNo;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getRemittUrl() {
        return remittUrl;
    }

    public void setRemittUrl(String remittUrl) {
        this.remittUrl = remittUrl;
    }
}
