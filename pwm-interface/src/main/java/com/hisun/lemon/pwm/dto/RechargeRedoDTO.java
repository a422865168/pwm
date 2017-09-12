package com.hisun.lemon.pwm.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * 充值补单传输DTO
 */
public class RechargeRedoDTO {

    @ApiModelProperty(name = "orderNo", value = "充值订单号",required = true)
    @NotEmpty
    @Length(max = 28)
    private  String orderNo;


    @ApiModelProperty(name = "对账子类型",value = "chkSubType",required = true)
    private String chkSubType;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getChkSubType() {
        return chkSubType;
    }

    public void setChkSubType(String chkSubType) {
        this.chkSubType = chkSubType;
    }
}
