package com.hisun.lemon.pwm.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * 营业厅对账长短款处理请求 结果响应对象
 * @author xian
 * @date 2017年8月24日
 * @time 上午10:27:30
 *
 */
public class HallRechargeFundRspDTO {
    @ApiModelProperty(name = "chkErrId", value = "对账处理订单号")
    @NotEmpty
    @Length(max = 32)
    private  String chkErrId;

    @ApiModelProperty(name = "status", value = "差错处理结果状态(S:成功|F:失败)")
    private String status;

    @ApiModelProperty(name = "orderNo",value = "差错订单号")
    private String orderNo;

    public String getChkErrId() {
        return chkErrId;
    }

    public void setChkErrId(String chkErrId) {
        this.chkErrId = chkErrId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
