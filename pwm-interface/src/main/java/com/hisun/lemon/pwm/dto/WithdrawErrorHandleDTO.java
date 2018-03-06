package com.hisun.lemon.pwm.dto;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import io.swagger.annotations.ApiModelProperty;

/**
 * 提现对账处理请求 传输对象
 * @author leon
 * @date 2017年8月24日
 * @time 上午10:27:30
 *
 */
public class WithdrawErrorHandleDTO {

    @ApiModelProperty(name = "orderNo", value = "提现订单号")
    @NotEmpty
    @Length(max = 28)
    private  String orderNo;

    @ApiModelProperty(name = "chkBusSubTyp", value = "对账业务子类型")
    @NotEmpty
    @Length(max = 4)
    private  String chkBusSubTyp;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getChkBusSubTyp() {
        return chkBusSubTyp;
    }

    public void setChkBusSubTyp(String chkBusSubTyp) {
        this.chkBusSubTyp = chkBusSubTyp;
    }
}
