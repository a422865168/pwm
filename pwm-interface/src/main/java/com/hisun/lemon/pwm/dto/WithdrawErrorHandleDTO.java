package com.hisun.lemon.pwm.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import java.time.LocalDate;

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

    @ApiModelProperty(name = "chkOrderNo", value = "对账差错处理主键")
    @NotEmpty
    @Length(max = 32)
    private  String chkErrId;

    @ApiModelProperty(name = "orderStatus", value = "处理状态：(成功:S1|失败:F1)")
    @NotEmpty
    private String orderStatus;

    @ApiModelProperty(name = "chkOrdDt", value = "对账处理时间")
    private LocalDate chkOrdDt;

    @ApiModelProperty(name = "remark", value = "备注信息")
    private String remark;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDate getChkOrdDt() {
        return chkOrdDt;
    }

    public void setChkOrdDt(LocalDate chkOrdDt) {
        this.chkOrdDt = chkOrdDt;
    }

    public String getChkErrId() {
        return chkErrId;
    }

    public void setChkErrId(String chkErrId) {
        this.chkErrId = chkErrId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
