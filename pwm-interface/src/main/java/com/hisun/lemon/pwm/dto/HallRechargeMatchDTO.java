package com.hisun.lemon.pwm.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 营业厅充值对平账务处理 传输对象
 * @author xian
 * @date 2017年8月24日
 * @time 上午10:27:30
 *
 */
public class HallRechargeMatchDTO {

    @ApiModelProperty(name = "充值对平总金额",value = "totalAmt",required = true)
    @NotNull(message="PWM10050")
    private BigDecimal totalAmt;

    @ApiModelProperty(name = "对账子类型",value = "chkSubType",required = true)
    private String chkSubType;

    public BigDecimal getTotalAmt() {
        return totalAmt;
    }

    public void setTotalAmt(BigDecimal totalAmt) {
        this.totalAmt = totalAmt;
    }

    public String getChkSubType() {
        return chkSubType;
    }

    public void setChkSubType(String chkSubType) {
        this.chkSubType = chkSubType;
    }
}
