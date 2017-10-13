package com.hisun.lemon.pwm.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
/**
 * Created by Xian on 2017/8/9.
 */
@ApiModel("线下汇款充值 上传汇款凭证传输对象")
public class RemittanceUploadDTO {

    /**
     * 收银订单号
     */
    @ApiModelProperty(name = "orderNo", value = "收银订单号")
    @NotEmpty(message="PWM10007")
    @Length(max =28)
    private String orderNo;

    /**
     * 充值用户或商户id
     */
    @ApiModelProperty(name = "payerId", value = "充值用户或商户id",required = true, dataType = "String")
    @NotEmpty(message="PWM10013")
    @Length(max = 16)
    private String payerId;

    @ApiModelProperty(name = "cashRemittUrl", value = "汇款单图片url", required = true, dataType = "String")
    @NotEmpty(message="PWM10039")
    @Length(max = 256)
    private String remittUrl;

    /**
     * 附言摘要
     */
    @ApiModelProperty(name = "remark", value = "附言摘要")
    @NotEmpty(message="PWM10051")
    @Length(max = 256)
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

    public String getRemittUrl() {
        return remittUrl;
    }

    public void setRemittUrl(String remittUrl) {
        this.remittUrl = remittUrl;
    }

}
