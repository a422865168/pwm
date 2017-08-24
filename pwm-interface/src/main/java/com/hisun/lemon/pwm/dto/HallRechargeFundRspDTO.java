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
    @ApiModelProperty(name = "chkOrderNo", value = "对账处理订单号")
    @NotEmpty
    @Length(max = 32)
    private  String chkOrderNo;
}
