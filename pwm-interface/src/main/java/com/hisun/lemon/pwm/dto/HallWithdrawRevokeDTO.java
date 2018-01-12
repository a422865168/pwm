package com.hisun.lemon.pwm.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * 个人营业厅长款撤单请求对象
 * Created by XianSky on 2017/11/15.
 */
public class HallWithdrawRevokeDTO {

    @ApiModelProperty(name = "orderNo", value = "提现订单号",required = true)
    @NotEmpty
    @Length(max = 28)
    private  String orderNo;

    @ApiModelProperty(name = "对账子类型",value = "chkSubType",required = true)
    @NotEmpty
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
