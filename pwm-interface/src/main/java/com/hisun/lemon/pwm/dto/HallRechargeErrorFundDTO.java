package com.hisun.lemon.pwm.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import java.time.LocalDate;

/**
 * 营业厅对账长短款处理请求 传输对象
 * @author xian
 * @date 2017年8月24日
 * @time 上午10:27:30
 *
 */
public class HallRechargeErrorFundDTO {

    @ApiModelProperty(name = "orderNo", value = "充值订单号")
    @NotEmpty
    @Length(max = 28)
    private  String orderNo;

    @ApiModelProperty(name = "chkOrderNo", value = "对账差错处理主键")
    @NotEmpty
    @Length(max = 32)
    private  String chkErrId;

    @ApiModelProperty(name = "fundType", value = "处理类型：(长款:L|短款:S)")
    @NotEmpty
    @Length(max = 1)
    private  String fundType;

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


    public String getFundType() {
        return fundType;
    }

    public void setFundType(String fundType) {
        this.fundType = fundType;
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
