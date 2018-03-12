package com.hisun.lemon.pwm.dto;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by XianSky on 2017/9/7.
 */
public class RechargeRevokeDTO {

    @ApiModelProperty(name = "orderNo", value = "充值订单号",required = true)
    @NotEmpty
    @Length(max = 28)
    private  String orderNo;

    @ApiModelProperty(name = "chkOrderNo", value = "对账差错处理主键")
    @NotEmpty
    @Length(max = 32)
    private  String chkErrId;

    @ApiModelProperty(name = "对账子类型",value = "chkSubType",required = true)
    private String chkSubType;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getChkErrId() {
        return chkErrId;
    }

    public void setChkErrId(String chkErrId) {
        this.chkErrId = chkErrId;
    }

    public String getChkSubType() {
        return chkSubType;
    }

    public void setChkSubType(String chkSubType) {
        this.chkSubType = chkSubType;
    }
}
